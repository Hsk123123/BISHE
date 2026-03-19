package com.skillchain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("wallet")
public class Wallet {
    @TableId(type = IdType.AUTO)
    private Long walletId;

    private Long userId;

    private BigDecimal cnyCoinBalance;

    private BigDecimal pointBalance;

    private String paymentPassword;

    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}