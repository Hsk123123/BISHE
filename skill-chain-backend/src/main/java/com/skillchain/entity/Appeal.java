package com.skillchain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("appeal")
public class Appeal {
    @TableId(type = IdType.AUTO)
    private Long appealId;

    private Long orderId;

    private Long appealerId;

    private String reason;

    private String description;

    // 0-pending, 1-processing, 2-resolved
    private Integer status;

    private String handleResult;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
