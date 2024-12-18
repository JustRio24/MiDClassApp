package com.app.midclassapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// Adapter yang menghubungkan data jadwal dengan tampilan, memungkinkan data ditampilkan dalam format yang rapi di UI
public class JadwalAdapter extends RecyclerView.Adapter<JadwalAdapter.JadwalViewHolder> {

    private List<Jadwal> jadwalList; // Menyimpan daftar jadwal

    // Constructor untuk menginisialisasi data yang akan ditampilkan
    public JadwalAdapter(List<Jadwal> jadwalList) {
        this.jadwalList = jadwalList;
    }

    // Digunakan untuk membuat ViewHolder baru untuk setiap item di RecyclerView
    @NonNull
    @Override
    public JadwalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout untuk setiap item (mengubah layout XML menjadi tampilan di layar)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_jadwal, parent, false);
        return new JadwalViewHolder(view); // Mengembalikan ViewHolder yang akan digunakan untuk item ini
    }

    // Digunakan untuk mengikat data ke tampilan yang telah dibuat
    @Override
    public void onBindViewHolder(@NonNull JadwalViewHolder holder, int position) {
        // Mengambil data jadwal berdasarkan posisi dalam daftar
        Jadwal jadwal = jadwalList.get(position);

        // Jika jadwal tidak null, tampilkan data ke tampilan
        if (jadwal != null) {
            holder.dosenTextView.setText(jadwal.getDosen()); // Menampilkan nama dosen
            holder.matkulTextView.setText(jadwal.getMatkul()); // Menampilkan mata kuliah
            holder.hariTextView.setText(jadwal.getHari()); // Menampilkan hari
            holder.ruangTextView.setText(jadwal.getRuang()); // Menampilkan ruang
            holder.waktuTextView.setText(jadwal.getWaktuMulai() + " - " + jadwal.getWaktuSelesai()); // Menampilkan waktu
        } else {
            // Jika data jadwal kosong, tampilkan "Data tidak ditemukan"
            holder.dosenTextView.setText("Data tidak ditemukan");
            holder.matkulTextView.setText("Data tidak ditemukan");
            holder.hariTextView.setText("Data tidak ditemukan");
            holder.ruangTextView.setText("Data tidak ditemukan");
            holder.waktuTextView.setText("Data tidak ditemukan");
        }
    }

    // Mengembalikan jumlah item yang akan ditampilkan di RecyclerView
    @Override
    public int getItemCount() {
        return jadwalList.size(); // Mengembalikan jumlah item dalam daftar jadwal
    }

    // ViewHolder untuk setiap item di dalam RecyclerView
    public static class JadwalViewHolder extends RecyclerView.ViewHolder {

        // Menyimpan referensi ke elemen tampilan yang ada di layout item_jadwal.xml
        TextView dosenTextView, matkulTextView, hariTextView, ruangTextView, waktuTextView;

        // Konstruktor untuk menginisialisasi tampilan setiap item
        public JadwalViewHolder(View itemView) {
            super(itemView);
            // Menghubungkan elemen tampilan dengan ID di layout item_jadwal.xml
            dosenTextView = itemView.findViewById(R.id.dosenTextView);
            matkulTextView = itemView.findViewById(R.id.matkulTextView);
            hariTextView = itemView.findViewById(R.id.hariTextView);
            ruangTextView = itemView.findViewById(R.id.ruangTextView);
            waktuTextView = itemView.findViewById(R.id.waktuTextView);
        }
    }
}
