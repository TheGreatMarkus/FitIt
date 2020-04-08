package com.ui.fitit.data.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class Session {
    private LocalDate date;
    private Event event;
    private boolean attended;
}
