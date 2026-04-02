package com.skillchain.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.skillchain.entity.Order;
import com.skillchain.entity.WithdrawalRequest;
import com.skillchain.mapper.OrderMapper;
import com.skillchain.mapper.WithdrawalRequestMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;

@Service
public class AdminFinanceService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private WithdrawalRequestMapper withdrawalRequestMapper;

    @Value("${system.platform-commission-rate:0.05}")
    private Double platformCommissionRate;

    public Map<String, Object> getOverview() {
        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime todayEnd = today.plusDays(1).atStartOfDay();

        YearMonth currentMonth = YearMonth.now();
        LocalDateTime monthStart = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime monthEnd = currentMonth.plusMonths(1).atDay(1).atStartOfDay();

        List<Order> allCompletedOrders = orderMapper.selectList(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getStatus, 5)
                        .eq(Order::getDeleted, 0)
        );
        List<Order> todayCompletedOrders = orderMapper.selectList(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getStatus, 5)
                        .eq(Order::getDeleted, 0)
                        .ge(Order::getCreateTime, todayStart)
                        .lt(Order::getCreateTime, todayEnd)
        );
        List<Order> monthCompletedOrders = orderMapper.selectList(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getStatus, 5)
                        .eq(Order::getDeleted, 0)
                        .ge(Order::getCreateTime, monthStart)
                        .lt(Order::getCreateTime, monthEnd)
        );

        List<WithdrawalRequest> pendingWithdrawals = withdrawalRequestMapper.selectList(
                new LambdaQueryWrapper<WithdrawalRequest>()
                        .eq(WithdrawalRequest::getStatus, 0)
        );

        Map<String, Object> result = new HashMap<>();
        result.put("todayRevenue", sumOrderAmount(todayCompletedOrders));
        result.put("monthRevenue", sumOrderAmount(monthCompletedOrders));
        result.put("monthOrders", (long) monthCompletedOrders.size());
        result.put("pendingWithdrawals", sumWithdrawalAmount(pendingWithdrawals));
        result.put("pendingCount", (long) pendingWithdrawals.size());
        result.put("totalRevenue", sumOrderAmount(allCompletedOrders));
        result.put("totalOrders", (long) allCompletedOrders.size());
        return result;
    }

    public Map<String, Object> getTransactions(Integer page, Integer size, String type, LocalDate startDate, LocalDate endDate) {
        List<Map<String, Object>> rows = new ArrayList<>();
        LocalDateTime start = startDate == null ? null : startDate.atStartOfDay();
        LocalDateTime end = endDate == null ? null : endDate.plusDays(1).atStartOfDay();

        if (type == null || type.isEmpty() || "order".equals(type) || "refund".equals(type)) {
            LambdaQueryWrapper<Order> orderWrapper = new LambdaQueryWrapper<Order>()
                    .eq(Order::getDeleted, 0)
                    .orderByDesc(Order::getCreateTime);
            if (start != null) {
                orderWrapper.ge(Order::getCreateTime, start);
            }
            if (end != null) {
                orderWrapper.lt(Order::getCreateTime, end);
            }
            if ("refund".equals(type)) {
                orderWrapper.eq(Order::getStatus, 7);
            } else if ("order".equals(type)) {
                orderWrapper.ne(Order::getStatus, 7);
            }

            List<Order> orders = orderMapper.selectList(orderWrapper);
            for (Order order : orders) {
                Map<String, Object> item = new HashMap<>();
                boolean isRefund = Integer.valueOf(7).equals(order.getStatus());
                BigDecimal amount = safeAmount(order.getAmount());
                BigDecimal feeRate = BigDecimal.valueOf(platformCommissionRate == null ? 0.05 : platformCommissionRate);
                BigDecimal fee = isRefund ? BigDecimal.ZERO : amount.multiply(feeRate).setScale(2, RoundingMode.HALF_UP);
                BigDecimal actualAmount = isRefund ? amount : amount.subtract(fee);

                item.put("transactionId", "ORD-" + order.getOrderId());
                item.put("type", isRefund ? "refund" : "order");
                item.put("orderId", order.getOrderId());
                item.put("userName", "用户#" + order.getBuyerId());
                item.put("providerName", "服务者#" + order.getProviderId());
                item.put("amount", amount);
                item.put("platformFee", fee);
                item.put("actualAmount", actualAmount);
                item.put("status", toOrderTransactionStatus(order.getStatus()));
                item.put("createTime", order.getCreateTime());
                rows.add(item);
            }
        }

        if (type == null || type.isEmpty() || "withdrawal".equals(type)) {
            LambdaQueryWrapper<WithdrawalRequest> withdrawalWrapper = new LambdaQueryWrapper<WithdrawalRequest>()
                    .orderByDesc(WithdrawalRequest::getCreateTime);
            if (start != null) {
                withdrawalWrapper.ge(WithdrawalRequest::getCreateTime, start);
            }
            if (end != null) {
                withdrawalWrapper.lt(WithdrawalRequest::getCreateTime, end);
            }
            List<WithdrawalRequest> withdrawals = withdrawalRequestMapper.selectList(withdrawalWrapper);
            for (WithdrawalRequest withdrawal : withdrawals) {
                Map<String, Object> item = new HashMap<>();
                item.put("transactionId", "WDR-" + withdrawal.getId());
                item.put("type", "withdrawal");
                item.put("orderId", null);
                item.put("userName", "用户#" + withdrawal.getUserId());
                item.put("providerName", "服务者#" + withdrawal.getUserId());
                item.put("amount", safeAmount(withdrawal.getAmount()));
                item.put("platformFee", safeAmount(withdrawal.getFee()));
                item.put("actualAmount", safeAmount(withdrawal.getActualAmount()));
                item.put("status", toWithdrawalTransactionStatus(withdrawal.getStatus()));
                item.put("createTime", withdrawal.getCreateTime());
                rows.add(item);
            }
        }

        rows.sort((a, b) -> {
            LocalDateTime t1 = (LocalDateTime) a.get("createTime");
            LocalDateTime t2 = (LocalDateTime) b.get("createTime");
            if (t1 == null && t2 == null) {
                return 0;
            }
            if (t1 == null) {
                return 1;
            }
            if (t2 == null) {
                return -1;
            }
            return t2.compareTo(t1);
        });

        int safePage = page == null || page < 1 ? 1 : page;
        int safeSize = size == null || size < 1 ? 10 : size;
        int total = rows.size();
        int from = (safePage - 1) * safeSize;
        if (from >= total) {
            from = total;
        }
        int to = Math.min(from + safeSize, total);

        Map<String, Object> result = new HashMap<>();
        result.put("records", rows.subList(from, to));
        result.put("total", total);
        result.put("current", safePage);
        result.put("size", safeSize);
        return result;
    }

    private BigDecimal safeAmount(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private BigDecimal sumOrderAmount(List<Order> orders) {
        BigDecimal total = BigDecimal.ZERO;
        for (Order order : orders) {
            total = total.add(safeAmount(order.getAmount()));
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal sumWithdrawalAmount(List<WithdrawalRequest> rows) {
        BigDecimal total = BigDecimal.ZERO;
        for (WithdrawalRequest row : rows) {
            total = total.add(safeAmount(row.getAmount()));
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    private String toOrderTransactionStatus(Integer orderStatus) {
        if (orderStatus == null) {
            return "pending";
        }
        if (orderStatus == 5 || orderStatus == 7) {
            return "completed";
        }
        return "pending";
    }

    private String toWithdrawalTransactionStatus(Integer withdrawalStatus) {
        if (withdrawalStatus == null) {
            return "pending";
        }
        if (withdrawalStatus == 1) {
            return "completed";
        }
        if (withdrawalStatus == 2) {
            return "rejected";
        }
        return "pending";
    }
}
