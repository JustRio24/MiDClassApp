package com.app.midclassapp;

// model data yang menyimpan informasi Event
public class AcademicEvent {
    private String no;
    private String eventName;
    private String dateRange;

    // Constructor kosong diperlukan oleh Firebase Firestore
    public AcademicEvent() {
        // Diperlukan untuk deserialisasi
    }

    public AcademicEvent(String no, String eventName, String dateRange) {
        this.no = no;
        this.eventName = eventName;
        this.dateRange = dateRange;
    }

    // Getter dan Setter
    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getDateRange() {
        return dateRange;
    }

    public void setDateRange(String dateRange) {
        this.dateRange = dateRange;
    }
}
