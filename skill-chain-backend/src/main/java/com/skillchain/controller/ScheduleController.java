package com.skillchain.controller;

import com.skillchain.common.Result;
import com.skillchain.entity.Schedule;
import com.skillchain.exception.BusinessException;
import com.skillchain.service.ScheduleService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    /**
     * GET /schedule/my
     * 返回当前服务者所有时间段（支持可选 date 过滤）
     */
    @GetMapping("/my")
    public Result<List<Schedule>> getMySchedules(
            @RequestParam(required = false) String date,
            HttpServletRequest request) {
        Long providerId = (Long) request.getAttribute("userId");
        List<Schedule> list = scheduleService.getProviderSchedule(providerId, date);
        return Result.success(list);
    }

    /**
     * POST /schedule/add
     * body: { date: "2026-04-10", timeSlot: "上午(09:00-12:00)" }
     * 不绑定具体技能，作为服务者通用可用时间段
     */
    @PostMapping("/add")
    public Result<Void> addSchedule(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        Long providerId = (Long) request.getAttribute("userId");

        Object dateObj = body.get("date");
        Object slotObj = body.get("timeSlot");
        if (dateObj == null || slotObj == null) {
            throw new BusinessException("date 和 timeSlot 不能为空");
        }

        LocalDate date = LocalDate.parse(dateObj.toString());
        if (date.isBefore(LocalDate.now())) {
            throw new BusinessException("不能添加过去的时间段");
        }

        Schedule schedule = new Schedule();
        schedule.setDate(date);
        schedule.setTimeSlot(slotObj.toString());
        schedule.setDeleted(0);

        // skillId 传 null：通用时间段，不绑定具体技能
        scheduleService.addSchedule(providerId, null, schedule);
        return Result.success("添加成功", null);
    }

    /**
     * DELETE /schedule/{id}
     * 删除时间段（仅能删除自己的且状态为空闲的）
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteSchedule(@PathVariable Long id, HttpServletRequest request) {
        Long providerId = (Long) request.getAttribute("userId");
        scheduleService.deleteSchedule(providerId, id);
        return Result.success("删除成功", null);
    }
}
