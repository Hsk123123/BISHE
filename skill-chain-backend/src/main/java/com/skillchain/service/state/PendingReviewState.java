package com.skillchain.service.state;

import com.skillchain.entity.Order;
import com.skillchain.enums.OrderStatus;
import com.skillchain.service.OrderService;

public class PendingReviewState implements OrderState {

    @Override
    public void pay(OrderService orderService, Order order) {
        throw new RuntimeException("订单已完成，无需支付");
    }

    @Override
    public void accept(OrderService orderService, Order order) {
        throw new RuntimeException("订单已完成，无需接单");
    }

    @Override
    public void startService(OrderService orderService, Order order) {
        throw new RuntimeException("订单已完成，无法开始服务");
    }

    @Override
    public void complete(OrderService orderService, Order order) {
        orderService.updateOrderStatus(order, OrderStatus.COMPLETED);
    }

    @Override
    public void cancel(OrderService orderService, Order order) {
        throw new RuntimeException("订单已完成，无法取消");
    }

    @Override
    public void refund(OrderService orderService, Order order) {
        orderService.updateOrderStatus(order, OrderStatus.REFUNDING);
    }
}