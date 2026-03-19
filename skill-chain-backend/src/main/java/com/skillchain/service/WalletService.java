package com.skillchain.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.skillchain.entity.Wallet;
import com.skillchain.mapper.WalletMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WalletService {

    @Autowired
    private WalletMapper walletMapper;

    // 使用内存存储签到状态（替代Redis）
    private static final ConcurrentHashMap<Long, Set<String>> checkInCache = new ConcurrentHashMap<>();

    public Wallet getWalletByUserId(Long userId) {
        return walletMapper.selectOne(
                new LambdaQueryWrapper<Wallet>().eq(Wallet::getUserId, userId)
        );
    }

    @Transactional
    public void recharge(Long userId, BigDecimal amount) {
        Wallet wallet = getWalletByUserId(userId);
        wallet.setCnyCoinBalance(wallet.getCnyCoinBalance().add(amount));
        walletMapper.updateById(wallet);
    }

    @Transactional
    public void deductCoin(Long userId, BigDecimal amount) {
        Wallet wallet = getWalletByUserId(userId);
        if (wallet.getCnyCoinBalance().compareTo(amount) < 0) {
            throw new RuntimeException("余额不足");
        }
        wallet.setCnyCoinBalance(wallet.getCnyCoinBalance().subtract(amount));
        walletMapper.updateById(wallet);
    }

    @Transactional
    public void addPoints(Long userId, BigDecimal points) {
        Wallet wallet = getWalletByUserId(userId);
        wallet.setPointBalance(wallet.getPointBalance().add(points));
        walletMapper.updateById(wallet);
    }

    @Transactional
    public void deductPoints(Long userId, BigDecimal points) {
        Wallet wallet = getWalletByUserId(userId);
        if (wallet.getPointBalance().compareTo(points) < 0) {
            throw new RuntimeException("积分不足");
        }
        wallet.setPointBalance(wallet.getPointBalance().subtract(points));
        walletMapper.updateById(wallet);
    }

    public boolean checkIn(Long userId) {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Set<String> userCheckIns = checkInCache.computeIfAbsent(userId, k -> new HashSet<>());

        if (userCheckIns.contains(today)) {
            return false;
        }

        userCheckIns.add(today);
        addPoints(userId, BigDecimal.TEN);

        return true;
    }
}