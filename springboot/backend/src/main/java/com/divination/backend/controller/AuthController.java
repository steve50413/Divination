package com.divination.backend.controller;

import com.divination.backend.dto.AuthDTO;
import com.divination.backend.model.User;
import com.divination.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {
    
    private final AuthService authService;
    
    // ✅ 發送驗證碼 API
    @PostMapping("/send-code")
    public ResponseEntity<?> sendVerificationCode(
            @RequestBody AuthDTO.SendCodeRequest request) {
        try {
            authService.sendVerificationCode(request.getEmail());
            
            return ResponseEntity.ok(new AuthDTO.ApiResponse(
                true,
                "驗證碼已發送到您的 Email"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthDTO.ApiResponse(false, e.getMessage()));
        }
    }
    
    // ✅ 註冊 API（有驗證碼）
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody AuthDTO.SignupRequest request) {
        try {
            AuthDTO.LoginResponse response = authService.signup(request);
            
            return ResponseEntity.ok(new AuthDTO.ApiResponse(
                true,
                "註冊成功",
                response
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthDTO.ApiResponse(false, e.getMessage()));
        }
    }
    
    // 註冊 API（無驗證碼，舊版相容）
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthDTO.RegisterRequest request) {
        try {
            User user = authService.register(request);
            return ResponseEntity.ok(new AuthDTO.ApiResponse(
                true,
                "註冊成功",
                user
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthDTO.ApiResponse(false, e.getMessage()));
        }
    }
    
    // 登入 API
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthDTO.LoginRequest request) {
        try {
            AuthDTO.LoginResponse response = authService.login(request);
            return ResponseEntity.ok(new AuthDTO.ApiResponse(
                true,
                "登入成功",
                response
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthDTO.ApiResponse(false, e.getMessage()));
        }
    }
    
    // 取得當前用戶資訊 API
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            User user = authService.getUserFromToken(token);
            return ResponseEntity.ok(new AuthDTO.ApiResponse(
                true,
                "成功",
                user
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthDTO.ApiResponse(false, e.getMessage()));
        }
    }
    
    // ✅ 忘記密碼 - 發送驗證碼
    @PostMapping("/forgot-password/send-code")
    public ResponseEntity<?> sendPasswordResetCode(
            @RequestBody AuthDTO.SendCodeRequest request) {
        try {
            authService.sendPasswordResetCode(request.getEmail());
            
            return ResponseEntity.ok(new AuthDTO.ApiResponse(
                true,
                "驗證碼已發送到您的 Email"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthDTO.ApiResponse(false, e.getMessage()));
        }
    }
    
    // ✅ 忘記密碼 - 重設密碼
    @PostMapping("/forgot-password/reset")
    public ResponseEntity<?> resetPassword(
            @RequestBody AuthDTO.ResetPasswordRequest request) {
        try {
            authService.resetPassword(
                request.getEmail(),
                request.getVerificationCode(),
                request.getNewPassword()
            );
            
            return ResponseEntity.ok(new AuthDTO.ApiResponse(
                true,
                "密碼重設成功"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthDTO.ApiResponse(false, e.getMessage()));
        }
    }
}