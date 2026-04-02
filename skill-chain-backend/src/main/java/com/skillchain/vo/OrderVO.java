package com.skillchain.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderVO {
    private Long orderId;
    private Long buyerId;
    private Long providerId;
    private Long skillId;
    private BigDecimal amount;
    private Integer currencyType;
    private Integer status;
    private String scheduleDate;
    private String timeSlot;
    private String location;
    private String verificationCode;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private String skillTitle;
    private String description;
    private String workerName;
    private String workerAvatar;
    private String buyerName;
    private String buyerAvatar;
}
