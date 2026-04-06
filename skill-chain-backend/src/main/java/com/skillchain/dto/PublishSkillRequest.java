package com.skillchain.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class PublishSkillRequest {
    private Long categoryId;
    private String title;
    private String description;
    private Double pricePerUnit;
    private Integer unitType;
    private Integer scheduleRequired;
    private Integer serviceMode;
    private String mediaUrls;
    private List<ScheduleItem> schedules;

    @Data
    public static class ScheduleItem {
        private LocalDate date;
        private String timeSlot;
        private String location;
    }
}
