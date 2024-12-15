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

    private List<Absensi> historiAbsensi;

    public HistoriAbsensiAdapter(List<Absensi> historiAbsensi) {
        this.historiAbsensi = historiAbsensi;
    }

    @NonNull
    @Override
    public AbsensiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout untuk setiap item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_absensi, parent, false);
        return new AbsensiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AbsensiViewHolder holder, int position) {
        Absensi absensi = historiAbsensi.get(position);

        // Set data ke komponen UI
        holder.matkulTextView.setText(absensi.getMatkul());
        holder.tanggalTextView.setText(formatTimestamp(absensi.getTimestamp())); // Format timestamp
        holder.keteranganTextView.setText(absensi.getKeterangan());

        // Jika ada foto, tampilkan gambar
        if (absensi.getFotoBase64() != null && !absensi.getFotoBase64().isEmpty()) {
            byte[] decodedString = Base64.decode(absensi.getFotoBase64(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.fotoImageView.setImageBitmap(decodedByte);
        } else {
            holder.fotoImageView.setImageResource(R.drawable.ic_placeholder); // Gambar placeholder jika tidak ada foto
        }
    }

    @Override
    public int getItemCount() {
        return historiAbsensi.size();
    }

    // Helper method untuk memformat timestamp
    private String formatTimestamp(Timestamp timestamp) {
        if (timestamp != null) {
            Date date = timestamp.toDate(); // Konversi Timestamp menjadi Date
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
            return formatter.format(date);
        } else {
            return "Tanggal tidak tersedia"; // Jika timestamp null
        }
    }

    // ViewHolder untuk setiap item
    public static class AbsensiViewHolder extends RecyclerView.ViewHolder {

        TextView matkulTextView, tanggalTextView, keteranganTextView;
        ImageView fotoImageView;

        public AbsensiViewHolder(View itemView) {
            super(itemView);
            matkulTextView = itemView.findViewById(R.id.matkulTextView);
            tanggalTextView = itemView.findViewById(R.id.tanggalTextView);
            keteranganTextView = itemView.findViewById(R.id.keteranganTextView);
            fotoImageView = itemView.findViewById(R.id.fotoImageView);
        }
    }
}
