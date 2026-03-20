package com.skillchain.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.skillchain.entity.User;
import com.skillchain.entity.Wallet;
import com.skillchain.mapper.UserMapper;
import com.skillchain.mapper.WalletMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class AdminUserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WalletMapper walletMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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
    public void createUser(String username, String password, String phone, String email, Integer role, Integer status) {
        if (username == null || username.trim().isEmpty()) {
            throw new RuntimeException("用户名不能为空");
        }
        if (password == null || password.length() < 6) {
            throw new RuntimeException("密码长度不能小于6位");
        }

        User existUser = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, username)
        );
        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        user.setUsername(username.trim());
        user.setPassword(passwordEncoder.encode(password));
        user.setPhone(phone);
        user.setEmail(email);
        user.setNickname(username.trim());
        user.setRole(role == null ? 0 : role);
        user.setRealNameStatus(0);
        user.setDeleted(status != null && status == 0 ? 1 : 0);
        userMapper.insert(user);

        Wallet wallet = new Wallet();
        wallet.setUserId(user.getUserId());
        wallet.setCnyCoinBalance(BigDecimal.ZERO);
        wallet.setPointBalance(BigDecimal.ZERO);
        wallet.setDeleted(0);
        walletMapper.insert(wallet);
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
