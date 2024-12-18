package com.app.midclassapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Map;

public class ValidasiPresensiAdapter extends RecyclerView.Adapter<ValidasiPresensiAdapter.ViewHolder> {

    private Context context; // Context untuk mengakses resources dan layout inflater
    private ArrayList<Absensi> historyAbsensi; // List data absensi yang akan ditampilkan
    private Map<String, Absensi> selectedAbsensiMap; // Map untuk menyimpan absensi yang dipilih

    // Constructor adapter, menerima context, list data absensi, dan map absensi yang dipilih
    public ValidasiPresensiAdapter(Context context, ArrayList<Absensi> historyAbsensi, Map<String, Absensi> selectedAbsensiMap) {
        this.context = context;
        this.historyAbsensi = historyAbsensi;
        this.selectedAbsensiMap = selectedAbsensiMap;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Menginflate layout item untuk setiap item dalam RecyclerView
        View view = LayoutInflater.from(context).inflate(R.layout.item_validasi_presensi, parent, false);
        return new ViewHolder(view); // Mengembalikan ViewHolder untuk item tersebut
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Absensi absensi = historyAbsensi.get(position); // Mengambil data absensi berdasarkan posisi

        // Set data untuk tampilan
        holder.nimTextView.setText(absensi.getNimOrNip()); // Menampilkan NIM atau NIP mahasiswa
        holder.keteranganTextView.setText(absensi.getKeterangan()); // Menampilkan keterangan absensi

        // Format timestamp jika ada
        if (absensi.getTimestamp() != null) {
            holder.timestampTextView.setText(ValidasiPresensiActivity.formatTimestamp(absensi.getTimestamp())); // Menampilkan timestamp yang sudah diformat
        }

        // Decode foto dan tampilkan pada ImageView
        if (absensi.getFotoBase64() != null) {
            Bitmap bitmap = ValidasiPresensiActivity.decodeBase64(absensi.getFotoBase64()); // Decode Base64 ke Bitmap
            holder.fotoImageView.setImageBitmap(bitmap); // Menampilkan foto dalam ImageView
        }

        // Set status checkbox berdasarkan apakah absensi sudah dipilih atau belum
        holder.checkBox.setChecked(selectedAbsensiMap.containsKey(absensi.getDocumentId()));
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Jika checkbox dicentang, tambahkan absensi ke map
                selectedAbsensiMap.put(absensi.getDocumentId(), absensi);
            } else {
                // Jika checkbox tidak dicentang, hapus absensi dari map
                selectedAbsensiMap.remove(absensi.getDocumentId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return historyAbsensi.size(); // Mengembalikan jumlah item yang ada dalam list histori absensi
    }

    // ViewHolder untuk item yang akan ditampilkan dalam RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nimTextView, keteranganTextView, timestampTextView; // TextViews untuk menampilkan NIM, keterangan, dan timestamp
        ImageView fotoImageView; // ImageView untuk menampilkan foto
        CheckBox checkBox; // Checkbox untuk memilih absensi

        // Constructor ViewHolder untuk inisialisasi komponen tampilan
        public ViewHolder(View itemView) {
            super(itemView);
            nimTextView = itemView.findViewById(R.id.nimTextView); // Inisialisasi TextView untuk NIM
            keteranganTextView = itemView.findViewById(R.id.keteranganTextView); // Inisialisasi TextView untuk keterangan
            timestampTextView = itemView.findViewById(R.id.timestampTextView); // Inisialisasi TextView untuk timestamp
            fotoImageView = itemView.findViewById(R.id.fotoImageView); // Inisialisasi ImageView untuk foto
            checkBox = itemView.findViewById(R.id.checkBox); // Inisialisasi CheckBox untuk memilih absensi
        }
    }
}
