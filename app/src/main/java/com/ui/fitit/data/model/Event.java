package com.ui.fitit.data.model;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Event {
    final String id = UUID.randomUUID().toString();
    String name;
    String description;
    String location;

    FitTime startTime;
    FitTime endTime;
    FitDate startDate;
    Frequency frequency;
}
