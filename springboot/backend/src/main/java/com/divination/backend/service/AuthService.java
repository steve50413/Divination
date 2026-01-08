package com.divination.backend.service;

import com.divination.backend.dto.AuthDTO;
import com.divination.backend.model.User;
import com.divination.backend.model.VerificationCode;
import com.divination.backend.repository.UserRepository;
import com.divination.backend.repository.VerificationCodeRepository;
import com.divination.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final VerificationCodeRepository verificationCodeRepository;
    private final EmailService emailService;
    
    // 登入方法（原本就有的）
    public AuthDTO.LoginResponse login(AuthDTO.LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email 或密碼錯誤"));
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Email 或密碼錯誤");
        }
        
        String token = jwtUtil.generateToken(user.getEmail(), user.getId());  // ← 修正
        
        return new AuthDTO.LoginResponse(
            token,
            user.getId(),
            user.getNickname(),
            user.getEmail(),
            user.getRole()
        );
    }
    
    // ✅ 新增：發送驗證碼
    public void sendVerificationCode(String email) {
        // 檢查 Email 是否已註冊
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("此 Email 已被註冊");
        }
        
        // 生成 6 位數驗證碼
        String code = String.format("%06d", new Random().nextInt(1000000));
        
        // 儲存驗證碼
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setEmail(email);
        verificationCode.setCode(code);
        verificationCodeRepository.save(verificationCode);
        
        // 發送 Email
        try {
            emailService.sendVerificationCode(email, code);
        } catch (Exception e) {
            throw new RuntimeException("發送驗證碼失敗，請檢查 Email 是否正確");
        }
    }
    
    // ✅ 新增：驗證驗證碼
    private boolean verifyCode(String email, String code) {
        VerificationCode verification = verificationCodeRepository
                .findTopByEmailOrderByCreatedAtDesc(email)
                .orElseThrow(() -> new RuntimeException("驗證碼不存在"));
        
        // 檢查是否過期
        if (verification.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("驗證碼已過期");
        }
        
        // 檢查是否已使用
        if (verification.getIsUsed()) {
            throw new RuntimeException("驗證碼已使用");
        }
        
        // 檢查驗證碼是否正確
        if (!verification.getCode().equals(code)) {
            throw new RuntimeException("驗證碼錯誤");
        }
        
        // 標記為已使用
        verification.setIsUsed(true);
        verificationCodeRepository.save(verification);
        
        return true;
    }
    
    // ✅ 修改：註冊方法（加入驗證碼）
    public AuthDTO.LoginResponse signup(AuthDTO.SignupRequest request) {
        // 先驗證驗證碼
        verifyCode(request.getEmail(), request.getVerificationCode());
        
        // 檢查 Email 是否已註冊
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("此 Email 已被註冊");
        }
        
        // 建立用戶
        User user = new User();
        user.setEmail(request.getEmail());
        user.setNickname(request.getNickname());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("user");  // 預設角色
        
        user = userRepository.save(user);
        
        // 生成 token
        String token = jwtUtil.generateToken(user.getEmail(), user.getId());  // ← 修正
        
        return new AuthDTO.LoginResponse(
            token,
            user.getId(),
            user.getNickname(),
            user.getEmail(),
            user.getRole()
        );
    }
    
    // ✅ 新增：register 方法（與 signup 相同，為了相容舊的 Controller）
    public User register(AuthDTO.RegisterRequest request) {
        // 檢查 Email 是否已註冊
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("此 Email 已被註冊");
        }
        
        // 建立用戶
        User user = new User();
        user.setEmail(request.getEmail());
        user.setNickname(request.getNickname());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("user");
        
        return userRepository.save(user);
    }
    
    // ✅ 新增：從 token 取得用戶資訊
    public User getUserFromToken(String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用戶不存在"));
    }
    
    // ✅ 新增：忘記密碼 - 發送驗證碼
    public void sendPasswordResetCode(String email) {
        // 檢查用戶是否存在
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("此 Email 未註冊"));
        
        // 生成 6 位數驗證碼
        String code = String.format("%06d", new Random().nextInt(1000000));
        
        // 儲存驗證碼
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setEmail(email);
        verificationCode.setCode(code);
        verificationCodeRepository.save(verificationCode);
        
        // 發送 Email
        try {
            emailService.sendPasswordResetCode(email, code);
        } catch (Exception e) {
            throw new RuntimeException("發送驗證碼失敗，請檢查 Email 是否正確");
        }
    }
    
    // ✅ 新增：忘記密碼 - 重設密碼
    public void resetPassword(String email, String code, String newPassword) {
        // 驗證驗證碼
        verifyCode(email, code);
        
        // 查找用戶
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("用戶不存在"));
        
        // 更新密碼
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}