package com.skillchain.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.skillchain.entity.User;
import com.skillchain.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User getUserById(Long userId) {
        return userMapper.selectById(userId);
    }

    public User getUserByUsername(String username) {
        return userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username));
    }

    @Transactional
    public void updateUser(User user) {
        userMapper.updateById(user);
    }

    @Transactional
    public void submitRealNameVerification(Long userId, String idCardFront, String idCardBack) {
        User user = getUserById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        user.setIdCardFront(idCardFront);
        user.setIdCardBack(idCardBack);
        user.setRealNameStatus(1); // 待审核状态
        userMapper.updateById(user);
    }

    @Transactional
    public void applyForWorkerRole(Long userId) {
        User user = getUserById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (user.getRealNameStatus() != 2) {
            throw new RuntimeException("必须先通过实名认证才能申请成为个人工作者");
        }

        user.setRole(1); // 申请成为个人工作者，设置角色为1
        userMapper.updateById(user);
    }

    @Transactional
    public void switchRole(Long userId, Integer newRole) {
        User user = getUserById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (newRole == null) {
            throw new RuntimeException("角色参数不能为空");
        }
        // 仅允许普通用户与工作者之间切换，禁止通过该接口切管理员
        if (newRole != 0 && newRole != 1) {
            throw new RuntimeException("仅支持切换为普通用户或工作者角色");
        }

        // 验证角色切换的合法性
        if (newRole == 1 && user.getRealNameStatus() != 2) {
            throw new RuntimeException("必须先通过实名认证才能切换为个人工作者角色");
        }

        user.setRole(newRole);
        userMapper.updateById(user);
    }

    public boolean checkUserRole(Long userId, Integer requiredRole) {
        User user = getUserById(userId);
        return user != null && user.getRole() >= requiredRole;
    }
}
