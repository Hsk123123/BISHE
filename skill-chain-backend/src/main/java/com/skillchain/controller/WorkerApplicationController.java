package com.skillchain.controller;

import com.skillchain.common.Result;
import com.skillchain.entity.WorkerApplication;
import com.skillchain.service.WorkerApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/worker-application")
public class WorkerApplicationController {

    @Autowired
    private WorkerApplicationService workerApplicationService;

    @PostMapping("/submit")
    public Result<Void> submit(@RequestAttribute("userId") Long userId, @RequestBody WorkerApplication payload) {
        workerApplicationService.submitApplication(userId, payload);
        return Result.success("申请提交成功", null);
    }

    @GetMapping("/my")
    public Result<WorkerApplication> myLatest(@RequestAttribute("userId") Long userId) {
        return Result.success(workerApplicationService.getMyLatestApplication(userId));
    }

    @PutMapping("/{applicationId}/cancel")
    public Result<Void> cancel(@RequestAttribute("userId") Long userId, @PathVariable Long applicationId) {
        workerApplicationService.cancelMyPending(userId, applicationId);
        return Result.success("申请已撤回", null);
    }
}
