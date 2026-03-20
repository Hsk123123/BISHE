package com.skillchain.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.skillchain.common.Result;
import com.skillchain.entity.User;
import com.skillchain.service.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/user")
public class AdminUserController {

    @Autowired
    private AdminUserService adminUserService;

    @GetMapping("/list")
    public Result<Page<User>> getUserList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer role
    ) {
        Page<User> pageInfo = adminUserService.getUserList(page, size, keyword, role);
        return Result.success(pageInfo);
    }

    @PostMapping("/create")
    public Result<Void> createUser(@RequestBody Map<String, Object> params) {
        String username = params.get("username") == null ? null : params.get("username").toString();
        String password = params.get("password") == null ? null : params.get("password").toString();
        String phone = params.get("phone") == null ? null : params.get("phone").toString();
        String email = params.get("email") == null ? null : params.get("email").toString();
        Integer role = params.get("role") == null ? 0 : Integer.parseInt(params.get("role").toString());
        Integer status = params.get("status") == null ? 1 : Integer.parseInt(params.get("status").toString());
        adminUserService.createUser(username, password, phone, email, role, status);
        return Result.success("创建成功", null);
    }

    @PutMapping("/{userId}/role")
    public Result<Void> updateUserRole(@PathVariable Long userId, @RequestBody Map<String, Object> params) {
        Integer role = (Integer) params.get("role");
        adminUserService.updateUserRole(userId, role);
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/{userId}")
    public Result<Void> deleteUser(@PathVariable Long userId) {
        adminUserService.deleteUser(userId);
        return Result.success("删除成功", null);
    }

    @PutMapping("/{userId}/approve-realname")
    public Result<Void> approveRealName(@PathVariable Long userId) {
        adminUserService.approveRealName(userId);
        return Result.success("审核通过", null);
    }

    @PutMapping("/{userId}/reject-realname")
    public Result<Void> rejectRealName(@PathVariable Long userId) {
        adminUserService.rejectRealName(userId);
        return Result.success("审核拒绝", null);
    }
}
