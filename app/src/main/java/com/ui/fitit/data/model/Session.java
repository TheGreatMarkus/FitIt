package com.ui.fitit.data.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class Session {
    private LocalDate date;
    private Event event;
    private boolean attended;
}
