package com.divination.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .sessionManagement(session -> 
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            // 前端靜態資源
            .requestMatchers("/", "/index.html", "/assets/**", "/favicon.ico", "/images/**").permitAll()
            // API 端點 - 開放占卜功能（免登入）
            .requestMatchers("/api/auth/**").permitAll()
            .requestMatchers("/api/divination/**").permitAll()
            .requestMatchers("/api/daily/**").permitAll()
            .requestMatchers("/api/ai/**").permitAll()
            .requestMatchers("/api/articles/list").permitAll()
            .requestMatchers("/api/articles/{id}").permitAll()
            // 需要登入的功能
            .requestMatchers("/api/articles/create").authenticated()
            .requestMatchers("/api/articles/edit/**").authenticated()
            .requestMatchers("/api/articles/delete/**").authenticated()
           .requestMatchers("/api/admin/**").permitAll()      // ← 暫時改這樣
            .requestMatchers("/api/statistics/**").permitAll()
            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
            .anyRequest().authenticated()
        );
    
    return http.build();
}
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://localhost:5174", "http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}