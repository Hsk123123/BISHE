package com.skillchain.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.skillchain.entity.Wallet;
import com.skillchain.entity.WithdrawalRequest;
import com.skillchain.mapper.OrderMapper;
import com.skillchain.mapper.WithdrawalRequestMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class WithdrawalService {

    @Autowired
    private WithdrawalRequestMapper withdrawalRequestMapper;

    @Autowired
    private WalletService walletService;

    @Autowired
    private TransactionLogService transactionLogService;

    @Autowired
    private OrderMapper orderMapper;

    @Value("${system.withdrawal-fee-rate:0.05}")
    private Double withdrawalFeeRate;

    @Transactional
    public WithdrawalRequest createWithdrawal(Long userId, BigDecimal amount, String bankName, String bankCard) {
        Wallet wallet = walletService.getWalletByUserId(userId);
        if (wallet == null) {
            throw new RuntimeException("钱包不存在");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("提现金额必须大于0");
        }
        if (amount.compareTo(wallet.getCnyCoinBalance()) > 0) {
            throw new RuntimeException("余额不足");
        }
        if (amount.compareTo(new BigDecimal("10")) < 0) {
            throw new RuntimeException("单笔提现不能少于10元");
        }

        BigDecimal fee = amount.multiply(BigDecimal.valueOf(withdrawalFeeRate)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal actualAmount = amount.subtract(fee);

        WithdrawalRequest wr = new WithdrawalRequest();
        wr.setUserId(userId);
        wr.setAmount(amount);
        wr.setFee(fee);
        wr.setActualAmount(actualAmount);
        wr.setStatus(0);
        wr.setBankName(bankName);
        wr.setBankCard(bankCard);
        withdrawalRequestMapper.insert(wr);

        // 扣减可提现余额（或冻结）
        walletService.deductCoin(userId, amount);
        transactionLogService.createTransactionLog(userId, 4, amount.negate(), 1, "提现申请");

        return wr;
    }

    public Page<WithdrawalRequest> getMyWithdrawals(Long userId, Integer page, Integer size) {
        Page<WithdrawalRequest> pageInfo = new Page<>(page, size);
        return withdrawalRequestMapper.selectPage(pageInfo,
                new LambdaQueryWrapper<WithdrawalRequest>()
                        .eq(WithdrawalRequest::getUserId, userId)
                        .orderByDesc(WithdrawalRequest::getCreateTime));
    }

    public Page<WithdrawalRequest> getPendingWithdrawals(Integer page, Integer size) {
        Page<WithdrawalRequest> pageInfo = new Page<>(page, size);
        return withdrawalRequestMapper.selectPage(pageInfo,
                new LambdaQueryWrapper<WithdrawalRequest>()
                        .eq(WithdrawalRequest::getStatus, 0)
                        .orderByAsc(WithdrawalRequest::getCreateTime));
    }

    @Transactional
    public void approveWithdrawal(Long id) {
        WithdrawalRequest wr = withdrawalRequestMapper.selectById(id);
        if (wr == null) {
            throw new RuntimeException("提现申请不存在");
        }
        if (wr.getStatus() != 0) {
            throw new RuntimeException("该申请已处理");
        }
        wr.setStatus(1);
        wr.setProcessTime(LocalDateTime.now());
        withdrawalRequestMapper.updateById(wr);
    }

    @Transactional
    public void rejectWithdrawal(Long id) {
        WithdrawalRequest wr = withdrawalRequestMapper.selectById(id);
        if (wr == null) {
            throw new RuntimeException("提现申请不存在");
        }
        if (wr.getStatus() != 0) {
            throw new RuntimeException("该申请已处理");
        }
        wr.setStatus(2);
        wr.setProcessTime(LocalDateTime.now());
        withdrawalRequestMapper.updateById(wr);
        // 退还余额
        walletService.recharge(wr.getUserId(), wr.getAmount());
        transactionLogService.createTransactionLog(wr.getUserId(), 5, wr.getAmount(), 1, "提现拒绝退款");
    }

    public Map<String, Object> getEarningsStats(Long userId) {
        Wallet wallet = walletService.getWalletByUserId(userId);
        BigDecimal available = wallet != null ? wallet.getCnyCoinBalance() : BigDecimal.ZERO;

        BigDecimal totalEarnings = orderMapper.selectTotalEarnings(userId);
        BigDecimal monthlyEarnings = orderMapper.selectMonthlyEarnings(userId);
        Integer monthlyOrders = orderMapper.selectMonthlyOrders(userId);
        Integer completedOrders = orderMapper.selectCompletedOrders(userId);

        BigDecimal avgOrderValue = BigDecimal.ZERO;
        if (completedOrders != null && completedOrders > 0 && totalEarnings != null) {
            avgOrderValue = totalEarnings.divide(BigDecimal.valueOf(completedOrders), 2, RoundingMode.HALF_UP);
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("availableAmount", available);
        stats.put("frozenAmount", BigDecimal.ZERO);
        stats.put("totalEarnings", totalEarnings != null ? totalEarnings : BigDecimal.ZERO);
        stats.put("monthlyEarnings", monthlyEarnings != null ? monthlyEarnings : BigDecimal.ZERO);
        stats.put("monthlyOrders", monthlyOrders != null ? monthlyOrders : 0);
        stats.put("completedOrders", completedOrders != null ? completedOrders : 0);
        stats.put("avgOrderValue", avgOrderValue);
        return stats;
    }
}
