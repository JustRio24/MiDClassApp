package com.app.midclassapp;

import static com.app.midclassapp.AdminRiwayatPresensiActivity.decode64;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RiwayatPresensiAdapter extends RecyclerView.Adapter<RiwayatPresensiAdapter.ViewHolder> {

    private Context context;
    private List<Absensi> absensiList;

    public RiwayatPresensiAdapter(Context context, List<Absensi> absensiList) {
        this.context = context;
        this.absensiList = absensiList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate layout item_riwayat_presensi.xml untuk tiap item
        View view = LayoutInflater.from(context).inflate(R.layout.item_riwayat_presensi, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Absensi absensi = absensiList.get(position);

        // Jika fotoBase64 tidak null atau kosong, kita decode dan tampilkan
        if (absensi.getFotoBase64() != null && !absensi.getFotoBase64().isEmpty()) {
            Bitmap bitmap = decode64(absensi.getFotoBase64());
            if (bitmap != null) {
                holder.fotoImageView.setImageBitmap(bitmap);  // Menampilkan gambar ke ImageView
            } else {
                holder.fotoImageView.setImageResource(R.drawable.ic_placeholder);  // Gambar default jika decode gagal
            }
        } else {
            holder.fotoImageView.setImageResource(R.drawable.ic_placeholder);  // Gambar default jika tidak ada gambar
        }

        // Bind data dari Absensi ke tampilan
        holder.nimOrNipTextView.setText(absensi.getNimOrNip());
        holder.matkulTextView.setText(absensi.getMatkul());
        holder.tanggalTextView.setText(absensi.getTanggal());
        holder.keteranganTextView.setText(absensi.getKeterangan());
    }

    @Override
    public int getItemCount() {
        return absensiList.size();  // Mengembalikan jumlah item dalam list absensi
    }

    // ViewHolder untuk item presensi
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nimOrNipTextView, matkulTextView, tanggalTextView, keteranganTextView;
        ImageView fotoImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            nimOrNipTextView = itemView.findViewById(R.id.nimOrNipTextView);
            matkulTextView = itemView.findViewById(R.id.matkulTextView);
            tanggalTextView = itemView.findViewById(R.id.tanggalTextView);
            keteranganTextView = itemView.findViewById(R.id.keteranganTextView);
            fotoImageView = itemView.findViewById(R.id.fotoPresensiImageView);
        }
    }
}
