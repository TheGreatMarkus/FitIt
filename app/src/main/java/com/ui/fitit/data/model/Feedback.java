package com.ui.fitit.data.model;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Feedback {
    private final String id = UUID.randomUUID().toString();
    String userName;
    FitDate date;
    @Motivation int motivation;
    double successPercentage;
    @PostSessionFeeling int postSessionFeeling;
}
