package com.skillchain.controller;

import com.skillchain.common.Result;
import com.skillchain.dto.LoginDTO;
import com.skillchain.dto.RegisterDTO;
import com.skillchain.entity.User;
import com.skillchain.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginDTO loginDTO) {
        try {
            String token = authService.login(loginDTO);
            User user = authService.getUserInfo(jwtUtil.getUserIdFromToken(token));

            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", user);

            return Result.success("登录成功", data);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/register")
    public Result<Void> register(@RequestBody RegisterDTO registerDTO) {
        try {
            authService.register(registerDTO);
            return Result.success("注册成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/info")
    public Result<User> getUserInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        User user = authService.getUserInfo(userId);
        return Result.success(user);
    }

    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestBody User user, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        authService.updateProfile(userId, user);
        return Result.success("更新成功", null);
    }

    @Autowired
    private com.skillchain.utils.JwtUtil jwtUtil;
}