package com.skillchain.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.skillchain.entity.User;
import com.skillchain.entity.WorkerApplication;
import com.skillchain.mapper.UserMapper;
import com.skillchain.mapper.WorkerApplicationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class WorkerApplicationService {

    @Autowired
    private WorkerApplicationMapper workerApplicationMapper;

    @Autowired
    private UserMapper userMapper;

    @Transactional
    public void submitApplication(Long userId, WorkerApplication payload) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        WorkerApplication latest = workerApplicationMapper.selectOne(new LambdaQueryWrapper<WorkerApplication>()
                .eq(WorkerApplication::getUserId, userId)
                .eq(WorkerApplication::getDeleted, 0)
                .orderByDesc(WorkerApplication::getCreateTime)
                .last("limit 1"));
        if (latest != null && latest.getStatus() != null && latest.getStatus() == 0) {
            throw new RuntimeException("已有待审核申请，请勿重复提交");
        }

        WorkerApplication application = new WorkerApplication();
        application.setUserId(userId);
        application.setRealName(payload.getRealName());
        application.setIdCard(payload.getIdCard());
        application.setPhone(payload.getPhone());
        application.setSkillTitle(payload.getSkillTitle());
        application.setCategory(payload.getCategory());
        application.setDescription(payload.getDescription());
        application.setExperience(payload.getExperience());
        application.setPrice(payload.getPrice());
        application.setUnit(payload.getUnit());
        application.setServiceArea(payload.getServiceArea());
        application.setIdCardFrontUrl(payload.getIdCardFrontUrl());
        application.setIdCardBackUrl(payload.getIdCardBackUrl());
        application.setCertificateUrls(payload.getCertificateUrls());
        application.setStatus(0);
        application.setDeleted(0);
        application.setSubmitTime(LocalDateTime.now());

        workerApplicationMapper.insert(application);
    }

    public WorkerApplication getMyLatestApplication(Long userId) {
        return workerApplicationMapper.selectOne(new LambdaQueryWrapper<WorkerApplication>()
                .eq(WorkerApplication::getUserId, userId)
                .eq(WorkerApplication::getDeleted, 0)
                .orderByDesc(WorkerApplication::getCreateTime)
                .last("limit 1"));
    }

    @Transactional
    public void cancelMyPending(Long userId, Long applicationId) {
        WorkerApplication application = workerApplicationMapper.selectById(applicationId);
        if (application == null || !userId.equals(application.getUserId())) {
            throw new RuntimeException("申请不存在");
        }
        if (application.getStatus() == null || application.getStatus() != 0) {
            throw new RuntimeException("仅可撤回待审核申请");
        }
        application.setStatus(3);
        application.setReviewTime(LocalDateTime.now());
        workerApplicationMapper.updateById(application);
    }

    public Page<WorkerApplication> getAdminList(Integer page, Integer size, Integer status) {
        Page<WorkerApplication> pageInfo = new Page<>(page, size);
        LambdaQueryWrapper<WorkerApplication> wrapper = new LambdaQueryWrapper<WorkerApplication>()
                .eq(WorkerApplication::getDeleted, 0)
                .orderByDesc(WorkerApplication::getCreateTime);
        if (status != null) {
            wrapper.eq(WorkerApplication::getStatus, status);
        }
        return workerApplicationMapper.selectPage(pageInfo, wrapper);
    }

    @Transactional
    public void approve(Long applicationId, Long reviewerId) {
        WorkerApplication application = workerApplicationMapper.selectById(applicationId);
        if (application == null) {
            throw new RuntimeException("申请不存在");
        }
        application.setStatus(1);
        application.setReviewerId(reviewerId);
        application.setReviewTime(LocalDateTime.now());
        workerApplicationMapper.updateById(application);

        User user = userMapper.selectById(application.getUserId());
        if (user != null) {
            user.setRole(1);
            userMapper.updateById(user);
        }
    }

    @Transactional
    public void reject(Long applicationId, Long reviewerId, String reason) {
        WorkerApplication application = workerApplicationMapper.selectById(applicationId);
        if (application == null) {
            throw new RuntimeException("申请不存在");
        }
        application.setStatus(2);
        application.setRejectReason(reason);
        application.setReviewerId(reviewerId);
        application.setReviewTime(LocalDateTime.now());
        workerApplicationMapper.updateById(application);
    }

    @Transactional
    public void revert(Long applicationId) {
        WorkerApplication application = workerApplicationMapper.selectById(applicationId);
        if (application == null) {
            throw new RuntimeException("申请不存在");
        }
        application.setStatus(0);
        application.setRejectReason(null);
        application.setReviewerId(null);
        application.setReviewTime(null);
        workerApplicationMapper.updateById(application);

        User user = userMapper.selectById(application.getUserId());
        if (user != null && user.getRole() != null && user.getRole() == 1) {
            user.setRole(0);
            userMapper.updateById(user);
        }
    }
}
