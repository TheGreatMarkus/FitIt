package com.ui.fitit.data.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleItem {
    private Event event;
    private Session session;
    private String name;
    private String location;
    private Attendance attendance;
    private Frequency frequency;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    public ScheduleItem(Event event, Session session) {
        this.event = event;
        this.session = session;
        this.name = event.name;
        this.location = event.location;
        this.attendance = session.attendance;
        this.frequency = event.frequency;
        this.date = session.date.toLocalDate();
        this.startTime = event.startTime.toLocalTime();
        this.endTime = event.endTime.toLocalTime();
    }

    public int compareTo(ScheduleItem o) {
        return LocalDateTime.of(date, startTime).compareTo(LocalDateTime.of(o.date, o.startTime));
    }
}
