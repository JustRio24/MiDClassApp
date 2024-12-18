package com.app.midclassapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

// Adapter untuk menampilkan daftar tugas di layar
public class TugasAdapter extends RecyclerView.Adapter<TugasAdapter.ViewHolder> {

    private Context context; // Digunakan untuk mengakses aplikasi dan layout
    private ArrayList<TugasMateri> listMateriTugas; // Daftar tugas yang akan ditampilkan

    // Konstruktor untuk memasukkan data tugas dan context
    public TugasAdapter(Context context, ArrayList<TugasMateri> listMateriTugas) {
        this.context = context;
        this.listMateriTugas = listMateriTugas;
    }

    // Fungsi untuk membuat tampilan baru setiap kali ada item
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Membuat tampilan dari layout item_tugas
        View view = LayoutInflater.from(context).inflate(R.layout.item_tugas, parent, false);
        return new ViewHolder(view); // Mengembalikan tampilan yang telah dibuat
    }

    // Fungsi untuk mengisi tampilan dengan data tugas
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Mengambil data tugas berdasarkan posisi
        TugasMateri tugasMateri = listMateriTugas.get(position);

        // Menampilkan data tugas ke tampilan
        holder.tvTitle.setText(tugasMateri.getTitle()); // Menampilkan judul tugas
        holder.tvDescription.setText(tugasMateri.getDescription()); // Menampilkan deskripsi tugas
        holder.tvDeadline.setText("Deadline: " + tugasMateri.getDeadline()); // Menampilkan deadline tugas
        holder.tvMatkul.setText("Mata Kuliah: " + tugasMateri.getMatkul()); // Menampilkan mata kuliah
    }

    // Fungsi untuk mengembalikan jumlah tugas yang ada
    @Override
    public int getItemCount() {
        return listMateriTugas.size(); // Menghitung jumlah tugas
    }

    // Kelas ViewHolder untuk menyimpan tampilan setiap tugas
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Menyimpan referensi ke tampilan tugas
        TextView tvTitle, tvDescription, tvDeadline, tvMatkul;

        // Konstruktor untuk menghubungkan tampilan dengan elemen yang ada di layout
        public ViewHolder(View itemView) {
            super(itemView);
            // Menghubungkan setiap elemen tampilan dengan ID-nya
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvDeadline = itemView.findViewById(R.id.tvDeadline);
            tvMatkul = itemView.findViewById(R.id.tvMatkul);
        }
    }
}
