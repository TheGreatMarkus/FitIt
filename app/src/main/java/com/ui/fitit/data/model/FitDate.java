package com.ui.fitit.data.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Month;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FitDate implements Serializable {

    private int year;
    private Month month;
    private int day;

    public FitDate(LocalDate date) {
        this.year = date.getYear();
        this.month = date.getMonth();
        this.day = date.getDayOfMonth();
    }

    public LocalDate toLocalDate() {
        return LocalDate.of(year, month, day);
    }

    public int compareTo(FitDate o) {
        return this.toLocalDate().compareTo(o.toLocalDate());
    }


}

