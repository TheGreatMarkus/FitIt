package com.ui.fitit.data.model;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@Data
@NoArgsConstructor
public class Session {
    final String id = UUID.randomUUID().toString();
    FitDate date;
    String eventId;
    Attendance attendance;
}
