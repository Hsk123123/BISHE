package com.skillchain.aspect;

import com.skillchain.entity.Task;
import com.skillchain.entity.UserTask;
import com.skillchain.mapper.TaskMapper;
import com.skillchain.mapper.UserTaskMapper;
import com.skillchain.service.WalletService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;

@Aspect
@Component
public class TaskAspect {

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private UserTaskMapper userTaskMapper;

    @Autowired
    private WalletService walletService;

    @AfterReturning("@annotation(com.skillchain.aspect.TaskTrigger) && @annotation(taskTrigger)")
    public void handleTaskTrigger(JoinPoint joinPoint, TaskTrigger taskTrigger) {
        String action = taskTrigger.action();

        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return;
        }
        HttpServletRequest request = attrs.getRequest();
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return;
        }

        Task task = taskMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Task>()
                        .eq(Task::getCondition, action)
        );

        if (task == null) {
            return;
        }

        UserTask userTask = userTaskMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UserTask>()
                        .eq(UserTask::getUserId, userId)
                        .eq(UserTask::getTaskId, task.getTaskId())
                        .eq(UserTask::getResetDate, LocalDate.now())
        );

        if (userTask == null) {
            userTask = new UserTask();
            userTask.setUserId(userId);
            userTask.setTaskId(task.getTaskId());
            userTask.setCurrentProgress(0);
            userTask.setIsClaimed(0);
            userTask.setResetDate(LocalDate.now());
            userTaskMapper.insert(userTask);
        }

        if (userTask.getIsClaimed() == 1) {
            return;
        }

        userTask.setCurrentProgress(userTask.getCurrentProgress() + 1);

        if (userTask.getCurrentProgress() >= task.getTargetProgress()) {
            userTask.setIsClaimed(1);
            walletService.addPoints(userId, BigDecimal.valueOf(task.getRewardPoints()));
        }

        userTaskMapper.updateById(userTask);
    }

}