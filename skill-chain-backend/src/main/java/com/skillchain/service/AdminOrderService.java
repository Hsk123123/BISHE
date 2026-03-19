package com.skillchain.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.skillchain.entity.Order;
import com.skillchain.enums.OrderStatus;
import com.skillchain.mapper.OrderMapper;
import com.skillchain.service.state.OrderState;
import com.skillchain.service.state.OrderStateFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminOrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private WalletService walletService;

    public Page<Order> getOrderList(Integer page, Integer size, Integer status, String keyword) {
        Page<Order> pageInfo = new Page<>(page, size);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();

        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }

        if (keyword != null && !keyword.isEmpty()) {
            if (keyword.matches("\\d+")) {
                wrapper.eq(Order::getOrderId, Long.parseLong(keyword));
            } else {
                // Invalid order id keyword should not trigger server error.
                wrapper.eq(Order::getOrderId, -1L);
            }
        }

        wrapper.orderByDesc(Order::getCreateTime);

        return orderMapper.selectPage(pageInfo, wrapper);
    }

    @Transactional
    public void arbitrateOrder(Long orderId, Integer result, String remark) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        if (order.getStatus() != OrderStatus.REFUNDING.getCode()) {
            throw new RuntimeException("订单不在退款状态");
        }

        if (result == 1) {
            walletService.recharge(order.getBuyerId(), order.getAmount());
        } else if (result == 2) {
            walletService.recharge(order.getProviderId(), order.getAmount());
        }

        order.setStatus(OrderStatus.REFUNDED.getCode());
        orderMapper.updateById(order);
    }

    public Order getOrderDetail(Long orderId) {
        return orderMapper.selectById(orderId);
    }
}
