package com.skillchain.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.skillchain.entity.Schedule;
import com.skillchain.exception.BusinessException;
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
        wrapper.eq(Schedule::getProviderId, providerId)
                .eq(Schedule::getDeleted, 0);
        if (date != null && !date.isEmpty()) {
            wrapper.eq(Schedule::getDate, LocalDate.parse(date));
        }
        wrapper.orderByAsc(Schedule::getDate, Schedule::getTimeSlot);
        return scheduleMapper.selectList(wrapper);
    }

    @Transactional
    public void addSchedule(Long providerId, Long skillId, Schedule schedule) {
        Schedule duplicate = scheduleMapper.selectOne(
                new LambdaQueryWrapper<Schedule>()
                        .eq(Schedule::getProviderId, providerId)
                        .eq(Schedule::getDate, schedule.getDate())
                        .eq(Schedule::getTimeSlot, schedule.getTimeSlot())
                        .eq(Schedule::getDeleted, 0)
        );
        if (duplicate != null) {
            throw new BusinessException("该时间段已被占用，请选择其他时间");
        }

        schedule.setProviderId(providerId);
        schedule.setSkillId(skillId);
        schedule.setStatus(0);
        if (schedule.getDeleted() == null) {
            schedule.setDeleted(0);
        }
        scheduleMapper.insert(schedule);
    }

    @Transactional
    public void deleteSchedule(Long providerId, Long scheduleId) {
        Schedule existSchedule = scheduleMapper.selectById(scheduleId);
        if (existSchedule == null || Integer.valueOf(1).equals(existSchedule.getDeleted())) {
            throw new BusinessException("时间段不存在");
        }
        if (!existSchedule.getProviderId().equals(providerId)) {
            throw new BusinessException(403, "无权操作");
        }
        if (existSchedule.getStatus() != null && existSchedule.getStatus() != 0) {
            throw new BusinessException("只能删除空闲状态的时间段，已预约或锁定的时间段不可删除");
        }

        scheduleMapper.deleteById(scheduleId);
    }

    public List<Schedule> getMySkillSchedule(Long providerId, Long skillId, String date) {
        LambdaQueryWrapper<Schedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Schedule::getProviderId, providerId)
                .eq(Schedule::getSkillId, skillId)
                .eq(Schedule::getDeleted, 0);

        if (date != null && !date.isEmpty()) {
            wrapper.eq(Schedule::getDate, LocalDate.parse(date));
        }
        wrapper.orderByAsc(Schedule::getDate, Schedule::getTimeSlot);
        return scheduleMapper.selectList(wrapper);
    }

    @Transactional
    public void bookSchedule(Long scheduleId) {
        Schedule schedule = scheduleMapper.selectById(scheduleId);
        if (schedule == null || Integer.valueOf(1).equals(schedule.getDeleted())) {
            throw new BusinessException("时间段不存在");
        }
        if (schedule.getStatus() != 0) {
            throw new BusinessException("该时间段已被预约");
        }

        schedule.setStatus(1);
        int rows = scheduleMapper.updateById(schedule);
        if (rows == 0) {
            throw new BusinessException("预约失败，请稍后重试");
        }
    }

    @Transactional
    public boolean preLockSchedule(Long scheduleId) {
        Schedule schedule = scheduleMapper.selectById(scheduleId);
        if (schedule == null || Integer.valueOf(1).equals(schedule.getDeleted())) {
            return false;
        }
        if (schedule.getStatus() != 0) {
            return false;
        }

        schedule.setStatus(2);
        int rows = scheduleMapper.updateById(schedule);
        return rows > 0;
    }

    @Transactional
    public void releasePreLockSchedule(Long scheduleId) {
        Schedule schedule = scheduleMapper.selectById(scheduleId);
        if (schedule != null && !Integer.valueOf(1).equals(schedule.getDeleted()) && schedule.getStatus() == 2) {
            schedule.setStatus(0);
            scheduleMapper.updateById(schedule);
        }
    }

    @Transactional
    public void confirmLockSchedule(Long scheduleId) {
        Schedule schedule = scheduleMapper.selectById(scheduleId);
        if (schedule == null || Integer.valueOf(1).equals(schedule.getDeleted())) {
            throw new BusinessException("时间段不存在");
        }
        if (schedule.getStatus() != 2) {
            throw new BusinessException("时间段状态异常");
        }

        schedule.setStatus(1);
        int rows = scheduleMapper.updateById(schedule);
        if (rows == 0) {
            throw new BusinessException("确认预约失败，请稍后重试");
        }
    }

    @Transactional
    public void releaseSchedule(Long scheduleId) {
        Schedule schedule = scheduleMapper.selectById(scheduleId);
        if (schedule == null || Integer.valueOf(1).equals(schedule.getDeleted())) {
            throw new BusinessException("时间段不存在");
        }

        schedule.setStatus(0);
        scheduleMapper.updateById(schedule);
    }

    public List<Schedule> getAvailableSlots(Long providerId, String date) {
        return scheduleMapper.selectList(
                new LambdaQueryWrapper<Schedule>()
                        .eq(Schedule::getProviderId, providerId)
                        .eq(Schedule::getDate, LocalDate.parse(date))
                        .in(Schedule::getStatus, 0, 1, 2)
                        .eq(Schedule::getDeleted, 0)
                        .orderByAsc(Schedule::getTimeSlot)
        );
    }

    public Schedule findByProviderDateSlot(Long providerId, String date, String timeSlot) {
        return scheduleMapper.selectOne(
                new LambdaQueryWrapper<Schedule>()
                        .eq(Schedule::getProviderId, providerId)
                        .eq(Schedule::getDate, LocalDate.parse(date))
                        .eq(Schedule::getTimeSlot, timeSlot)
                        .eq(Schedule::getDeleted, 0)
        );
    }
}
