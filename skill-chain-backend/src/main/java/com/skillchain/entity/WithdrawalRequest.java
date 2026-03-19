package com.skillchain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("withdrawal_request")
public class WithdrawalRequest {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private BigDecimal amount;

    private BigDecimal fee;

    private BigDecimal actualAmount;

    private Integer status;

    private String bankName;

    private String bankCard;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    private LocalDateTime processTime;
}
