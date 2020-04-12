package com.ui.fitit.data.model;

import java.time.LocalDate;
import java.time.Month;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FitDate {

    private int year;
    private Month month;
    private int day;

    public FitDate(LocalDate date) {
        this.year = date.getYear();
        this.month = date.getMonth();
        this.day = date.getDayOfMonth();
    }

    LocalDate toLocalDate() {
        return LocalDate.of(year, month, day);
    }


}

