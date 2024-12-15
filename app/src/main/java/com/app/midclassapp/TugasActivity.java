package com.app.midclassapp;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

public class TugasActivity extends AppCompatActivity {

    private Spinner spinnerMataKuliah;
    private RecyclerView recyclerViewMateriTugas;
    private TugasAdapter tugasAdapter;
    private HashMap<String, ArrayList<String>> tugasData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tugas);

        // Inisialisasi views
        spinnerMataKuliah = findViewById(R.id.spinnerMataKuliah);
        recyclerViewMateriTugas = findViewById(R.id.recyclerViewMateriTugas);

        // Load data tugas
        loadTugasData();

        // Setup Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.matkul_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMataKuliah.setAdapter(adapter);

        // Setup RecyclerView
        recyclerViewMateriTugas.setLayoutManager(new LinearLayoutManager(this));
        tugasAdapter = new TugasAdapter(new ArrayList<>());
        recyclerViewMateriTugas.setAdapter(tugasAdapter);

        // Spinner selection listener
        spinnerMataKuliah.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedMatkul = parent.getItemAtPosition(position).toString();
                updateTugasList(selectedMatkul);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Tidak ada item yang dipilih
            }
        });
    }

    // Method untuk memuat data tugas
    private void loadTugasData() {
        tugasData = new HashMap<>();

        // Contoh data tugas untuk setiap mata kuliah
        tugasData.put("Praktikum Pemrograman Mobile", new ArrayList<String>() {{
            add("Membuat aplikasi dengan RecyclerView");
            add("Integrasi API menggunakan Retrofit");
        }});

        tugasData.put("Praktikum Basis Data", new ArrayList<String>() {{
            add("Membuat ERD untuk sistem akademik");
            add("Implementasi trigger dan stored procedure");
        }});

        tugasData.put("Praktikum Pemrograman Berorientasi Objek", new ArrayList<String>() {{
            add("Implementasi inheritance dalam Java");
            add("Membuat UML class diagram");
        }});

        // Tambahkan data untuk mata kuliah lainnya sesuai kebutuhan
        tugasData.put("Analisis Perancangan Sistem Informasi", new ArrayList<String>() {{
            add("Analisis kebutuhan pengguna");
            add("Membuat dokumen SRS");
        }});

        tugasData.put("Matematika Diskrit", new ArrayList<String>() {{
            add("Mengerjakan soal logika proposisi");
            add("Mengimplementasikan graf dalam algoritma");
        }});
    }

    // Method untuk memperbarui daftar tugas
    private void updateTugasList(String matkul) {
        ArrayList<String> tugasList = tugasData.get(matkul);

        if (tugasList != null && !tugasList.isEmpty()) {
            tugasAdapter.updateTugasList(tugasList);
        } else {
            tugasAdapter.updateTugasList(new ArrayList<>());
            Toast.makeText(this, "Tidak ada tugas untuk mata kuliah ini", Toast.LENGTH_SHORT).show();
        }
    }
}
