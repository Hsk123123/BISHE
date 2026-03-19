package com.skillchain.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.skillchain.entity.TransactionLog;
import com.skillchain.mapper.TransactionLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransactionLogService {

    @Autowired
    private TransactionLogMapper transactionLogMapper;

    public Page<TransactionLog> getTransactionLogs(Long userId, Integer page, Integer size) {
        Page<TransactionLog> pageInfo = new Page<>(page, size);
        LambdaQueryWrapper<TransactionLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TransactionLog::getUserId, userId);
        wrapper.orderByDesc(TransactionLog::getCreateTime);
        return transactionLogMapper.selectPage(pageInfo, wrapper);
    }

    public void createTransactionLog(Long userId, Integer type, BigDecimal amount, Integer currency, String description) {
        TransactionLog log = new TransactionLog();
        log.setUserId(userId);
        log.setType(type);
        log.setAmount(amount);
        log.setCurrency(currency);
        log.setDescription(description);
        transactionLogMapper.insert(log);
    }
}