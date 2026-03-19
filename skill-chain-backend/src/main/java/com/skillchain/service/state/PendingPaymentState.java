package com.skillchain.service.state;

import com.skillchain.entity.Order;
import com.skillchain.enums.OrderStatus;
import com.skillchain.service.OrderService;

public class PendingPaymentState implements OrderState {

    @Override
    public void pay(OrderService orderService, Order order) {
        orderService.updateOrderStatus(order, OrderStatus.PENDING_ACCEPT);
    }

    @Override
    public void accept(OrderService orderService, Order order) {
        throw new RuntimeException("订单未支付，无法接单");
    }

    @Override
    public void startService(OrderService orderService, Order order) {
        throw new RuntimeException("订单未支付，无法开始服务");
    }

    @Override
    public void complete(OrderService orderService, Order order) {
        throw new RuntimeException("订单未支付，无法完成");
    }

    @Override
    public void cancel(OrderService orderService, Order order) {
        orderService.updateOrderStatus(order, OrderStatus.CANCELLED);
    }

    @Override
    public void refund(OrderService orderService, Order order) {
        throw new RuntimeException("订单未支付，无法退款");
    }
}