package com.skillchain.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.skillchain.common.Result;
import com.skillchain.entity.User;
import com.skillchain.service.AdminCertificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/certification")
public class AdminCertificationController {

    @Autowired
    private AdminCertificationService adminCertificationService;

    @GetMapping("/list")
    public Result<Page<User>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status
    ) {
        return Result.success(adminCertificationService.getCertificationList(page, size, status));
    }

    @PutMapping("/{userId}/approve")
    public Result<Void> approve(@PathVariable Long userId) {
        adminCertificationService.approve(userId);
        return Result.success("审核通过", null);
    }

    @PutMapping("/{userId}/reject")
    public Result<Void> reject(@PathVariable Long userId) {
        adminCertificationService.reject(userId);
        return Result.success("审核拒绝", null);
    }
}
