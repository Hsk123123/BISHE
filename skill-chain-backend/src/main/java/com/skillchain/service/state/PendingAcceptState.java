package com.skillchain.service.state;

import com.skillchain.entity.Order;
import com.skillchain.enums.OrderStatus;
import com.skillchain.service.OrderService;

public class PendingAcceptState implements OrderState {

    @Override
    public void pay(OrderService orderService, Order order) {
        throw new RuntimeException("订单已支付，无需重复支付");
    }

    @Override
    public void accept(OrderService orderService, Order order) {
        orderService.updateOrderStatus(order, OrderStatus.ACCEPTED);
    }

    @Override
    public void startService(OrderService orderService, Order order) {
        throw new RuntimeException("订单未接单，无法开始服务");
    }

    @Override
    public void complete(OrderService orderService, Order order) {
        throw new RuntimeException("订单未接单，无法完成");
    }

    @Override
    public void cancel(OrderService orderService, Order order) {
        orderService.updateOrderStatus(order, OrderStatus.REFUNDING);
    }

    @Override
    public void refund(OrderService orderService, Order order) {
        orderService.updateOrderStatus(order, OrderStatus.REFUNDED);
    }
}