package com.skillchain.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.skillchain.common.Result;
import com.skillchain.entity.Schedule;
import com.skillchain.entity.Skill;
import com.skillchain.service.ScheduleService;
import com.skillchain.service.SkillService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
        return Result.success("Published", null);
    }

    @PutMapping("/{id}")
    public Result<Void> updateSkill(@PathVariable Long id, @RequestBody Skill skill, HttpServletRequest request) {
        Long providerId = (Long) request.getAttribute("userId");
        skillService.updateSkill(providerId, id, skill);
        return Result.success("Updated", null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteSkill(@PathVariable Long id, HttpServletRequest request) {
        Long providerId = (Long) request.getAttribute("userId");
        skillService.deleteSkill(providerId, id);
        return Result.success("Deleted", null);
    }

    @GetMapping("/{skillId}/schedule")
    public Result<List<Schedule>> getSkillSchedule(
            @PathVariable Long skillId,
            @RequestParam String date
    ) {
        Skill skill = skillService.getSkillById(skillId);
        if (skill == null) {
            return Result.error("Skill not found");
        }
        List<Schedule> schedules = scheduleService.getAvailableSlots(skill.getProviderId(), date);
        return Result.success(schedules);
    }

    @GetMapping("/{skillId}/my-schedule")
    public Result<List<Schedule>> getMySkillSchedule(
            @PathVariable Long skillId,
            @RequestParam(required = false) String date,
            HttpServletRequest request
    ) {
        Long providerId = (Long) request.getAttribute("userId");
        Skill skill = skillService.getSkillById(skillId);
        if (skill == null) {
            return Result.error("Skill not found");
        }
        if (!skill.getProviderId().equals(providerId)) {
            return Result.error("No permission");
        }
        return Result.success(scheduleService.getMySkillSchedule(providerId, skillId, date));
    }

    @PostMapping("/{skillId}/schedule")
    public Result<Void> addSchedule(
            @PathVariable Long skillId,
            @RequestBody Map<String, Object> payload,
            HttpServletRequest request
    ) {
        Long providerId = (Long) request.getAttribute("userId");
        Skill skill = skillService.getSkillById(skillId);
        if (skill == null) {
            return Result.error("Skill not found");
        }
        if (!skill.getProviderId().equals(providerId)) {
            return Result.error("No permission");
        }

        String date = (String) payload.get("date");
        String timeSlot = (String) payload.get("timeSlot");
        String location = (String) payload.get("location");
        if (date == null || timeSlot == null) {
            return Result.error("date and timeSlot are required");
        }

        Schedule schedule = new Schedule();
        schedule.setDate(LocalDate.parse(date));
        schedule.setTimeSlot(timeSlot);
        schedule.setLocation(location);

        scheduleService.addSchedule(providerId, skillId, schedule);
        return Result.success("Schedule created", null);
    }

    @DeleteMapping("/{skillId}/schedule/{scheduleId}")
    public Result<Void> deleteSchedule(
            @PathVariable Long skillId,
            @PathVariable Long scheduleId,
            HttpServletRequest request
    ) {
        Long providerId = (Long) request.getAttribute("userId");
        Skill skill = skillService.getSkillById(skillId);
        if (skill == null) {
            return Result.error("Skill not found");
        }
        if (!skill.getProviderId().equals(providerId)) {
            return Result.error("No permission");
        }

        scheduleService.deleteSchedule(providerId, scheduleId);
        return Result.success("Schedule deleted", null);
    }
}
