package com.app.midclassapp;

public class AcademicEvent {

    private String no;
    private String eventName;
    private String dateRange;

    public AcademicEvent(String no, String eventName, String dateRange) {
        this.no = no;
        this.eventName = eventName;
        this.dateRange = dateRange;
    }

    public String getNo() {
        return no;
    }

    public String getEventName() {
        return eventName;
    }

    public String getDateRange() {
        return dateRange;
    }
}
