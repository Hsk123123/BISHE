package com.skillchain.controller;

import com.skillchain.common.Result;
import com.skillchain.entity.Task;
import com.skillchain.entity.UserTask;
import com.skillchain.service.TaskService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping("/list")
    public Result<List<Task>> getAllTasks() {
        List<Task> tasks = taskService.getAllTasks();
        return Result.success(tasks);
    }

    @GetMapping("/my")
    public Result<List<UserTask>> getMyTasks(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        taskService.resetDailyTasks(userId);
        List<UserTask> userTasks = taskService.getUserTasks(userId);
        return Result.success(userTasks);
    }
}