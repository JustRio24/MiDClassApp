package com.app.midclassapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
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

public class HistoryPresensiAdapter extends RecyclerView.Adapter<HistoryPresensiAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Absensi> historyAbsensi;
    private Map<String, Absensi> selectedAbsensiMap;

    public HistoryPresensiAdapter(Context context, ArrayList<Absensi> historyAbsensi, Map<String, Absensi> selectedAbsensiMap) {
        this.context = context;
        this.historyAbsensi = historyAbsensi;
        this.selectedAbsensiMap = selectedAbsensiMap;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_history_presensi, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Absensi absensi = historyAbsensi.get(position);

        // Set data untuk tampilan
        holder.nimTextView.setText(absensi.getNimOrNip()); // Menampilkan NIM atau NIP mahasiswa
        holder.keteranganTextView.setText(absensi.getKeterangan());

        // Format timestamp jika ada
        if (absensi.getTimestamp() != null) {
            holder.timestampTextView.setText(ValidasiPresensiActivity.formatTimestamp(absensi.getTimestamp()));
        }

        // Decode foto
        if (absensi.getFotoBase64() != null) {
            Bitmap bitmap = ValidasiPresensiActivity.decodeBase64(absensi.getFotoBase64());
            holder.fotoImageView.setImageBitmap(bitmap);
        }

        // Checkbox listener
        holder.checkBox.setChecked(selectedAbsensiMap.containsKey(absensi.getDocumentId()));
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedAbsensiMap.put(absensi.getDocumentId(), absensi);
            } else {
                selectedAbsensiMap.remove(absensi.getDocumentId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return historyAbsensi.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nimTextView, keteranganTextView, timestampTextView;
        ImageView fotoImageView;
        CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            nimTextView = itemView.findViewById(R.id.nimTextView);
            keteranganTextView = itemView.findViewById(R.id.keteranganTextView);
            timestampTextView = itemView.findViewById(R.id.timestampTextView);
            fotoImageView = itemView.findViewById(R.id.fotoImageView);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }
}
