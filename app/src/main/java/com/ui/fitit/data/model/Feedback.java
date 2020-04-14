package com.ui.fitit.data.model;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class Feedback {
    private final String id = UUID.randomUUID().toString();
    String userName;
    FitDate date;
    @Motivation
    int motivation;
    double successPercentage;
    @PostSessionFeeling
    int postSessionFeeling;

    public int compareTo(Feedback f) {
        return date.toLocalDate().compareTo(f.date.toLocalDate());
    }
}
