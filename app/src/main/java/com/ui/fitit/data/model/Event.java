package com.ui.fitit.data.model;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Event {
    private LocalTime start;
    private LocalTime end;
    private LocalDate startDate;
    private String description;
    private String name;
    private String location;
    //private Frequency frequency;
}
