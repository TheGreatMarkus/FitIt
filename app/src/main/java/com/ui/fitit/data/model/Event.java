package com.ui.fitit.data.model;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
public class Event {
    private LocalTime start;
    private LocalTime end;
    private LocalDate startDate;
    private String description;
    private String name;
    private String location;
    //private Frequency frequency;
}
