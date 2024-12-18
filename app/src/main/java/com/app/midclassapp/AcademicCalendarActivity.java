package com.app.midclassapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AcademicCalendarActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AcademicEventAdapter adapter;
    private List<AcademicEvent> events;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private TextView noDataTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_academic_calendar);

        // Inisialisasi Firestore dan komponen tampilan
        db = FirebaseFirestore.getInstance(); // Menginisialisasi Firestore
        recyclerView = findViewById(R.id.recyclerView); // Menghubungkan RecyclerView
        progressBar = findViewById(R.id.progressBar); // Menghubungkan ProgressBar
        noDataTextView = findViewById(R.id.noDataTextView); // Menghubungkan TextView untuk menampilkan pesan tidak ada data

        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // Menetapkan layout manager untuk RecyclerView
        events = new ArrayList<>(); // Membuat list untuk menyimpan data acara
        adapter = new AcademicEventAdapter(events); // Menetapkan adapter untuk RecyclerView
        recyclerView.setAdapter(adapter); // Menghubungkan adapter dengan RecyclerView

        // Memuat data dari Firestore
        fetchAcademicCalendar(); // Memanggil metode untuk mengambil data kalender akademik
    }

    // Memuat kalender akademik dari Firestore
    private void fetchAcademicCalendar() {
        progressBar.setVisibility(View.VISIBLE); // Menampilkan ProgressBar saat memuat data
        noDataTextView.setVisibility(View.GONE); // Menyembunyikan pesan jika data sedang dimuat

        db.collection("academic_calendar") // Mengambil data dari koleksi "academic_calendar"
                .get() // Mengambil semua dokumen dari koleksi
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE); // Menyembunyikan ProgressBar setelah data dimuat
                    if (task.isSuccessful()) { // Mengecek apakah task berhasil
                        QuerySnapshot querySnapshot = task.getResult(); // Mengambil hasil query
                        if (querySnapshot != null && !querySnapshot.isEmpty()) { // Mengecek apakah ada data yang ditemukan
                            events.clear(); // Menghapus data lama
                            for (QueryDocumentSnapshot document : querySnapshot) { // Iterasi untuk setiap dokumen
                                String no = document.getString("no"); // Mengambil nomor acara
                                String eventName = document.getString("event_name"); // Mengambil nama acara
                                String dateRange = document.getString("date_range"); // Mengambil rentang tanggal acara

                                events.add(new AcademicEvent(no, eventName, dateRange)); // Menambahkan acara ke list
                            }
                            adapter.notifyDataSetChanged(); // Memberitahu adapter untuk memperbarui tampilan
                        } else {
                            noDataTextView.setVisibility(View.VISIBLE); // Menampilkan pesan jika tidak ada data
                        }
                    } else {
                        noDataTextView.setVisibility(View.VISIBLE); // Menampilkan pesan jika gagal memuat data
                    }
                });
    }
}
