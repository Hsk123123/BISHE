package com.skillchain.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.skillchain.entity.User;
import com.skillchain.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminCertificationService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AdminUserService adminUserService;

    public Page<User> getCertificationList(Integer page, Integer size, Integer status) {
        Page<User> pageInfo = new Page<>(page, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .eq(User::getDeleted, 0)
                .orderByDesc(User::getUpdateTime);

        if (status != null) {
            wrapper.eq(User::getRealNameStatus, status);
        } else {
            wrapper.in(User::getRealNameStatus, 1, 2, 3);
        }

        return userMapper.selectPage(pageInfo, wrapper);
    }

    public void approve(Long userId) {
        adminUserService.approveRealName(userId);
    }

    public void reject(Long userId) {
        adminUserService.rejectRealName(userId);
    }
}
