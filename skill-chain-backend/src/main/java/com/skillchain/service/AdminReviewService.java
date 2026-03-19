package com.skillchain.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.skillchain.entity.Appeal;
import com.skillchain.entity.Review;
import com.skillchain.mapper.AppealMapper;
import com.skillchain.mapper.ReviewMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminReviewService {

    @Autowired
    private ReviewMapper reviewMapper;

    @Autowired
    private AppealMapper appealMapper;

    public Page<Review> getReviewList(Integer page, Integer size, Integer status) {
        Page<Review> pageInfo = new Page<>(page, size);
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<Review>()
                .orderByDesc(Review::getCreateTime);

        // status: 1-published, 2-hidden
        if (status != null) {
            if (status == 2) {
                wrapper.eq(Review::getDeleted, 1);
            } else {
                wrapper.eq(Review::getDeleted, 0);
            }
        }

        return reviewMapper.selectPage(pageInfo, wrapper);
    }

    @Transactional
    public void hideReview(Long reviewId) {
        Review review = reviewMapper.selectById(reviewId);
        if (review == null) {
            throw new RuntimeException("评价不存在");
        }
        review.setDeleted(1);
        reviewMapper.updateById(review);
    }

    @Transactional
    public void publishReview(Long reviewId) {
        Review review = reviewMapper.selectById(reviewId);
        if (review == null) {
            throw new RuntimeException("评价不存在");
        }
        review.setDeleted(0);
        reviewMapper.updateById(review);
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        reviewMapper.deleteById(reviewId);
    }

    public Page<Appeal> getAppealList(Integer page, Integer size, Integer status) {
        Page<Appeal> pageInfo = new Page<>(page, size);
        LambdaQueryWrapper<Appeal> wrapper = new LambdaQueryWrapper<Appeal>()
                .orderByDesc(Appeal::getCreateTime);
        if (status != null) {
            wrapper.eq(Appeal::getStatus, status);
        }
        return appealMapper.selectPage(pageInfo, wrapper);
    }

    @Transactional
    public void resolveAppeal(Long appealId, String handleResult) {
        Appeal appeal = appealMapper.selectById(appealId);
        if (appeal == null) {
            throw new RuntimeException("申诉不存在");
        }
        appeal.setStatus(2);
        appeal.setHandleResult(handleResult);
        appealMapper.updateById(appeal);
    }
}
