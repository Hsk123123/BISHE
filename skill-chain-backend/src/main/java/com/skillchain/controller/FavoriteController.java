package com.skillchain.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.skillchain.common.Result;
import com.skillchain.entity.Skill;
import com.skillchain.service.FavoriteService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/favorite")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @PostMapping("/add")
    public Result<Void> addFavorite(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Long skillId = Long.valueOf(body.get("skillId").toString());
        favoriteService.addFavorite(userId, skillId);
        return Result.success("收藏成功", null);
    }

    @DeleteMapping("/remove")
    public Result<Void> removeFavorite(@RequestParam Long skillId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        favoriteService.removeFavorite(userId, skillId);
        return Result.success("取消收藏", null);
    }

    @GetMapping("/list")
    public Result<Page<Skill>> getFavoriteList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Page<Skill> result = favoriteService.getFavoriteSkills(userId, page, size);
        return Result.success(result);
    }

    @GetMapping("/count")
    public Result<Long> getFavoriteCount(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(favoriteService.countFavorites(userId));
    }
}
