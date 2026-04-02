package com.skillchain.controller;

import com.skillchain.common.Result;
import com.skillchain.service.AdminFinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/admin/finance")
public class AdminFinanceController {

    @Autowired
    private AdminFinanceService adminFinanceService;

    @GetMapping("/overview")
    public Result<Map<String, Object>> overview() {
        return Result.success(adminFinanceService.getOverview());
    }

    @GetMapping("/transactions")
    public Result<Map<String, Object>> transactions(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {
        LocalDate start = (startDate == null || startDate.isEmpty()) ? null : LocalDate.parse(startDate);
        LocalDate end = (endDate == null || endDate.isEmpty()) ? null : LocalDate.parse(endDate);
        return Result.success(adminFinanceService.getTransactions(page, size, type, start, end));
    }
}

