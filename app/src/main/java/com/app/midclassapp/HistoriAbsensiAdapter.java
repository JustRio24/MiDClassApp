package com.app.midclassapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoriAbsensiAdapter extends RecyclerView.Adapter<HistoriAbsensiAdapter.AbsensiViewHolder> {

    private List<Absensi> historiAbsensi; // List yang menyimpan data absensi
    private List<Absensi> historiAbsensiFiltered; // List untuk menyimpan data hasil filter

    public HistoriAbsensiAdapter(List<Absensi> historiAbsensi) {
        this.historiAbsensi = historiAbsensi; // Konstruktor untuk inisialisasi list absensi
        this.historiAbsensiFiltered = historiAbsensi; // Secara default, data yang ditampilkan adalah historiAbsensi
    }

    @NonNull
    @Override
    public AbsensiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout untuk setiap item pada RecyclerView
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_absensi, parent, false);
        return new AbsensiViewHolder(view); // Mengembalikan ViewHolder yang baru
    }

    @Override
    public void onBindViewHolder(@NonNull AbsensiViewHolder holder, int position) {
        // Ambil data absensi berdasarkan posisi
        Absensi absensi = historiAbsensiFiltered.get(position);

        // Set data absensi ke komponen UI
        holder.matkulTextView.setText(absensi.getMatkul());
        holder.tanggalTextView.setText(formatTimestamp(absensi.getTimestamp())); // Format timestamp ke format yang diinginkan
        holder.keteranganTextView.setText(absensi.getKeterangan());

        // Jika ada foto, tampilkan foto dari Base64
        if (absensi.getFotoBase64() != null && !absensi.getFotoBase64().isEmpty()) {
            byte[] decodedString = Base64.decode(absensi.getFotoBase64(), Base64.DEFAULT); // Dekode foto dari base64
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length); // Mengubah byte array menjadi gambar
            holder.fotoImageView.setImageBitmap(decodedByte); // Set gambar ke ImageView
        } else {
            holder.fotoImageView.setImageResource(R.drawable.ic_placeholder); // Gambar placeholder jika tidak ada foto
        }
    }

    @Override
    public int getItemCount() {
        return historiAbsensiFiltered.size(); // Mengembalikan jumlah item di list yang sudah difilter
    }

    // Method untuk memperbarui data adapter dengan data baru
    public void updateData(List<Absensi> newData) {
        historiAbsensi.clear(); // Membersihkan data lama
        historiAbsensi.addAll(newData); // Menambahkan data baru
        historiAbsensiFiltered = historiAbsensi; // Menyimpan hasil filter terbaru
        notifyDataSetChanged(); // Memberitahu adapter untuk memperbarui tampilan
    }

    // Method untuk melakukan filter data berdasarkan kriteria tertentu
    public void filterData(String keyword) {
        // Jika kata kunci kosong, kembalikan ke data asli
        if (keyword == null || keyword.isEmpty()) {
            historiAbsensiFiltered = historiAbsensi;
        } else {
            // Filter data yang sesuai dengan keyword
            historiAbsensiFiltered.clear();
            for (Absensi absensi : historiAbsensi) {
                if (absensi.getMatkul().toLowerCase().contains(keyword.toLowerCase()) ||
                        absensi.getKeterangan().toLowerCase().contains(keyword.toLowerCase())) {
                    historiAbsensiFiltered.add(absensi);
                }
            }
        }
        notifyDataSetChanged(); // Update tampilan setelah filter
    }

    // Helper method untuk memformat timestamp menjadi string dengan format yang diinginkan
    private String formatTimestamp(Timestamp timestamp) {
        if (timestamp != null) {
            Date date = timestamp.toDate(); // Konversi Timestamp menjadi objek Date
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()); // Format tanggal
            return formatter.format(date); // Kembalikan hasil format tanggal
        } else {
            return "Tanggal tidak tersedia"; // Jika timestamp null, kembalikan pesan default
        }
    }

    // ViewHolder untuk setiap item
    public static class AbsensiViewHolder extends RecyclerView.ViewHolder {

        TextView matkulTextView, tanggalTextView, keteranganTextView; // TextView untuk menampilkan data absensi
        ImageView fotoImageView; // ImageView untuk menampilkan foto

        public AbsensiViewHolder(View itemView) {
            super(itemView);
            matkulTextView = itemView.findViewById(R.id.matkulTextView); // Menghubungkan matkulTextView dengan layout
            tanggalTextView = itemView.findViewById(R.id.tanggalTextView); // Menghubungkan tanggalTextView dengan layout
            keteranganTextView = itemView.findViewById(R.id.keteranganTextView); // Menghubungkan keteranganTextView dengan layout
            fotoImageView = itemView.findViewById(R.id.fotoImageView); // Menghubungkan fotoImageView dengan layout
        }
    }
}
