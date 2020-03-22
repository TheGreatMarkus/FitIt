package com.ui.fitit.models;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Session {
    private LocalDate date;
    private boolean attended;
}
