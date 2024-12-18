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

    // Konstruktor dengan semua parameter untuk ValidasiPresensiAdapter (Dosen)
    public Absensi(String documentId, String matkul, Timestamp timestamp, String keterangan, String fotoBase64, String nimOrNip) {
        this.documentId = documentId;
        this.matkul = matkul;
        this.timestamp = timestamp;
        this.keterangan = keterangan;
        this.fotoBase64 = fotoBase64;
        this.nimOrNip = nimOrNip;
    }

    // Konstruktor 4 parameter untuk HistoriAbsensiAdapter (mahasiswa)
    public Absensi(String matkul, Timestamp timestamp, String keterangan, String fotoBase64) {
        this.matkul =  matkul;
        this.timestamp = timestamp;
        this.keterangan = keterangan;
        this.fotoBase64 = fotoBase64;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

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

    public String getNimOrNip() {
        return nimOrNip;
    }

    public void setNimOrNip(String nimOrNip) {
        this.nimOrNip = nimOrNip;
    }

    public String getNamaMahasiswa() {
        return namaMahasiswa;
    }

    public void setNamaMahasiswa(String namaMahasiswa) {
        this.namaMahasiswa = namaMahasiswa;
    }
}
