package com.app.midclassapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class JadwalAdapter extends RecyclerView.Adapter<JadwalAdapter.JadwalViewHolder> {

    private List<Jadwal> jadwalList;

    // Constructor untuk menginisialisasi data
    public JadwalAdapter(List<Jadwal> jadwalList) {
        this.jadwalList = jadwalList;
    }

    @NonNull
    @Override
    public JadwalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout untuk setiap item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_jadwal, parent, false);
        return new JadwalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JadwalViewHolder holder, int position) {
        Jadwal jadwal = jadwalList.get(position);

        // Pastikan jadwal tidak null
        if (jadwal != null) {
            holder.dosenTextView.setText(jadwal.getDosen());
            holder.matkulTextView.setText(jadwal.getMatkul());
            holder.hariTextView.setText(jadwal.getHari());
            holder.ruangTextView.setText(jadwal.getRuang());
            holder.waktuTextView.setText(jadwal.getWaktuMulai() + " - " + jadwal.getWaktuSelesai());
        } else {
            // Log error jika data kosong
            holder.dosenTextView.setText("Data tidak ditemukan");
            holder.matkulTextView.setText("Data tidak ditemukan");
            holder.hariTextView.setText("Data tidak ditemukan");
            holder.ruangTextView.setText("Data tidak ditemukan");
            holder.waktuTextView.setText("Data tidak ditemukan");
        }
    }

    @Override
    public int getItemCount() {
        return jadwalList.size();
    }

    // ViewHolder untuk setiap item
    public static class JadwalViewHolder extends RecyclerView.ViewHolder {

        TextView dosenTextView, matkulTextView, hariTextView, ruangTextView, waktuTextView;

        public JadwalViewHolder(View itemView) {
            super(itemView);
            dosenTextView = itemView.findViewById(R.id.dosenTextView);
            matkulTextView = itemView.findViewById(R.id.matkulTextView);
            hariTextView = itemView.findViewById(R.id.hariTextView);
            ruangTextView = itemView.findViewById(R.id.ruangTextView);
            waktuTextView = itemView.findViewById(R.id.waktuTextView);
        }
    }
}
