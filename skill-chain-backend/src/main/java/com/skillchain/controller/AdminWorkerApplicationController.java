package com.skillchain.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.skillchain.common.Result;
import com.skillchain.entity.WorkerApplication;
import com.skillchain.service.WorkerApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/worker-application")
public class AdminWorkerApplicationController {

    @Autowired
    private WorkerApplicationService workerApplicationService;

    @GetMapping("/list")
    public Result<Page<WorkerApplication>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status
    ) {
        return Result.success(workerApplicationService.getAdminList(page, size, status));
    }

    @PutMapping("/{applicationId}/approve")
    public Result<Void> approve(@RequestAttribute("userId") Long reviewerId, @PathVariable Long applicationId) {
        workerApplicationService.approve(applicationId, reviewerId);
        return Result.success("审核通过", null);
    }

    @PutMapping("/{applicationId}/reject")
    public Result<Void> reject(
            @RequestAttribute("userId") Long reviewerId,
            @PathVariable Long applicationId,
            @RequestBody(required = false) Map<String, Object> payload
    ) {
        String reason = payload == null ? null : (String) payload.get("reason");
        workerApplicationService.reject(applicationId, reviewerId, reason);
        return Result.success("审核拒绝", null);
    }

    @PutMapping("/{applicationId}/revert")
    public Result<Void> revert(@PathVariable Long applicationId) {
        workerApplicationService.revert(applicationId);
        return Result.success("已撤回审核结果", null);
    }
}
