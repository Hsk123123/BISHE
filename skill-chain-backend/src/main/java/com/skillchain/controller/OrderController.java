package com.skillchain.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.skillchain.common.Result;
import com.skillchain.entity.Order;
import com.skillchain.service.OrderService;
import com.skillchain.vo.OrderVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/list")
    public Result<Page<OrderVO>> getOrderList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status,
            HttpServletRequest request
    ) {
        Long userId = (Long) request.getAttribute("userId");
        Page<OrderVO> pageInfo = orderService.getOrderVOList(userId, status, page, size);
        return Result.success(pageInfo);
    }

    @GetMapping("/provider/list")
    public Result<Page<OrderVO>> getProviderOrderList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status,
            HttpServletRequest request
    ) {
        Long providerId = (Long) request.getAttribute("userId");
        Page<OrderVO> pageInfo = orderService.getProviderOrderVOList(providerId, status, page, size);
        return Result.success(pageInfo);
    }

    @GetMapping("/{id}")
    public Result<OrderVO> getOrderDetail(@PathVariable Long id) {
        OrderVO order = orderService.getOrderVOById(id);
        return Result.success(order);
    }

    @PostMapping("/create")
    public Result<Order> createOrder(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Long buyerId = (Long) request.getAttribute("userId");
        Long skillId = Long.parseLong(params.get("skillId").toString());
        String scheduleDate = (String) params.get("scheduleDate");
        String timeSlot = (String) params.get("timeSlot");

        Order order = orderService.createOrder(buyerId, skillId, scheduleDate, timeSlot);
        return Result.success("创建订单成功", order);
    }

    @PostMapping("/{id}/pay")
    public Result<Void> payOrder(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Order order = orderService.getOrderById(id);
        if (!order.getBuyerId().equals(userId)) {
            return Result.error("无权操作");
        }
        orderService.payOrder(id);
        return Result.success("支付成功", null);
    }

    @PostMapping("/{id}/accept")
    public Result<Void> acceptOrder(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        orderService.acceptOrder(userId, id);
        return Result.success("接单成功", null);
    }

    @PostMapping("/{id}/start")
    public Result<Void> startService(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        orderService.startService(userId, id);
        return Result.success("开始服务", null);
    }

    @PostMapping("/{id}/complete")
    public Result<Void> completeOrder(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        orderService.completeOrder(userId, id);
        return Result.success("完成服务", null);
    }

    @PostMapping("/{id}/cancel")
    public Result<Void> cancelOrder(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        orderService.cancelOrder(userId, id);
        return Result.success("取消订单成功", null);
    }

    @PostMapping("/{id}/refund")
    public Result<Void> refundOrder(@PathVariable Long id) {
        orderService.refundOrder(id);
        return Result.success("退款成功", null);
    }
}