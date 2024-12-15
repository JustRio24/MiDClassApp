package com.app.midclassapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.ProgressBar; // Import ProgressBar

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HistoriAbsensiActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HistoriAbsensiAdapter adapter;
    private List<Absensi> historiAbsensiList;
    private FirebaseFirestore db;
    private ProgressBar progressBar; // Deklarasikan ProgressBar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histori_absensi);

        // Inisialisasi Firestore
        db = FirebaseFirestore.getInstance();

        // Inisialisasi RecyclerView dan Adapter
        recyclerView = findViewById(R.id.recyclerViewHistori);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        historiAbsensiList = new ArrayList<>();
        adapter = new HistoriAbsensiAdapter(historiAbsensiList);
        recyclerView.setAdapter(adapter);

        // Inisialisasi ProgressBar
        progressBar = findViewById(R.id.progressBar);

        // Ambil data histori absensi dari Firestore
        loadHistoriAbsensi();
    }


    private void loadHistoriAbsensi() {
        String nimOrNip = getSharedPreferences("MiDClassPrefs", MODE_PRIVATE)
                .getString("nimOrNip", null);

        if (nimOrNip == null) {
            Toast.makeText(this, "NIM/NIP tidak ditemukan", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        db.collection("presensi")
                .whereEqualTo("nimOrNip", nimOrNip)
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful() && task.getResult() != null) {
                        QuerySnapshot querySnapshot = task.getResult();

                        if (querySnapshot.isEmpty()) {
                            Toast.makeText(this, "Tidak ada data absensi", Toast.LENGTH_SHORT).show();
                        } else {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                String matkul = document.getString("matkul");
                                Timestamp timestamp = document.getTimestamp("timestamp"); // Ambil timestamp
                                String keterangan = document.getString("keterangan");
                                String photoBase64 = document.getString("photo");

                                if (matkul != null && timestamp != null && keterangan != null) {
                                    historiAbsensiList.add(new Absensi(matkul, timestamp, keterangan, photoBase64));
                                }
                            }

                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "Gagal memuat data", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Terjadi kesalahan: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


}
