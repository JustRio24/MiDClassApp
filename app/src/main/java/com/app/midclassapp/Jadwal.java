package com.app.midclassapp;

public class Jadwal {
    private String dosen;
    private String matkul;
    private String hari;
    private String ruang;
    private String waktuMulai;
    private String waktuSelesai;

    public Jadwal(String dosen, String matkul, String hari, String ruang, String waktuMulai, String waktuSelesai) {
        this.dosen = dosen;
        this.matkul = matkul;
        this.hari = hari;
        this.ruang = ruang;
        this.waktuMulai = waktuMulai;
        this.waktuSelesai = waktuSelesai;
    }

    public String getDosen() {
        return dosen;
    }

    public String getMatkul() {
        return matkul;
    }

    public String getHari() {
        return hari;
    }

    public String getRuang() {
        return ruang;
    }

    public String getWaktuMulai() {
        return waktuMulai;
    }

    public String getWaktuSelesai() {
        return waktuSelesai;
    }
}
