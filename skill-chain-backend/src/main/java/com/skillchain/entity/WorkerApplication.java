package com.skillchain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("worker_application")
public class WorkerApplication {
    @TableId(type = IdType.AUTO)
    private Long applicationId;

    private Long userId;

    private String realName;

    private String idCard;

    private String phone;

    private String skillTitle;

    private String category;

    private String description;

    private String experience;

    private BigDecimal price;

    private String unit;

    private String serviceArea;

    private String idCardFrontUrl;

    private String idCardBackUrl;

    private String certificateUrls;

    // 0-pending, 1-approved, 2-rejected, 3-revoked
    private Integer status;

    private String rejectReason;

    private Long reviewerId;

    private LocalDateTime submitTime;

    private LocalDateTime reviewTime;

    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
