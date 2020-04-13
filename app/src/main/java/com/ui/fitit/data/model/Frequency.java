package com.ui.fitit.data.model;

import androidx.annotation.NonNull;

import lombok.Getter;

@Getter
public enum Frequency {

    ONCE("Once", -1),
    DAILY("Daily", 1),
    WEEKLY("Weekly", 7);

    private final String name;
    private final int daysTillNext;

    Frequency(String s, int i) {
        name = s;
        daysTillNext = i;
    }

    @NonNull
    public String toString() {
        return this.name;
    }


}
