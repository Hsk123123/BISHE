package com.skillchain.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.skillchain.common.Result;
import com.skillchain.entity.Skill;
import com.skillchain.service.BrowseHistoryService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/history")
public class BrowseHistoryController {

    @Autowired
    private BrowseHistoryService browseHistoryService;

    @PostMapping("/add")
    public Result<Void> addHistory(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Long skillId = Long.valueOf(body.get("skillId").toString());
        browseHistoryService.record(userId, skillId);
        return Result.success("已记录", null);
    }

    @GetMapping("/list")
    public Result<Page<Skill>> getHistoryList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Page<Skill> result = browseHistoryService.getHistorySkills(userId, page, size);
        return Result.success(result);
    }

    @DeleteMapping("/clear")
    public Result<Void> clearHistory(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        browseHistoryService.clearHistory(userId);
        return Result.success("清除成功", null);
    }
}
