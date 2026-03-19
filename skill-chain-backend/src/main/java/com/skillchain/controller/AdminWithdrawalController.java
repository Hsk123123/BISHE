package com.skillchain.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.skillchain.common.Result;
import com.skillchain.entity.WithdrawalRequest;
import com.skillchain.service.WithdrawalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/withdrawal")
public class AdminWithdrawalController {

    @Autowired
    private WithdrawalService withdrawalService;

    @GetMapping("/list")
    public Result<Page<WithdrawalRequest>> getPendingWithdrawals(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        Page<WithdrawalRequest> pageInfo = withdrawalService.getPendingWithdrawals(page, size);
        return Result.success(pageInfo);
    }

    @PutMapping("/{id}/approve")
    public Result<Void> approveWithdrawal(@PathVariable Long id) {
        withdrawalService.approveWithdrawal(id);
        return Result.success("审核通过", null);
    }

    @PutMapping("/{id}/reject")
    public Result<Void> rejectWithdrawal(@PathVariable Long id) {
        withdrawalService.rejectWithdrawal(id);
        return Result.success("已拒绝", null);
    }
}
