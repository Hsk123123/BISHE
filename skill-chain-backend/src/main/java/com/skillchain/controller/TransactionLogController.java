package com.skillchain.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.skillchain.common.Result;
import com.skillchain.entity.TransactionLog;
import com.skillchain.service.TransactionLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transaction")
public class TransactionLogController {

    @Autowired
    private TransactionLogService transactionLogService;

    @GetMapping("/list")
    public Result<Page<TransactionLog>> getTransactionLogs(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            HttpServletRequest request
    ) {
        Long userId = (Long) request.getAttribute("userId");
        Page<TransactionLog> pageInfo = transactionLogService.getTransactionLogs(userId, page, size);
        return Result.success(pageInfo);
    }
}
