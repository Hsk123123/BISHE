package com.skillchain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("`order`")
public class Order {
    @TableId(type = IdType.AUTO)
    private Long orderId;

    private Long buyerId;

    private Long skillId;

    private Long providerId;

    private BigDecimal amount;

    private Integer currencyType;

    private Integer status;

    private String scheduleDate;

    private String timeSlot;

    private String location;

    private String verificationCode;

    private Long scheduleId;

    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}