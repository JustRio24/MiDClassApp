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

    private RecyclerView recyclerView; // RecyclerView untuk menampilkan daftar histori absensi
    private HistoriAbsensiAdapter adapter; // Adapter untuk RecyclerView
    private List<Absensi> historiAbsensiList; // List untuk menyimpan data absensi
    private FirebaseFirestore db; // Firestore instance
    private ProgressBar progressBar; // Deklarasikan ProgressBar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histori_absensi);

        // Inisialisasi Firestore
        db = FirebaseFirestore.getInstance();

        // Inisialisasi RecyclerView dan Adapter
        recyclerView = findViewById(R.id.recyclerViewHistori); // Menghubungkan RecyclerView dengan layout
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // Set layout manager untuk RecyclerView
        historiAbsensiList = new ArrayList<>(); // Inisialisasi list histori absensi
        adapter = new HistoriAbsensiAdapter(historiAbsensiList); // Inisialisasi adapter untuk RecyclerView
        recyclerView.setAdapter(adapter); // Menetapkan adapter ke RecyclerView

        // Inisialisasi ProgressBar
        progressBar = findViewById(R.id.progressBar); // Menghubungkan ProgressBar dengan layout

        // Ambil data histori absensi dari Firestore
        loadHistoriAbsensi(); // Memanggil metode untuk memuat data
    }

    // Metode untuk memuat histori absensi dari Firestore
    private void loadHistoriAbsensi() {
        // Mengambil nimOrNip dari SharedPreferences
        String nimOrNip = getSharedPreferences("MiDClassPrefs", MODE_PRIVATE)
                .getString("nimOrNip", null);

        // Menampilkan pesan jika nimOrNip tidak ditemukan
        if (nimOrNip == null) {
            Toast.makeText(this, "NIM/NIP tidak ditemukan", Toast.LENGTH_SHORT).show();
            return; // Menghentikan eksekusi jika nimOrNip tidak ada
        }

        // Menampilkan ProgressBar saat memuat data
        progressBar.setVisibility(View.VISIBLE);

        // Query untuk mengambil data absensi berdasarkan nimOrNip
        db.collection("presensi")
                .whereEqualTo("nimOrNip", nimOrNip) // Filter berdasarkan nimOrNip
                .get() // Menjalankan query
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE); // Menyembunyikan ProgressBar setelah query selesai

                    if (task.isSuccessful() && task.getResult() != null) {
                        // Mengecek jika query berhasil dan memiliki hasil
                        QuerySnapshot querySnapshot = task.getResult();

                        if (querySnapshot.isEmpty()) {
                            // Menampilkan pesan jika tidak ada data
                            Toast.makeText(this, "Tidak ada data absensi", Toast.LENGTH_SHORT).show();
                        } else {
                            // Menambahkan data absensi ke historiAbsensiList
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                String matkul = document.getString("matkul");
                                Timestamp timestamp = document.getTimestamp("timestamp"); // Ambil timestamp
                                String keterangan = document.getString("keterangan");
                                String photoBase64 = document.getString("photo");

                                // Mengecek jika data lengkap sebelum menambahkannya ke list
                                if (matkul != null && timestamp != null && keterangan != null) {
                                    historiAbsensiList.add(new Absensi(matkul, timestamp, keterangan, photoBase64));
                                }
                            }

                            // Memberitahukan adapter untuk memperbarui tampilan
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        progressBar.setVisibility(View.GONE); // Menyembunyikan ProgressBar jika gagal
                        Toast.makeText(this, "Gagal memuat data", Toast.LENGTH_SHORT).show(); // Menampilkan pesan gagal
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE); // Menyembunyikan ProgressBar jika terjadi kesalahan
                    Toast.makeText(this, "Terjadi kesalahan: " + e.getMessage(), Toast.LENGTH_SHORT).show(); // Menampilkan pesan error
                });
    }
}
