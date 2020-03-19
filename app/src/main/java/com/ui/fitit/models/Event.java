package com.ui.fitit.models;

import java.sql.Time;
import java.util.List;

public class Event {
    private Time start;
    private Time end;
    private List<Session> sessionList;
    private String place;

    public Event(Time start, Time end, List<Session> sessionList, String place) {
        this.start = start;
        this.end = end;
        this.sessionList = sessionList;
        this.place = place;
    }

    public Time getStart() {
        return start;
    }

    public void setStart(Time start) {
        this.start = start;
    }

    public Time getEnd() {
        return end;
    }

    public void setEnd(Time end) {
        this.end = end;
    }

    public List<Session> getSessionList() {
        return sessionList;
    }

    public void setSessionList(List<Session> sessionList) {
        this.sessionList = sessionList;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }
}
