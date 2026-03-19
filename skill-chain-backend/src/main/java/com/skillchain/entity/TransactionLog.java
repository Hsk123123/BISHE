package com.skillchain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("transaction_log")
public class TransactionLog {
    @TableId(type = IdType.AUTO)
    private Long logId;

    private Long userId;

    private Integer type;

    private BigDecimal amount;

    private Integer currency;

    private String description;

    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}