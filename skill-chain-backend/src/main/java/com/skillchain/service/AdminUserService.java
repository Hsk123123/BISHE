package com.skillchain.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.skillchain.entity.User;
import com.skillchain.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminUserService {

    @Autowired
    private UserMapper userMapper;

    public Page<User> getUserList(Integer page, Integer size, String keyword, Integer role) {
        Page<User> pageInfo = new Page<>(page, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(User::getUsername, keyword)
                    .or()
                    .like(User::getPhone, keyword));
        }

        if (role != null) {
            wrapper.eq(User::getRole, role);
        }

        wrapper.orderByDesc(User::getCreateTime);

        return userMapper.selectPage(pageInfo, wrapper);
    }

    @Transactional
    public void updateUserRole(Long userId, Integer role) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        user.setRole(role);
        userMapper.updateById(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        userMapper.deleteById(userId);
    }

    @Transactional
    public void approveRealName(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        user.setRealNameStatus(2);
        userMapper.updateById(user);
    }

    @Transactional
    public void rejectRealName(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        user.setRealNameStatus(3);
        userMapper.updateById(user);
    }
}