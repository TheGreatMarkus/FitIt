package com.ui.fitit.data.model;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
public class Event {
    private String name;
    private String description;
    private LocalTime start;
    private LocalTime end;
    private LocalDate startDate;
    private String location;
}
