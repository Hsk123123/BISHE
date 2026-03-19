package com.skillchain.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.skillchain.entity.Task;
import com.skillchain.entity.UserTask;
import com.skillchain.mapper.TaskMapper;
import com.skillchain.mapper.UserTaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private UserTaskMapper userTaskMapper;

    public List<Task> getAllTasks() {
        return taskMapper.selectList(null);
    }

    public List<UserTask> getUserTasks(Long userId) {
        return userTaskMapper.selectList(
                new LambdaQueryWrapper<UserTask>()
                        .eq(UserTask::getUserId, userId)
                        .eq(UserTask::getResetDate, LocalDate.now())
        );
    }

    public void resetDailyTasks(Long userId) {
        userTaskMapper.delete(
                new LambdaQueryWrapper<UserTask>()
                        .eq(UserTask::getUserId, userId)
                        .lt(UserTask::getResetDate, LocalDate.now())
        );
    }
}