package com.skillchain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("user_task")
public class UserTask {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long taskId;

    private Integer currentProgress;

    private Integer isClaimed;

    private LocalDate resetDate;

    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}