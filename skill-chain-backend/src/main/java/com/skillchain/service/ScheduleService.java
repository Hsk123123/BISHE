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
            throw new RuntimeException("Schedule already exists for this time slot");
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
            throw new RuntimeException("Schedule not found");
        }
        if (!existSchedule.getProviderId().equals(providerId)) {
            throw new RuntimeException("No permission");
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
            throw new RuntimeException("Schedule not found");
        }
        if (schedule.getStatus() != 0) {
            throw new RuntimeException("Time slot already booked");
        }

        schedule.setStatus(1);
        int rows = scheduleMapper.updateById(schedule);
        if (rows == 0) {
            throw new RuntimeException("Book schedule failed");
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
            throw new RuntimeException("Schedule not found");
        }
        if (schedule.getStatus() != 2) {
            throw new RuntimeException("Schedule is not in pre-locked status");
        }

        schedule.setStatus(1);
        int rows = scheduleMapper.updateById(schedule);
        if (rows == 0) {
            throw new RuntimeException("Confirm schedule failed");
        }
    }

    @Transactional
    public void releaseSchedule(Long scheduleId) {
        Schedule schedule = scheduleMapper.selectById(scheduleId);
        if (schedule == null || Integer.valueOf(1).equals(schedule.getDeleted())) {
            throw new RuntimeException("Schedule not found");
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
