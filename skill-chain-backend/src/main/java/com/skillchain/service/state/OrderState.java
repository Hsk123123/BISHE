package com.skillchain.service.state;

import com.skillchain.entity.Order;
import com.skillchain.enums.OrderStatus;
import com.skillchain.service.OrderService;

public interface OrderState {
    void pay(OrderService orderService, Order order);
    void accept(OrderService orderService, Order order);
    void startService(OrderService orderService, Order order);
    void complete(OrderService orderService, Order order);
    void cancel(OrderService orderService, Order order);
    void refund(OrderService orderService, Order order);
}