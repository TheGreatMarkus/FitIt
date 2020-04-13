package com.ui.fitit.data.model;

import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FitTime {
    private int hour;
    private int minute;

    public FitTime(LocalTime time) {
        this.hour = time.getHour();
        this.minute = time.getMinute();
    }

    public LocalTime toLocalTime() {
        return LocalTime.of(hour, minute);
    }

    public int compareTo(FitTime o) {
        return this.toLocalTime().compareTo(o.toLocalTime());
    }
}
