package com.app.midclassapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HistoriAbsensiActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HistoriAbsensiAdapter adapter;
    private List<Absensi> historiAbsensiList;
    private List<Absensi> allAbsensiList; // List untuk menyimpan semua data absensi dari Firestore
    private FirebaseFirestore db;
    private ProgressBar progressBar;

    private Spinner spinnerCourse;
    private TextView tvSelectedDate;
    private Button buttonFilter;
    private String selectedDate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histori_absensi);

        db = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.recyclerViewHistori);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        historiAbsensiList = new ArrayList<>();
        allAbsensiList = new ArrayList<>(); // Inisialisasi allAbsensiList
        adapter = new HistoriAbsensiAdapter(historiAbsensiList);
        recyclerView.setAdapter(adapter);

        progressBar = findViewById(R.id.progressBar);
        spinnerCourse = findViewById(R.id.spinner_course);
        tvSelectedDate = findViewById(R.id.tv_selected_date);
        buttonFilter = findViewById(R.id.button_filter);

        tvSelectedDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                Calendar selectedCalendar = Calendar.getInstance();
                selectedCalendar.set(year, month, dayOfMonth);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                selectedDate = dateFormat.format(selectedCalendar.getTime());
                tvSelectedDate.setText(selectedDate);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        buttonFilter.setOnClickListener(v -> {
            String selectedCourse = spinnerCourse.getSelectedItem().toString();
            filterHistoriAbsensi(selectedCourse, selectedDate);
        });

        // Load data pertama kali
        loadHistoriAbsensi("Semua Mata Kuliah", null);
    }

    private void loadHistoriAbsensi(String matkul, String tanggal) {
        String nimOrNip = getSharedPreferences("MiDClassPrefs", MODE_PRIVATE)
                .getString("nimOrNip", null);

        if (nimOrNip == null) {
            Toast.makeText(this, "NIM/NIP tidak ditemukan", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE); // Menyembunyikan RecyclerView selama proses pemuatan data

        db.collection("presensi")
                .whereEqualTo("nimOrNip", nimOrNip) // Mengambil data sesuai dengan nimOrNip
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful() && task.getResult() != null) {
                        allAbsensiList.clear(); // Membersihkan data lama
                        historiAbsensiList.clear(); // Membersihkan data lama yang akan ditampilkan

                        // Memasukkan data baru ke allAbsensiList
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String matkulDb = document.getString("matkul");
                            String keterangan = document.getString("keterangan");
                            String photoBase64 = document.getString("photo");
                            String tanggalDb = document.getString("tanggal");
                            Timestamp timestamp = document.getTimestamp("timestamp");

                            if (matkulDb != null && tanggalDb != null && keterangan != null) {
                                Absensi absensi = new Absensi(matkulDb, timestamp, keterangan, photoBase64, tanggalDb);
                                allAbsensiList.add(absensi);
                            }
                        }

                        // Memfilter histori absensi pertama kali
                        filterHistoriAbsensi(matkul, tanggal);

                        recyclerView.setVisibility(View.VISIBLE); // Menampilkan RecyclerView setelah data dimuat
                    } else {
                        Toast.makeText(this, "Gagal memuat data", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Terjadi kesalahan: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void filterHistoriAbsensi(String matkul, String tanggal) {
        historiAbsensiList.clear(); // Bersihkan data historiAbsensiList

        for (Absensi absensi : allAbsensiList) {
            boolean matchMatkul = matkul.equals("Semua Mata Kuliah") || matkul.equals(absensi.getMatkul());
            boolean matchTanggal = tanggal == null || tanggal.equals(absensi.getTanggal());

            if (matchMatkul && matchTanggal) {
                historiAbsensiList.add(absensi);
            }
        }

        adapter.notifyDataSetChanged(); // Memberi tahu adapter bahwa data sudah berubah

        if (historiAbsensiList.isEmpty()) {
            Toast.makeText(this, "Data tidak ditemukan untuk filter ini", Toast.LENGTH_SHORT).show();
        } else {
            Log.d("FILTER", "Data hasil filter: " + historiAbsensiList.size());
        }
    }
}
