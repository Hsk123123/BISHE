package com.skillchain.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.skillchain.entity.User;
import com.skillchain.service.AdminUserService;
import com.skillchain.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public ResponseEntity<Page<User>> getUserList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer role) {
        return ResponseEntity.ok(adminUserService.getUserList(page, size, keyword, role));
    }

    @PutMapping("/users/{userId}/role")
    public ResponseEntity<Void> updateUserRole(
            @PathVariable Long userId,
            @RequestParam Integer role) {
        adminUserService.updateUserRole(userId, role);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        adminUserService.deleteUser(userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/users/{userId}/real-name/approve")
    public ResponseEntity<Void> approveRealName(@PathVariable Long userId) {
        adminUserService.approveRealName(userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/users/{userId}/real-name/reject")
    public ResponseEntity<Void> rejectRealName(@PathVariable Long userId) {
        adminUserService.rejectRealName(userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<User> getUserDetail(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }
}
