package com.skillchain.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.skillchain.common.Result;
import com.skillchain.entity.WithdrawalRequest;
import com.skillchain.service.WithdrawalService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/withdrawal")
public class WithdrawalController {

    @Autowired
    private WithdrawalService withdrawalService;

    @PostMapping("/apply")
    public Result<WithdrawalRequest> applyWithdrawal(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        BigDecimal amount = new BigDecimal(params.get("amount").toString());
        String bankName = params.getOrDefault("bankName", "银行卡").toString();
        String bankCard = params.getOrDefault("bankCard", "").toString();
        WithdrawalRequest wr = withdrawalService.createWithdrawal(userId, amount, bankName, bankCard);
        return Result.success("提现申请已提交", wr);
    }

    @GetMapping("/list")
    public Result<Page<WithdrawalRequest>> getMyWithdrawals(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            HttpServletRequest request
    ) {
        Long userId = (Long) request.getAttribute("userId");
        Page<WithdrawalRequest> pageInfo = withdrawalService.getMyWithdrawals(userId, page, size);
        return Result.success(pageInfo);
    }

    @GetMapping("/earnings")
    public Result<Map<String, Object>> getEarningsStats(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Map<String, Object> stats = withdrawalService.getEarningsStats(userId);
        return Result.success(stats);
    }
}
