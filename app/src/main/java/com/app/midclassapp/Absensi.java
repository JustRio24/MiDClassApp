package com.app.midclassapp;

import com.google.firebase.Timestamp;

public class Absensi {
    private String matkul;
    private Timestamp timestamp;
    private String keterangan;
    private String fotoBase64;

    // Constructor
    public Absensi(String matkul, Timestamp timestamp, String keterangan, String fotoBase64) {
        this.matkul = matkul;
        this.timestamp = timestamp;
        this.keterangan = keterangan;
        this.fotoBase64 = fotoBase64;
    }

    // Getter and Setter
    public String getMatkul() {
        return matkul;
    }

    public void setMatkul(String matkul) {
        this.matkul = matkul;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getFotoBase64() {
        return fotoBase64;
    }

    public void setFotoBase64(String fotoBase64) {
        this.fotoBase64 = fotoBase64;
    }
}
