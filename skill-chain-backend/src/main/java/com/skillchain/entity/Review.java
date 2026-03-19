package com.skillchain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("review")
public class Review {
    @TableId(type = IdType.AUTO)
    private Long reviewId;

    private Long orderId;

    private Long reviewerId;

    private Long providerId;

    private Integer rating;

    private String content;

    private String images;

    private String replyContent;

    private Integer isAppeal;

    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}