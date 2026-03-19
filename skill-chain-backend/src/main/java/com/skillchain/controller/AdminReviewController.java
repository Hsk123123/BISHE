package com.skillchain.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.skillchain.common.Result;
import com.skillchain.entity.Appeal;
import com.skillchain.entity.Review;
import com.skillchain.service.AdminReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/review")
public class AdminReviewController {

    @Autowired
    private AdminReviewService adminReviewService;

    @GetMapping("/list")
    public Result<Page<Review>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status
    ) {
        return Result.success(adminReviewService.getReviewList(page, size, status));
    }

    @PutMapping("/{reviewId}/hide")
    public Result<Void> hide(@PathVariable Long reviewId) {
        adminReviewService.hideReview(reviewId);
        return Result.success("评价已隐藏", null);
    }

    @PutMapping("/{reviewId}/publish")
    public Result<Void> publish(@PathVariable Long reviewId) {
        adminReviewService.publishReview(reviewId);
        return Result.success("评价已发布", null);
    }

    @DeleteMapping("/{reviewId}")
    public Result<Void> delete(@PathVariable Long reviewId) {
        adminReviewService.deleteReview(reviewId);
        return Result.success("删除成功", null);
    }

    @GetMapping("/appeal/list")
    public Result<Page<Appeal>> appeals(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status
    ) {
        return Result.success(adminReviewService.getAppealList(page, size, status));
    }

    @PutMapping("/appeal/{appealId}/resolve")
    public Result<Void> resolve(@PathVariable Long appealId, @RequestBody(required = false) Map<String, Object> payload) {
        String handleResult = payload == null ? null : (String) payload.get("handleResult");
        adminReviewService.resolveAppeal(appealId, handleResult);
        return Result.success("申诉已处理", null);
    }
}
