package com.ui.fitit.data.model;

import androidx.annotation.NonNull;

public enum Attendance {
    COMPLETED("Completed"),
    MISSED("Missed"),
    UPCOMING("Upcoming");

    private final String name;

    Attendance(String s) {
        name = s;
    }

    @NonNull
    public String toString() {
        return this.name;
    }
}