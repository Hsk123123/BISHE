package com.skillchain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("decoration")
public class Decoration {
    @TableId(type = IdType.AUTO)
    private Long decoId;

    private Long userId;

    private Integer type;

    private String name;

    private String imageUrl;

    private Integer isEquipped;

    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}