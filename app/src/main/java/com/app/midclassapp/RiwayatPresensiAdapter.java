package com.app.midclassapp;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RiwayatPresensiAdapter extends RecyclerView.Adapter<RiwayatPresensiAdapter.ViewHolder> {

    private List<Absensi> absensiList;

    public RiwayatPresensiAdapter(List<Absensi> absensiList) {
        this.absensiList = absensiList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_riwayat_presensi, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Absensi absensi = absensiList.get(position);

        holder.nimOrNipTextView.setText(absensi.getNimOrNip());
        holder.matkulTextView.setText(absensi.getMatkul());
        holder.tanggalTextView.setText(absensi.getTanggal());
        holder.keteranganTextView.setText(absensi.getKeterangan());

        // Ambil string Base64 dari Absensi
        String encodedPhoto = absensi.getPhoto();

        // Jika string Base64 ada, decode gambar
        if (encodedPhoto != null && !encodedPhoto.isEmpty()) {
            try {
                // Menggunakan helper method untuk decode Base64 menjadi Bitmap
                Bitmap decodedByte = decodeBase64(encodedPhoto);
                holder.fotoPresensiImageView.setImageBitmap(decodedByte);
            } catch (Exception e) {
                // Menampilkan placeholder jika terjadi kesalahan saat decoding
                holder.fotoPresensiImageView.setImageResource(R.drawable.ic_placeholder);
            }
        } else {
            // Menampilkan placeholder jika foto tidak ada
            holder.fotoPresensiImageView.setImageResource(R.drawable.ic_placeholder);
        }
    }

    @Override
    public int getItemCount() {
        return absensiList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView fotoPresensiImageView;
        TextView nimOrNipTextView, matkulTextView, tanggalTextView, keteranganTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fotoPresensiImageView = itemView.findViewById(R.id.fotoPresensiImageView);
            nimOrNipTextView = itemView.findViewById(R.id.nimOrNipTextView);
            matkulTextView = itemView.findViewById(R.id.matkulTextView);
            tanggalTextView = itemView.findViewById(R.id.tanggalTextView);
            keteranganTextView = itemView.findViewById(R.id.keteranganTextView);
        }
    }

    // Helper method untuk mendekode gambar Base64
    public static Bitmap decodeBase64(String encodedImage) {
        byte[] decodedBytes = android.util.Base64.decode(encodedImage, android.util.Base64.DEFAULT);
        return android.graphics.BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}
