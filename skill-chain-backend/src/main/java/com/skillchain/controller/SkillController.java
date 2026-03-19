package com.skillchain.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.skillchain.common.Result;
import com.skillchain.entity.Skill;
import com.skillchain.service.ScheduleService;
import com.skillchain.service.SkillService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/skill")
public class SkillController {

    @Autowired
    private SkillService skillService;

    @Autowired
    private ScheduleService scheduleService;

    @GetMapping("/list")
    public Result<Page<Skill>> getSkillList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") Integer sort
    ) {
        Page<Skill> pageInfo = skillService.getSkillList(page, size, categoryId, keyword, sort);
        return Result.success(pageInfo);
    }

    @GetMapping("/{id}")
    public Result<Skill> getSkillDetail(@PathVariable Long id) {
        Skill skill = skillService.getSkillById(id);
        return Result.success(skill);
    }

    @GetMapping("/provider/{providerId}")
    public Result<List<Skill>> getProviderSkills(@PathVariable Long providerId) {
        List<Skill> skills = skillService.getProviderSkills(providerId);
        return Result.success(skills);
    }

    @PostMapping("/publish")
    public Result<Void> publishSkill(@RequestBody Skill skill, HttpServletRequest request) {
        Long providerId = (Long) request.getAttribute("userId");
        skillService.publishSkill(providerId, skill);
        return Result.success("发布成功", null);
    }

    @PutMapping("/{id}")
    public Result<Void> updateSkill(@PathVariable Long id, @RequestBody Skill skill, HttpServletRequest request) {
        Long providerId = (Long) request.getAttribute("userId");
        skillService.updateSkill(providerId, id, skill);
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteSkill(@PathVariable Long id, HttpServletRequest request) {
        Long providerId = (Long) request.getAttribute("userId");
        skillService.deleteSkill(providerId, id);
        return Result.success("删除成功", null);
    }

    @GetMapping("/{skillId}/schedule")
    public Result<List<Map<String, Object>>> getSkillSchedule(
            @PathVariable Long skillId,
            @RequestParam String date
    ) {
        Skill skill = skillService.getSkillById(skillId);
        List<Map<String, Object>> schedules = (List<Map<String, Object>>) (Object) scheduleService.getAvailableSlots(skill.getProviderId(), date);
        return Result.success(schedules);
    }
}