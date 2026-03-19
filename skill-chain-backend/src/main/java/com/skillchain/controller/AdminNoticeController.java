package com.skillchain.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.skillchain.common.Result;
import com.skillchain.entity.Notice;
import com.skillchain.service.AdminNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/notice")
public class AdminNoticeController {

    @Autowired
    private AdminNoticeService adminNoticeService;

    @GetMapping("/list")
    public Result<Page<Notice>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return Result.success(adminNoticeService.getNoticeList(page, size));
    }

    @PostMapping("/create")
    public Result<Notice> create(@RequestBody Notice notice) {
        return Result.success("创建成功", adminNoticeService.createNotice(notice));
    }

    @PutMapping("/{noticeId}")
    public Result<Void> update(@PathVariable Long noticeId, @RequestBody Notice notice) {
        adminNoticeService.updateNotice(noticeId, notice);
        return Result.success("更新成功", null);
    }

    @PutMapping("/{noticeId}/publish")
    public Result<Void> publish(@PathVariable Long noticeId) {
        adminNoticeService.publish(noticeId);
        return Result.success("发布成功", null);
    }

    @DeleteMapping("/{noticeId}")
    public Result<Void> delete(@PathVariable Long noticeId) {
        adminNoticeService.deleteNotice(noticeId);
        return Result.success("删除成功", null);
    }
}
