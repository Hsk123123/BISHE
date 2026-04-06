package com.skillchain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("skill")
public class Skill {
    @TableId(type = IdType.AUTO)
    private Long skillId;

    private Long providerId;

    private Long categoryId;

    private String title;

    private String description;

    private BigDecimal pricePerUnit;

    private Integer unitType;

    private Integer scheduleRequired;

    private Integer serviceMode;

    private String mediaUrls;

    private Integer status;

    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}