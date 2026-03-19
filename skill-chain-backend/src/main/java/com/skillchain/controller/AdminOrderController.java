package com.skillchain.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.skillchain.common.Result;
import com.skillchain.entity.Order;
import com.skillchain.service.AdminOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/order")
public class AdminOrderController {

    @Autowired
    private AdminOrderService adminOrderService;

    @GetMapping("/list")
    public Result<Page<Order>> getOrderList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword
    ) {
        Page<Order> pageInfo = adminOrderService.getOrderList(page, size, status, keyword);
        return Result.success(pageInfo);
    }

    @GetMapping("/{orderId}")
    public Result<Order> getOrderDetail(@PathVariable Long orderId) {
        Order order = adminOrderService.getOrderDetail(orderId);
        return Result.success(order);
    }

    @PostMapping("/{orderId}/arbitrate")
    public Result<Void> arbitrateOrder(@PathVariable Long orderId, @RequestBody Map<String, Object> params) {
        Integer result = (Integer) params.get("result");
        String remark = (String) params.get("remark");
        adminOrderService.arbitrateOrder(orderId, result, remark);
        return Result.success("仲裁成功", null);
    }
}