package com.ui.fitit.data.model;

import java.time.LocalTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Event {
    private LocalTime start;
    private LocalTime end;
    private List<Session> sessionList;
    private String place;
}
