package com.app.midclassapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class DashboardDosenActivity extends AppCompatActivity {

    private TextView dashboardUserName, dashboardJurusan, dashboardKampus, nextScheduleTitle, nextScheduleValue;
    private ImageView logoImageView, iconProfile;
    private FirebaseFirestore db;
    private String dosenId, dosenNama, dosenMatkul;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_dosen);

        // Inisialisasi Firebase
        db = FirebaseFirestore.getInstance();

        // Inisialisasi tampilan (views)
        dashboardUserName = findViewById(R.id.dashboardUserName);
        dashboardJurusan = findViewById(R.id.dashboardJurusan);
        dashboardKampus = findViewById(R.id.dashboardKampus);
        logoImageView = findViewById(R.id.logoImageView);
        iconProfile = findViewById(R.id.iconProfile);
        nextScheduleTitle = findViewById(R.id.nextScheduleTitle);
        nextScheduleValue = findViewById(R.id.nextScheduleValue);

        // Ambil nimOrNip dari SharedPreferences (asumsi sudah ada)
        SharedPreferences sharedPreferences = getSharedPreferences("MiDClassPrefs", MODE_PRIVATE);
        String nimOrNip = sharedPreferences.getString("nimOrNip", "");

        // Ambil data dosen berdasarkan nimOrNip
        ambilDataDosen(nimOrNip);

        setupMenuActions();
        setupBottomNavigation();
    }


    private void setupMenuActions() {
        // Menu Presensi
        LinearLayout menuPresensi = findViewById(R.id.menuValidasiPresensi);
        menuPresensi.setOnClickListener(view -> {
            Intent intent = new Intent(this, ValidasiPresensiActivity.class);
            startActivity(intent);
        });

        // Menu Tugas
        LinearLayout menuNilai = findViewById(R.id.menuUploadTugas);
        menuNilai.setOnClickListener(view -> {
            Intent intent = new Intent(this, UploadTugasActivity.class);
            startActivity(intent);
        });

        LinearLayout menuSoon = findViewById(R.id.menuSoon);
        menuSoon.setOnClickListener(view -> {
            Toast.makeText(this, "Fitur Baru akan Segera hadir !!!", Toast.LENGTH_SHORT).show();
        });

        ImageView iconProfile = findViewById(R.id.iconProfile);
        iconProfile.setOnClickListener(view -> {
            Intent intent = new Intent(this, ProfileDosenActivity.class);
            startActivity(intent);
        });
    }

    // Ambil data dosen dari Firestore berdasarkan nimOrNip
    private void ambilDataDosen(String nimOrNip) {
        db.collection("users").whereEqualTo("nimOrNip", nimOrNip).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        dosenId = documentSnapshot.getId();  // Ambil ID dokumen dosen
                        dosenNama = documentSnapshot.getString("nama");  // Ambil nama dosen
                        dosenMatkul = documentSnapshot.getString("matkul");  // Ambil mata kuliah dosen

                        // Setel data ke TextView
                        dashboardUserName.setText(dosenNama);
                        dashboardJurusan.setText(dosenMatkul);  // Mata kuliah dosen
                        dashboardKampus.setText("Politeknik Negeri Sriwijaya"); // Nama kampus

                        // Setel gambar profil
                        iconProfile.setImageResource(R.drawable.ic_profile_pink);

                        // Setelah mengambil data dosen, ambil data jadwal
                        ambilDataJadwal(dosenMatkul);  // Mengambil jadwal berdasarkan mata kuliah
                    } else {
                        Toast.makeText(DashboardDosenActivity.this, "Data dosen tidak ditemukan.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // Menangani error saat pengambilan data dosen
                    Toast.makeText(DashboardDosenActivity.this, "Gagal memuat data dosen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Ambil data jadwal mengajar dosen berdasarkan mata kuliah
    private void ambilDataJadwal(String matkul) {
        db.collection("jadwal")
                .whereEqualTo("matkul", matkul)  // Filter berdasarkan mata kuliah dosen
                .orderBy("waktuMulai")  // Urutkan berdasarkan waktu mulai
                .limit(1) // Ambil hanya jadwal yang pertama (jadwal berikutnya)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Menampilkan jadwal
                        StringBuilder jadwalDetails = new StringBuilder();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String hari = documentSnapshot.getString("hari");
                            String waktuMulai = documentSnapshot.getString("waktuMulai");
                            String waktuSelesai = documentSnapshot.getString("waktuSelesai");
                            String ruang = documentSnapshot.getString("ruang");

                            // Menambahkan detail jadwal ke jadwalDetails
                            jadwalDetails.append(hari).append(", ").append(waktuMulai).append(" - ").append(waktuSelesai)
                                    .append(" (").append(ruang).append(")\n");
                        }

                        // Menyusun tampilan "Pengingat Jadwal Mengajar Berikutnya"
                        nextScheduleTitle.setText("Jadwal Mengajar " + dosenMatkul);
                        nextScheduleValue.setText(jadwalDetails.toString());
                    } else {
                        nextScheduleTitle.setText("Jadwal Mengajar " + dosenMatkul);
                        nextScheduleValue.setText("Tidak ada jadwal yang ditemukan.");
                    }
                })
                .addOnFailureListener(e -> {
                    // Menangani error saat pengambilan jadwal
                    nextScheduleTitle.setText("Jadwal Mengajar " + dosenMatkul);
                    nextScheduleValue.setText("Gagal memuat jadwal: " + e.getMessage());
                    Toast.makeText(DashboardDosenActivity.this, "Gagal memuat jadwal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.menu_home) {
                Toast.makeText(this, "Home dipilih", Toast.LENGTH_SHORT).show();
            } else if (itemId == R.id.menu_calender) {
                Intent intent = new Intent(this, AcademicCalendarActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.menu_profile) {
                Intent intent = new Intent(this, ProfileDosenActivity.class);
                startActivity(intent);
            } else {
                Log.e("DashboardDosenActivity", "Menu tidak dikenal");
            }

            return true;
        });
    }

}
