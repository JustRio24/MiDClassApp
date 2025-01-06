package com.app.midclassapp;

// model data yang menyimpan informasi jadwal, seperti mata kuliah, waktu, hari, dan ruang kelas.
public class Jadwal {
    private String id;
    private String dosen;
    private String matkul;
    private String hari;
    private String ruang;
    private String waktuMulai;
    private String waktuSelesai;

    public Jadwal() {}

    public Jadwal(String dosen, String matkul, String hari, String ruang, String waktuMulai, String waktuSelesai) {
        this.dosen = dosen;
        this.matkul = matkul;
        this.hari = hari;
        this.ruang = ruang;
        this.waktuMulai = waktuMulai;
        this.waktuSelesai = waktuSelesai;
    }

    public Jadwal(String id, String dosen, String matkul, String hari, String ruang, String waktuMulai, String waktuSelesai) {
        this.id = id;
        this.dosen = dosen;
        this.matkul = matkul;
        this.hari = hari;
        this.ruang = ruang;
        this.waktuMulai = waktuMulai;
        this.waktuSelesai = waktuSelesai;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDosen() {
        return dosen;
    }

    public void setDosen(String dosen) {
        this.dosen = dosen;
    }

    public String getMatkul() {
        return matkul;
    }

    public void setMatkul(String matkul) {
        this.matkul = matkul;
    }

    public String getHari() {
        return hari;
    }

    public void setHari(String hari) {
        this.hari = hari;
    }

    public String getRuang() {
        return ruang;
    }

    public void setRuang(String ruang) {
        this.ruang = ruang;
    }

    public String getWaktuMulai() {
        return waktuMulai;
    }

    public void setWaktuMulai(String waktuMulai) {
        this.waktuMulai = waktuMulai;
    }

    public String getWaktuSelesai() {
        return waktuSelesai;
    }

    public void setWaktuSelesai(String waktuSelesai) {
        this.waktuSelesai = waktuSelesai;
    }
}
