package org.example.apitest.controller;

import jakarta.validation.Valid;
import org.example.apitest.dto.*;
import org.example.apitest.model.User;
import org.example.apitest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class    AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerUser(@Valid @RequestBody RegisterRequest request,
                                                    BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                Map<String, String> errors = new HashMap<>();
                bindingResult.getFieldErrors().forEach(error ->
                        errors.put(error.getField(), error.getDefaultMessage())
                );
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "Dữ liệu không hợp lệ", errors));
            }

            User user = userService.registerUser(request);

            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getId());
            userData.put("username", user.getUsername());
            userData.put("email", user.getEmail());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(true, "Đăng ký thành công", userData));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Lỗi hệ thống"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> loginUser(@Valid @RequestBody LoginRequest request,
                                                 BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                Map<String, String> errors = new HashMap<>();
                bindingResult.getFieldErrors().forEach(error ->
                        errors.put(error.getField(), error.getDefaultMessage())
                );
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "Dữ liệu không hợp lệ", errors));
            }

            User user = userService.loginUser(request);

            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getId());
            userData.put("username", user.getUsername());
            userData.put("email", user.getEmail());

            return ResponseEntity.ok()
                    .body(new ApiResponse(true, "Đăng nhập thành công", userData));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Lỗi hệ thống"));
        }
    }

    @PutMapping("/profile/{id}")
    public ResponseEntity<ApiResponse> updateProfile(@PathVariable Long id,
                                                     @Valid @RequestBody UpdateProfileRequest request,
                                                     BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                Map<String, String> errors = new HashMap<>();
                bindingResult.getFieldErrors().forEach(error ->
                        errors.put(error.getField(), error.getDefaultMessage())
                );
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "Dữ liệu không hợp lệ", errors));
            }

            User user = userService.updateProfile(id, request);

            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getId());
            userData.put("username", user.getUsername());
            userData.put("email", user.getEmail());

            return ResponseEntity.ok()
                    .body(new ApiResponse(true, "Cập nhật profile thành công", userData));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Lỗi hệ thống"));
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse> changePassword(@Valid @RequestBody ChangePasswordRequest request,
                                                      BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                Map<String, String> errors = new HashMap<>();
                bindingResult.getFieldErrors().forEach(error ->
                        errors.put(error.getField(), error.getDefaultMessage())
                );
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "Dữ liệu không hợp lệ", errors));
            }

            userService.changePassword(request);

            return ResponseEntity.ok()
                    .body(new ApiResponse(true, "Đổi mật khẩu thành công"));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Lỗi hệ thống"));
        }
    }



    @GetMapping("/profile/{id}")
    public ResponseEntity<ApiResponse> getProfile(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);

            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getId());
            userData.put("username", user.getUsername());
            userData.put("email", user.getEmail());

            return ResponseEntity.ok()
                    .body(new ApiResponse(true, "Lấy thông tin profile thành công", userData));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Lỗi hệ thống"));
        }
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);

            return ResponseEntity.ok()
                    .body(new ApiResponse(true, "Xóa user thành công"));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Lỗi hệ thống"));
        }
    }



    @GetMapping("/users")
    public ResponseEntity<ApiResponse> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();

            return ResponseEntity.ok()
                    .body(new ApiResponse(true, "Lấy danh sách user thành công", users));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Lỗi hệ thống"));
        }
    }
}
