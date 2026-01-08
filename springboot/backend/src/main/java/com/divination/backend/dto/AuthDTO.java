package com.divination.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class AuthDTO {
    
    // 註冊請求
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterRequest {
        private String email;
        private String nickname;
        private String password;
    }

    // 登入請求
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {
        private String email;
        private String password;
    }

    // 登入回應（包含 token）
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginResponse {
        private String token;
        private Long userId;
        private String nickname;
        private String email;
        private String role;
    }

    // 通用回應
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApiResponse {
        private boolean success;
        private String message;
        private Object data;
        
        public ApiResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }

    // 加入到 AuthDTO.java 中

    // 加入到 AuthDTO.java 中

// 發送驗證碼請求
@Data
@NoArgsConstructor
@AllArgsConstructor
public static class SendCodeRequest {
    private String email;
}

// 重設密碼請求
@Data
@NoArgsConstructor
@AllArgsConstructor
public static class ResetPasswordRequest {
    private String email;
    private String verificationCode;
    private String newPassword;
}

// 註冊請求（新增 verificationCode 欄位）
@Data
@NoArgsConstructor
@AllArgsConstructor
public static class SignupRequest {
    private String email;
    private String nickname;
    private String password;
    private String verificationCode;  // ← 加這個
}


}