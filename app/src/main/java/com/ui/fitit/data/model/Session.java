package com.ui.fitit.data.model;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@Data
@NoArgsConstructor
public class Session {


    private final String id = UUID.randomUUID().toString();
    private FitDate date;
    private Event event;
    private Attendance attendance;
}
