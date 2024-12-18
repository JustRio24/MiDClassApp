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

    private Spinner spinnerMataKuliah; // Spinner untuk memilih mata kuliah
    private RecyclerView recyclerViewMateriTugas; // RecyclerView untuk menampilkan daftar materi tugas

    private FirebaseFirestore db; // Referensi ke Firestore untuk mengambil data
    private ArrayList<TugasMateri> listMateriTugas; // Daftar tugas yang akan ditampilkan
    private TugasAdapter adapter; // Adapter untuk menghubungkan data tugas dengan RecyclerView

    private String nimOrNip; // ID mahasiswa untuk memfilter data tugas

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tugas);

        // Inisialisasi Firestore
        db = FirebaseFirestore.getInstance();

        // Ambil NIM mahasiswa dari SharedPreferences
        nimOrNip = getSharedPreferences("MiDClassPrefs", MODE_PRIVATE).getString("nimOrNip", null);

        // Jika NIM tidak ditemukan, tampilkan pesan kesalahan dan keluar dari aktivitas
        if (nimOrNip == null) {
            Toast.makeText(this, "Data mahasiswa tidak ditemukan!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Inisialisasi elemen UI
        spinnerMataKuliah = findViewById(R.id.spinnerMataKuliah);
        recyclerViewMateriTugas = findViewById(R.id.recyclerViewMateriTugas);

        // Setup RecyclerView untuk menampilkan daftar tugas
        listMateriTugas = new ArrayList<>();
        adapter = new TugasAdapter(this, listMateriTugas);
        recyclerViewMateriTugas.setLayoutManager(new LinearLayoutManager(this)); // Mengatur layout vertikal
        recyclerViewMateriTugas.setAdapter(adapter);

        // Setup Spinner untuk memilih mata kuliah
        setupSpinner();

        // Listener untuk spinner saat mata kuliah dipilih
        spinnerMataKuliah.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedMatkul = parent.getItemAtPosition(position).toString();

                // Jika mata kuliah dipilih, ambil data tugas berdasarkan mata kuliah tersebut
                if (!selectedMatkul.equals("Pilih Mata Kuliah")) {
                    fetchMateriTugas(selectedMatkul);
                } else {
                    // Jika "Pilih Mata Kuliah" dipilih, kosongkan daftar tugas
                    listMateriTugas.clear();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Tidak ada tindakan jika tidak ada mata kuliah yang dipilih
            }
        });
    }

    // Fungsi untuk setup spinner dengan daftar mata kuliah
    private void setupSpinner() {
        // Daftar mata kuliah yang akan ditampilkan di spinner
        ArrayList<String> matkulList = new ArrayList<>();
        matkulList.add("Pilih Mata Kuliah"); // Pilihan default

        // Mengambil daftar mata kuliah dari resources
        String[] matkulArray = getResources().getStringArray(R.array.matkul_array);

        // Menambahkan mata kuliah ke dalam list
        for (String m : matkulArray) {
            matkulList.add(m.trim());
        }

        // Menyusun adapter untuk spinner dengan mata kuliah
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, matkulList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMataKuliah.setAdapter(spinnerAdapter);
    }

    // Fungsi untuk mengambil materi tugas dari Firestore berdasarkan mata kuliah
    private void fetchMateriTugas(String matkul) {
        // Mengambil data tugas dari koleksi "tugas_materi" berdasarkan mata kuliah
        db.collection("tugas_materi")
                .whereEqualTo("matkul", matkul) // Memfilter berdasarkan mata kuliah yang dipilih
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listMateriTugas.clear(); // Kosongkan daftar tugas sebelumnya

                    // Memasukkan data tugas yang diambil dari Firestore ke dalam list
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        TugasMateri tugasMateri = new TugasMateri(
                                document.getString("title"), // Judul tugas
                                document.getString("description"), // Deskripsi tugas
                                document.getString("deadline"), // Deadline tugas
                                document.getString("matkul") // Mata kuliah tugas
                        );
                        listMateriTugas.add(tugasMateri); // Menambahkan tugas ke dalam daftar
                    }

                    // Mengupdate tampilan RecyclerView
                    adapter.notifyDataSetChanged();

                    // Jika tidak ada tugas untuk mata kuliah yang dipilih, tampilkan pesan
                    if (listMateriTugas.isEmpty()) {
                        Toast.makeText(this, "Tidak ada materi/tugas untuk mata kuliah ini.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // Jika gagal mengambil data, tampilkan pesan kesalahan
                    Toast.makeText(this, "Gagal memuat data materi/tugas!", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
    }
}
