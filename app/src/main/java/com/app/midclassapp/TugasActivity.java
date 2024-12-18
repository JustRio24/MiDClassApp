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

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class TugasActivity extends AppCompatActivity {

    private Spinner spinnerMataKuliah;
    private RecyclerView recyclerViewMateriTugas;

    private FirebaseFirestore db;
    private ArrayList<TugasMateri> listMateriTugas;
    private TugasAdapter adapter;

    private String nimOrNip; // ID mahasiswa untuk memfilter data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tugas);

        // Inisialisasi Firestore
        db = FirebaseFirestore.getInstance();

        // Ambil NIM mahasiswa dari SharedPreferences
        nimOrNip = getSharedPreferences("MiDClassPrefs", MODE_PRIVATE).getString("nimOrNip", null);
        if (nimOrNip == null) {
            Toast.makeText(this, "Data mahasiswa tidak ditemukan!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Inisialisasi UI
        spinnerMataKuliah = findViewById(R.id.spinnerMataKuliah);
        recyclerViewMateriTugas = findViewById(R.id.recyclerViewMateriTugas);

        // Setup RecyclerView
        listMateriTugas = new ArrayList<>();
        adapter = new TugasAdapter(this, listMateriTugas);
        recyclerViewMateriTugas.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMateriTugas.setAdapter(adapter);

        // Ambil mata kuliah dan atur spinner
        setupSpinner();

        // Listener untuk spinner
        spinnerMataKuliah.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedMatkul = parent.getItemAtPosition(position).toString();
                if (!selectedMatkul.equals("Pilih Mata Kuliah")) {
                    fetchMateriTugas(selectedMatkul);
                } else {
                    listMateriTugas.clear();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Tidak ada tindakan yang dilakukan
            }
        });
    }

    private void setupSpinner() {
        // Data mata kuliah untuk spinner, langsung menggunakan matkul_array
        ArrayList<String> matkulList = new ArrayList<>();
        matkulList.add("Pilih Mata Kuliah"); // Pilihan default

        // Menggunakan matkul_array yang sudah didefinisikan
        String[] matkulArray = getResources().getStringArray(R.array.matkul_array);

        // Menambahkan mata kuliah ke dalam matkulList
        for (String m : matkulArray) {
            matkulList.add(m.trim());
        }

        // Atur adapter untuk spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                matkulList
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMataKuliah.setAdapter(spinnerAdapter);
    }

    private void fetchMateriTugas(String matkul) {
        // Ambil data materi/tugas dari Firestore berdasarkan mata kuliah
        db.collection("tugas_materi")
                .whereEqualTo("matkul", matkul)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listMateriTugas.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        TugasMateri tugasMateri = new TugasMateri(
                                document.getString("title"),
                                document.getString("description"),
                                document.getString("deadline"),
                                document.getString("matkul")
                        );
                        listMateriTugas.add(tugasMateri);
                    }
                    adapter.notifyDataSetChanged();

                    if (listMateriTugas.isEmpty()) {
                        Toast.makeText(this, "Tidak ada materi/tugas untuk mata kuliah ini.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Gagal memuat data materi/tugas!", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
    }
}
