package com.skillchain.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.skillchain.entity.Order;
import com.skillchain.entity.Review;
import com.skillchain.mapper.ReviewMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    @Autowired
    private ReviewMapper reviewMapper;

    @Autowired
    private OrderService orderService;

    public Page<Review> getReviewsBySkillId(Long skillId, Integer page, Integer size) {
        Page<Review> pageInfo = new Page<>(page, size);
        List<Long> orderIds = orderService.getOrdersBySkillId(skillId)
                .stream().map(Order::getOrderId).collect(Collectors.toList());
        if (orderIds.isEmpty()) {
            return pageInfo;
        }
        return reviewMapper.selectPage(pageInfo, new LambdaQueryWrapper<Review>()
                .in(Review::getOrderId, orderIds)
                .orderByDesc(Review::getCreateTime));
    }

    public Page<Review> getReviewsByProviderId(Long providerId, Integer page, Integer size) {
        Page<Review> pageInfo = new Page<>(page, size);
        return reviewMapper.selectPage(pageInfo, new LambdaQueryWrapper<Review>()
                .eq(Review::getProviderId, providerId)
                .orderByDesc(Review::getCreateTime));
    }

    @Transactional
    public Review createReview(Long reviewerId, Long orderId, Integer rating, String content) {
        Order order = orderService.getOrderById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!order.getBuyerId().equals(reviewerId)) {
            throw new RuntimeException("只能评价自己购买的订单");
        }
        if (order.getStatus() != 4) {
            throw new RuntimeException("订单未到待评价状态");
        }

        Review review = new Review();
        review.setOrderId(orderId);
        review.setReviewerId(reviewerId);
        review.setProviderId(order.getProviderId());
        review.setRating(rating);
        review.setContent(content);
        reviewMapper.insert(review);

        // 更新订单状态为已完成
        orderService.finishReview(reviewerId, orderId);

        return review;
    }
}
