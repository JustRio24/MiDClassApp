package com.app.midclassapp;

public class TugasMateri {
    private String title;
    private String description;
    private String deadline;
    private String matkul;

    public TugasMateri(String title, String description, String deadline, String matkul) {
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.matkul = matkul;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDeadline() {
        return deadline;
    }

    public String getMatkul() {
        return matkul;
    }
}
