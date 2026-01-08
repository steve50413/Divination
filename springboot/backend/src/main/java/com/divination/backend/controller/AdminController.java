package com.divination.backend.controller;

import com.divination.backend.dto.AdminDTO;
import com.divination.backend.dto.AuthDTO;
import com.divination.backend.model.User;
import com.divination.backend.repository.UserRepository;
import com.divination.backend.security.JwtUtil;
import com.divination.backend.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminController {
    
    private final AdminService adminService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    
    // 檢查是否為管理員
    private boolean isAdmin(String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        User user = userRepository.findById(userId).orElse(null);
        return user != null && "admin".equals(user.getRole());
    }
    
    // ========== 文殊籤詩管理 ==========
    
    @PostMapping("/poems/manju")
    public ResponseEntity<?> saveManjuPoem(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody AdminDTO.PoemRequest request) {
        try {
            String token = authHeader.replace("Bearer ", "");
            
            if (!isAdmin(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new AuthDTO.ApiResponse(false, "無管理員權限"));
            }
            
            adminService.saveManjuPoem(request);
            
            return ResponseEntity.ok(new AuthDTO.ApiResponse(
                true,
                request.getId() != null ? "更新成功" : "新增成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthDTO.ApiResponse(false, e.getMessage()));
        }
    }
    
    @DeleteMapping("/poems/manju/{id}")
    public ResponseEntity<?> deleteManjuPoem(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {
        try {
            String token = authHeader.replace("Bearer ", "");
            
            if (!isAdmin(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new AuthDTO.ApiResponse(false, "無管理員權限"));
            }
            
            adminService.deleteManjuPoem(id);
            
            return ResponseEntity.ok(new AuthDTO.ApiResponse(true, "刪除成功"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthDTO.ApiResponse(false, e.getMessage()));
        }
    }
    
    @GetMapping("/poems/manju/{id}")
    public ResponseEntity<?> getManjuPoem(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {
        try {
            String token = authHeader.replace("Bearer ", "");
            
            if (!isAdmin(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new AuthDTO.ApiResponse(false, "無管理員權限"));
            }
            
            var poem = adminService.getManjuPoem(id);
            
            return ResponseEntity.ok(new AuthDTO.ApiResponse(true, "查詢成功", poem));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthDTO.ApiResponse(false, e.getMessage()));
        }
    }
    
    // ========== 觀音籤詩管理 ==========
    
    @PostMapping("/poems/avalo")
    public ResponseEntity<?> saveAvaloPoem(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody AdminDTO.PoemRequest request) {
        try {
            String token = authHeader.replace("Bearer ", "");
            
            if (!isAdmin(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new AuthDTO.ApiResponse(false, "無管理員權限"));
            }
            
            adminService.saveAvaloPoem(request);
            
            return ResponseEntity.ok(new AuthDTO.ApiResponse(
                true,
                request.getId() != null ? "更新成功" : "新增成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthDTO.ApiResponse(false, e.getMessage()));
        }
    }
    
    @DeleteMapping("/poems/avalo/{id}")
    public ResponseEntity<?> deleteAvaloPoem(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {
        try {
            String token = authHeader.replace("Bearer ", "");
            
            if (!isAdmin(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new AuthDTO.ApiResponse(false, "無管理員權限"));
            }
            
            adminService.deleteAvaloPoem(id);
            
            return ResponseEntity.ok(new AuthDTO.ApiResponse(true, "刪除成功"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthDTO.ApiResponse(false, e.getMessage()));
        }
    }
    
    @GetMapping("/poems/avalo/{id}")
    public ResponseEntity<?> getAvaloPoem(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {
        try {
            String token = authHeader.replace("Bearer ", "");
            
            if (!isAdmin(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new AuthDTO.ApiResponse(false, "無管理員權限"));
            }
            
            var poem = adminService.getAvaloPoem(id);
            
            return ResponseEntity.ok(new AuthDTO.ApiResponse(true, "查詢成功", poem));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthDTO.ApiResponse(false, e.getMessage()));
        }
    }
    
    // ========== 日本籤詩管理 ==========
    
    @PostMapping("/poems/japan")
    public ResponseEntity<?> saveJapanPoem(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody AdminDTO.PoemRequest request) {
        try {
            String token = authHeader.replace("Bearer ", "");
            
            if (!isAdmin(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new AuthDTO.ApiResponse(false, "無管理員權限"));
            }
            
            adminService.saveJapanPoem(request);
            
            return ResponseEntity.ok(new AuthDTO.ApiResponse(
                true,
                request.getId() != null ? "更新成功" : "新增成功"
                

            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthDTO.ApiResponse(false, e.getMessage()));
        }
    }
    
    @DeleteMapping("/poems/japan/{id}")
    public ResponseEntity<?> deleteJapanPoem(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {
        try {
            String token = authHeader.replace("Bearer ", "");
            
            if (!isAdmin(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new AuthDTO.ApiResponse(false, "無管理員權限"));
            }
            
            adminService.deleteJapanPoem(id);
            
            return ResponseEntity.ok(new AuthDTO.ApiResponse(true, "刪除成功"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthDTO.ApiResponse(false, e.getMessage()));
        }
    }
    
    @GetMapping("/poems/japan/{id}")
    public ResponseEntity<?> getJapanPoem(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {
        try {
            String token = authHeader.replace("Bearer ", "");
            
            if (!isAdmin(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new AuthDTO.ApiResponse(false, "無管理員權限"));
            }
            
            var poem = adminService.getJapanPoem(id);
            
            return ResponseEntity.ok(new AuthDTO.ApiResponse(true, "查詢成功", poem));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthDTO.ApiResponse(false, e.getMessage()));
        }
    }
}
