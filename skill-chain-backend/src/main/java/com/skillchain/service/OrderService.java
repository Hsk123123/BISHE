package com.skillchain.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.skillchain.entity.Order;
import com.skillchain.entity.Skill;
import com.skillchain.aspect.TaskTrigger;
import com.skillchain.enums.OrderStatus;
import com.skillchain.exception.BusinessException;
import com.skillchain.mapper.OrderMapper;
import com.skillchain.service.state.OrderState;
import com.skillchain.service.state.OrderStateFactory;
import com.skillchain.vo.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private SkillService skillService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private TransactionLogService transactionLogService;

    public Page<Order> getOrderList(Long userId, Integer status, Integer page, Integer size) {
        Page<Order> pageInfo = new Page<>(page, size);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(Order::getBuyerId, userId);

        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }

        wrapper.orderByDesc(Order::getCreateTime);

        return orderMapper.selectPage(pageInfo, wrapper);
    }

    public Page<OrderVO> getOrderVOList(Long userId, Integer status, Integer page, Integer size) {
        Page<OrderVO> pageInfo = new Page<>(page, size);
        return orderMapper.selectOrderVOList(pageInfo, userId, status);
    }

    public Page<OrderVO> getProviderOrderVOList(Long providerId, Integer status, Integer page, Integer size) {
        Page<OrderVO> pageInfo = new Page<>(page, size);
        return orderMapper.selectProviderOrderVOList(pageInfo, providerId, status);
    }

    public Order getOrderById(Long orderId) {
        return orderMapper.selectById(orderId);
    }

    public OrderVO getOrderVOById(Long orderId) {
        return orderMapper.selectOrderVOById(orderId);
    }

    @Transactional
    public Order createOrder(Long buyerId, Long skillId, String scheduleDate, String timeSlot) {
        Skill skill = skillService.getSkillById(skillId);
        if (skill == null) {
            throw new BusinessException("技能不存在");
        }

        com.skillchain.entity.Schedule schedule = scheduleService.findByProviderDateSlot(
                skill.getProviderId(), scheduleDate, timeSlot);
        if (schedule == null) {
            throw new BusinessException("该时间段不可预约，请选择其他时间");
        }
        Long scheduleId = schedule.getScheduleId();

        boolean lockSuccess = scheduleService.preLockSchedule(scheduleId);
        if (!lockSuccess) {
            throw new BusinessException("该时间段已被预约，请选择其他时间");
        }

        Order order = new Order();
        order.setBuyerId(buyerId);
        order.setSkillId(skillId);
        order.setProviderId(skill.getProviderId());
        order.setAmount(skill.getPricePerUnit());
        order.setCurrencyType(1);
        order.setStatus(OrderStatus.PENDING_PAYMENT.getCode());
        order.setScheduleDate(scheduleDate);
        order.setTimeSlot(timeSlot);
        order.setVerificationCode(generateVerificationCode());
        order.setScheduleId(scheduleId);

        orderMapper.insert(order);

        return order;
    }

    @Transactional
    public void payOrder(Long orderId) {
        Order order = getOrderById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        OrderState state = OrderStateFactory.getState(OrderStatus.fromCode(order.getStatus()));
        state.pay(this, order);
    }

    @Transactional
    public void acceptOrder(Long providerId, Long orderId) {
        Order order = getOrderById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getProviderId().equals(providerId)) {
            throw new BusinessException(403, "无权操作");
        }

        OrderState state = OrderStateFactory.getState(OrderStatus.fromCode(order.getStatus()));
        state.accept(this, order);
    }

    @Transactional
    public void startService(Long providerId, Long orderId) {
        Order order = getOrderById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getProviderId().equals(providerId)) {
            throw new BusinessException(403, "无权操作");
        }

        OrderState state = OrderStateFactory.getState(OrderStatus.fromCode(order.getStatus()));
        state.startService(this, order);
    }

    @Transactional
    @TaskTrigger(action = "complete_order")
    public void completeOrder(Long providerId, Long orderId) {
        Order order = getOrderById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getProviderId().equals(providerId)) {
            throw new BusinessException(403, "无权操作");
        }

        OrderState state = OrderStateFactory.getState(OrderStatus.fromCode(order.getStatus()));
        state.complete(this, order);
    }

    @Transactional
    public void cancelOrder(Long buyerId, Long orderId) {
        Order order = getOrderById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getBuyerId().equals(buyerId)) {
            throw new BusinessException(403, "无权操作");
        }

        OrderState state = OrderStateFactory.getState(OrderStatus.fromCode(order.getStatus()));
        state.cancel(this, order);
    }

    @Transactional
    public void refundOrder(Long orderId) {
        Order order = getOrderById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        OrderState state = OrderStateFactory.getState(OrderStatus.fromCode(order.getStatus()));
        state.refund(this, order);
    }

    public void updateOrderStatus(Order order, OrderStatus newStatus) {
        order.setStatus(newStatus.getCode());
        orderMapper.updateById(order);
    }

    @Transactional
    public void finishReview(Long buyerId, Long orderId) {
        Order order = getOrderById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getBuyerId().equals(buyerId)) {
            throw new BusinessException(403, "无权操作");
        }
        if (order.getStatus() != OrderStatus.PENDING_REVIEW.getCode()) {
            throw new BusinessException("订单未到待评价状态");
        }
        updateOrderStatus(order, OrderStatus.COMPLETED);
    }

    private String generateVerificationCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}