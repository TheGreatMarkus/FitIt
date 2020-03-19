package com.ui.fitit.models;

import java.time.LocalDate;

public class Session {
    private LocalDate date;
    private boolean attended;

    public Session(LocalDate date, boolean attended) {
        this.date = date;
        this.attended = attended;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public boolean isAttended() {
        return attended;
    }

    public void setAttended(boolean attended) {
        this.attended = attended;
    }
}
