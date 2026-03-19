package com.skillchain.service.state;

import com.skillchain.entity.Order;
import com.skillchain.enums.OrderStatus;
import com.skillchain.service.OrderService;

public class InServiceState implements OrderState {

    @Override
    public void pay(OrderService orderService, Order order) {
        throw new RuntimeException("订单已支付，无需重复支付");
    }

    @Override
    public void accept(OrderService orderService, Order order) {
        throw new RuntimeException("订单已接单，无需重复接单");
    }

    @Override
    public void startService(OrderService orderService, Order order) {
        throw new RuntimeException("订单已在服务中");
    }

    @Override
    public void complete(OrderService orderService, Order order) {
        orderService.updateOrderStatus(order, OrderStatus.PENDING_REVIEW);
    }

    @Override
    public void cancel(OrderService orderService, Order order) {
        throw new RuntimeException("服务中无法取消订单");
    }

    @Override
    public void refund(OrderService orderService, Order order) {
        orderService.updateOrderStatus(order, OrderStatus.REFUNDING);
    }
}