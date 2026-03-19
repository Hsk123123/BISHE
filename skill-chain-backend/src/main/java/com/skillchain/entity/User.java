package com.skillchain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long userId;

    private String username;

    private String password;

    private String phone;

    private String email;

    private String nickname;

    private String avatar;

    private String bio;

    private Integer role;

    private Integer realNameStatus;

    private String idCardFront;

    private String idCardBack;

    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}