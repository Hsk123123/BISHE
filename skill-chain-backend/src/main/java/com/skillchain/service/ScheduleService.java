package com.skillchain.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.skillchain.entity.Schedule;
import com.skillchain.mapper.ScheduleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleMapper scheduleMapper;

    public List<Schedule> getProviderSchedule(Long providerId, String date) {
        LambdaQueryWrapper<Schedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Schedule::getProviderId, providerId);
        if (date != null && !date.isEmpty()) {
            wrapper.eq(Schedule::getDate, LocalDate.parse(date));
        }
        wrapper.orderByAsc(Schedule::getDate, Schedule::getTimeSlot);
        return scheduleMapper.selectList(wrapper);
    }

    @Transactional
    public void addSchedule(Long providerId, Schedule schedule) {
        schedule.setProviderId(providerId);
        schedule.setStatus(0);
        scheduleMapper.insert(schedule);
    }

    @Transactional
    public void deleteSchedule(Long providerId, Long scheduleId) {
        Schedule existSchedule = scheduleMapper.selectById(scheduleId);
        if (existSchedule == null) {
            throw new RuntimeException("排期不存在");
        }
        if (!existSchedule.getProviderId().equals(providerId)) {
            throw new RuntimeException("无权操作");
        }

        scheduleMapper.deleteById(scheduleId);
    }

    @Transactional
    public void bookSchedule(Long scheduleId) {
        // 使用悲观锁查询，防止并发预约
        Schedule schedule = scheduleMapper.selectById(scheduleId);
        if (schedule == null) {
            throw new RuntimeException("排期不存在");
        }
        if (schedule.getStatus() != 0) {
            throw new RuntimeException("该时间段已被预约");
        }

        // 立即更新状态为已预约（1）
        schedule.setStatus(1);
        int rows = scheduleMapper.updateById(schedule);
        if (rows == 0) {
            throw new RuntimeException("预约失败，请重试");
        }
    }

    /**
     * 预锁定时间槽，用于订单创建前的时间槽预留
     * @param scheduleId 排期ID
     * @return 是否锁定成功
     */
    @Transactional
    public boolean preLockSchedule(Long scheduleId) {
        Schedule schedule = scheduleMapper.selectById(scheduleId);
        if (schedule == null) {
            return false;
        }
        if (schedule.getStatus() != 0) {
            return false;
        }

        // 设置状态为预锁定（2）
        schedule.setStatus(2);
        int rows = scheduleMapper.updateById(schedule);
        return rows > 0;
    }

    /**
     * 释放预锁定的时间槽
     * @param scheduleId 排期ID
     */
    @Transactional
    public void releasePreLockSchedule(Long scheduleId) {
        Schedule schedule = scheduleMapper.selectById(scheduleId);
        if (schedule != null && schedule.getStatus() == 2) {
            schedule.setStatus(0);
            scheduleMapper.updateById(schedule);
        }
    }

    /**
     * 确认锁定时间槽（将预锁定状态转为已预约状态）
     * @param scheduleId 排期ID
     */
    @Transactional
    public void confirmLockSchedule(Long scheduleId) {
        Schedule schedule = scheduleMapper.selectById(scheduleId);
        if (schedule == null) {
            throw new RuntimeException("排期不存在");
        }
        if (schedule.getStatus() != 2) {
            throw new RuntimeException("该时间段已被预约或锁定已过期");
        }

        // 设置状态为已预约（1）
        schedule.setStatus(1);
        int rows = scheduleMapper.updateById(schedule);
        if (rows == 0) {
            throw new RuntimeException("预约失败，请重试");
        }
    }

    @Transactional
    public void releaseSchedule(Long scheduleId) {
        Schedule schedule = scheduleMapper.selectById(scheduleId);
        if (schedule == null) {
            throw new RuntimeException("排期不存在");
        }

        schedule.setStatus(0);
        scheduleMapper.updateById(schedule);
    }

    public List<Schedule> getAvailableSlots(Long providerId, String date) {
        return scheduleMapper.selectList(
                new LambdaQueryWrapper<Schedule>()
                        .eq(Schedule::getProviderId, providerId)
                        .eq(Schedule::getDate, LocalDate.parse(date))
                        .eq(Schedule::getStatus, 0)
                        .orderByAsc(Schedule::getTimeSlot)
        );
    }

    public Schedule findByProviderDateSlot(Long providerId, String date, String timeSlot) {
        return scheduleMapper.selectOne(
                new LambdaQueryWrapper<Schedule>()
                        .eq(Schedule::getProviderId, providerId)
                        .eq(Schedule::getDate, LocalDate.parse(date))
                        .eq(Schedule::getTimeSlot, timeSlot)
        );
    }
}