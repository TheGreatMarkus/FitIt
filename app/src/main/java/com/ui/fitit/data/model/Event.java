package com.ui.fitit.data.model;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Event {

    private final String id = UUID.randomUUID().toString();
    private String name;
    private String description;
    private FitTime start;
    private FitTime end;
    private FitDate startDate;
    private String location;
}
