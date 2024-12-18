package com.app.midclassapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AcademicEventAdapter extends RecyclerView.Adapter<AcademicEventAdapter.ViewHolder> {

    private List<AcademicEvent> events; // Daftar acara akademik

    // Konstruktor untuk menginisialisasi daftar acara
    public AcademicEventAdapter(List<AcademicEvent> events) {
        this.events = events; // Menetapkan daftar acara
    }

    // Membuat ViewHolder untuk setiap item dalam RecyclerView
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_academic_event, parent, false); // Mengambil tampilan untuk item
        return new ViewHolder(view); // Mengembalikan ViewHolder
    }

    // Mengikat data acara ke ViewHolder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AcademicEvent event = events.get(position); // Mendapatkan acara pada posisi tertentu
        holder.noTextView.setText(event.getNo()); // Mengatur nomor acara
        holder.eventNameTextView.setText(event.getEventName()); // Mengatur nama acara
        holder.dateRangeTextView.setText(event.getDateRange()); // Mengatur rentang tanggal acara
    }

    // Mengembalikan jumlah total item dalam daftar acara
    @Override
    public int getItemCount() {
        return events.size(); // Mengembalikan jumlah acara
    }

    // ViewHolder untuk item acara akademik
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView noTextView, eventNameTextView, dateRangeTextView; // Variabel untuk tampilan

        // Konstruktor untuk menginisialisasi tampilan
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            noTextView = itemView.findViewById(R.id.noDataTextView); // Menginisialisasi tampilan nomor
            eventNameTextView = itemView.findViewById(R.id.eventNameTextView); // Menginisialisasi tampilan nama acara
            dateRangeTextView = itemView.findViewById(R.id.dateRangeTextView); // Menginisialisasi tampilan rentang tanggal
        }
    }
}
