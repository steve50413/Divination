package com.divination.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    
    private final JavaMailSender mailSender;
    
    public void sendVerificationCode(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@divination.com");
        message.setTo(toEmail);
        message.setSubject("占卜系統 - 註冊驗證碼");
        message.setText(
            "您好！\n\n" +
            "您的註冊驗證碼為：" + code + "\n\n" +
            "此驗證碼將在 5 分鐘後失效。\n\n" +
            "如果這不是您的操作，請忽略此郵件。\n\n" +
            "占卜系統團隊"
        );
        
        mailSender.send(message);
    }
    
    // ✅ 新增：發送密碼重設驗證碼
    public void sendPasswordResetCode(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@divination.com");
        message.setTo(toEmail);
        message.setSubject("占卜系統 - 密碼重設驗證碼");
        message.setText(
            "您好！\n\n" +
            "您正在進行密碼重設。\n\n" +
            "您的驗證碼為：" + code + "\n\n" +
            "此驗證碼將在 5 分鐘後失效。\n\n" +
            "如果這不是您的操作，請立即登入帳號並修改密碼。\n\n" +
            "占卜系統團隊"
        );
        
        mailSender.send(message);
    }
}