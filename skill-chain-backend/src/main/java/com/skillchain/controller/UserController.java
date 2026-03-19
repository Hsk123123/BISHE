package com.skillchain.controller;

import com.skillchain.entity.User;
import com.skillchain.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(@RequestAttribute("userId") Long userId) {
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/profile")
    public ResponseEntity<Void> updateProfile(@RequestAttribute("userId") Long userId, @RequestBody User user) {
        user.setUserId(userId);
        userService.updateUser(user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/real-name/verify")
    public ResponseEntity<Void> submitRealNameVerification(
            @RequestAttribute("userId") Long userId,
            @RequestParam String idCardFront,
            @RequestParam String idCardBack) {
        userService.submitRealNameVerification(userId, idCardFront, idCardBack);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/role/switch")
    public ResponseEntity<Void> switchRole(
            @RequestAttribute("userId") Long userId,
            @RequestParam Integer newRole) {
        userService.switchRole(userId, newRole);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/role/apply-worker")
    public ResponseEntity<Void> applyForWorkerRole(@RequestAttribute("userId") Long userId) {
        userService.applyForWorkerRole(userId);
        return ResponseEntity.ok().build();
    }
}
