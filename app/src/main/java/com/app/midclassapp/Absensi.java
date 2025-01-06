package com.app.midclassapp;

import com.google.firebase.Timestamp;

public class Absensi {
    private String documentId;
    private String matkul;
    private Timestamp timestamp;
    private String keterangan;
    private String fotoBase64;
    private String nimOrNip;
    private String namaMahasiswa;
    private String tanggal; // Menambahkan field tanggal

    public Absensi() {
        // Required for Firestore deserialization
    }

    // Konstruktor dengan semua parameter untuk ValidasiPresensiAdapter (Dosen)
    public Absensi(String documentId, String matkul, Timestamp timestamp, String keterangan, String fotoBase64, String nimOrNip, String tanggal) {
        this.documentId = documentId;
        this.matkul = matkul;
        this.timestamp = timestamp;
        this.keterangan = keterangan;
        this.fotoBase64 = fotoBase64;
        this.nimOrNip = nimOrNip;
        this.tanggal = tanggal; // Set tanggal
    }

    public Absensi(String matkul, Timestamp timestamp, String keterangan, String fotoBase64, String tanggal, String nimOrNip) {
        this.matkul = matkul;
        this.timestamp = timestamp;
        this.keterangan = keterangan;
        this.fotoBase64 = fotoBase64;
        this.tanggal = tanggal;
        this.nimOrNip = nimOrNip;
    }

    // Konstruktor 4 parameter untuk HistoriAbsensiAdapter (Mahasiswa)
    public Absensi(String matkul, Timestamp timestamp, String keterangan, String fotoBase64, String tanggal) {
        this.matkul = matkul;
        this.timestamp = timestamp;
        this.keterangan = keterangan;
        this.fotoBase64 = fotoBase64;
        this.tanggal = tanggal; // Set tanggal
    }

    // Getter dan Setter untuk documentId
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    // Getter dan Setter untuk matkul
    public String getMatkul() {
        return matkul;
    }

    public void setMatkul(String matkul) {
        this.matkul = matkul;
    }

    // Getter dan Setter untuk timestamp
    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    // Getter dan Setter untuk keterangan
    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getPhoto() {
        return fotoBase64;
    }

    public void setPhoto(String photo){
        this.fotoBase64 = photo;
    }

    public String getFotoBase64() {
        return fotoBase64;
    }

    public void setFotoBase64(String fotoBase64) {
        this.fotoBase64 = fotoBase64;
    }


    // Getter dan Setter untuk nimOrNip
    public String getNimOrNip() {
        return nimOrNip;
    }

    public void setNimOrNip(String nimOrNip) {
        this.nimOrNip = nimOrNip;
    }

    // Getter dan Setter untuk namaMahasiswa
    public String getNamaMahasiswa() {
        return namaMahasiswa;
    }

    public void setNamaMahasiswa(String namaMahasiswa) {
        this.namaMahasiswa = namaMahasiswa;
    }

    // Getter dan Setter untuk tanggal
    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }
}
