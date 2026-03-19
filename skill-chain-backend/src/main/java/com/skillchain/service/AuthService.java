package com.skillchain.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.skillchain.dto.LoginDTO;
import com.skillchain.dto.RegisterDTO;
import com.skillchain.entity.User;
import com.skillchain.entity.Wallet;
import com.skillchain.mapper.UserMapper;
import com.skillchain.mapper.WalletMapper;
import com.skillchain.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WalletMapper walletMapper;

    @Autowired
    private JwtUtil jwtUtil;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String login(LoginDTO loginDTO) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, loginDTO.getUsername())
        );

        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        return jwtUtil.generateToken(user.getUserId(), user.getUsername(), user.getRole());
    }

    @Transactional
    public void register(RegisterDTO registerDTO) {
        User existUser = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, registerDTO.getUsername())
        );

        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setPhone(registerDTO.getPhone());
        user.setEmail(registerDTO.getEmail());
        user.setNickname(registerDTO.getUsername());
        user.setRole(0);
        user.setRealNameStatus(0);

        userMapper.insert(user);

        Wallet wallet = new Wallet();
        wallet.setUserId(user.getUserId());
        wallet.setCnyCoinBalance(BigDecimal.ZERO);
        wallet.setPointBalance(BigDecimal.ZERO);

        walletMapper.insert(wallet);
    }

    public User getUserInfo(Long userId) {
        return userMapper.selectById(userId);
    }

    public User getUserByUsername(String username) {
        return userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, username)
        );
    }

    public void updateProfile(Long userId, User user) {
        user.setUserId(userId);
        userMapper.updateById(user);
    }
}
