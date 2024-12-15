package com.app.midclassapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DashboardMahasiswaActivity extends AppCompatActivity {

    private TextView dashboardUserName, dashboardJurusan, dashboardKampus, sksTempuhValue, statusValue;
    private ImageView logoImageView, iconProfile;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_mahasiswa);
        Log.d("DashboardMahasiswaActivity", "Activity started");

        // Inisialisasi Firestore
        db = FirebaseFirestore.getInstance();

        initializeViews();
        loadUserData(); // Mengambil data pengguna dari Firestore

        setupMenuActions();
        setupBottomNavigation();

    }

    @Override
    protected void onStart() {
        super.onStart();

        // Ambil nimOrNip dari SharedPreferences
        String nimOrNip = getSharedPreferences("MiDClassPrefs", MODE_PRIVATE)
                .getString("nimOrNip", null);

        if (nimOrNip == null) {
            // Jika nimOrNip tidak ditemukan, alihkan ke login
            Toast.makeText(this, "Anda belum login", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();  // Pastikan Dashboard tidak lagi ditampilkan
            return;
        }

        // Query Firestore menggunakan nimOrNip yang diambil dari SharedPreferences
        db.collection("users")
                .whereEqualTo("nimOrNip", nimOrNip)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);

                        // Ambil data pengguna dari Firestore
                        String username = document.getString("username");
                        String kelas = document.getString("kelas");
                        String jurusan = document.getString("jurusan");
                        String kampus = document.getString("kampus");
                        String sks = document.getString("sks");
                        String status = document.getString("status");
                        String base64Image = document.getString("profile_picture");

                        if (base64Image != null && !base64Image.isEmpty()) {
                            try {
                                byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inSampleSize = 4;
                                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length, options);
                                iconProfile.setImageBitmap(decodedByte);
                            } catch (Exception e) {
                                Log.e("ProfileActivity", "Error decoding Base64 image", e);
                            }
                        }

                        // Tampilkan data ke UI
                        dashboardUserName.setText(username != null ? username : "Nama tidak ditemukan");
                        dashboardJurusan.setText(jurusan != null ? jurusan : "Jurusan tidak ditemukan");
                        dashboardKampus.setText(kampus != null ? kampus : "Kampus tidak ditemukan");
                        sksTempuhValue.setText(sks != null ? sks : "0");
                        statusValue.setText(status != null ? status : "Tidak diketahui");

                        Log.d("DashboardMahasiswaActivity", "Data berhasil dimuat");
                    } else {
                        Log.e("DashboardMahasiswaActivity", "User not found");
                        Toast.makeText(this, "Data pengguna tidak ditemukan", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("DashboardMahasiswaActivity", "Error: " + e.getMessage());
                    Toast.makeText(this, "Gagal memuat data pengguna", Toast.LENGTH_SHORT).show();
                });
    }


    private void initializeViews() {
        // Header
        dashboardUserName = findViewById(R.id.dashboardUserName);
        dashboardJurusan = findViewById(R.id.dashboardJurusan);
        dashboardKampus = findViewById(R.id.dashboardKampus);
        logoImageView = findViewById(R.id.logoImageView);
        iconProfile = findViewById(R.id.iconProfile);

        // Data tambahan
        sksTempuhValue = findViewById(R.id.sksTempuhValue);
        statusValue = findViewById(R.id.status_Value);
    }

    private void loadUserData() {
        // Simulasikan NIM/NIP pengguna
        String nimOrNip = getSharedPreferences("MiDClassPrefs", MODE_PRIVATE)
                .getString("nimOrNip", null); // shared preferences

        // Query Firestore berdasarkan NIM/NIP
        db.collection("users").whereEqualTo("nimOrNip", nimOrNip)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Mengambil dokumen pertama
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);

                        String username = document.getString("username");
                        String jurusan = document.getString("jurusan");
                        String kampus = document.getString("kampus");
                        String sks = document.getString("sks");
                        String status = document.getString("status");

                        // Menampilkan data ke UI
                        dashboardUserName.setText(username != null ? username : "Nama tidak ditemukan");
                        dashboardJurusan.setText(jurusan != null ? jurusan : "Jurusan tidak ditemukan");
                        dashboardKampus.setText(kampus != null ? kampus : "Kampus tidak ditemukan");
                        sksTempuhValue.setText(sks != null ? sks : "0");
                        statusValue.setText(status != null ? status : "Tidak diketahui");
                    } else {
                        Log.e("DashboardMahasiswaActivity", "User not found");
                        Toast.makeText(this, "Data pengguna tidak ditemukan", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("DashboardMahasiswaActivity", "Error: " + e.getMessage());
                    Toast.makeText(this, "Gagal memuat data pengguna", Toast.LENGTH_SHORT).show();
                });
    }

    private void setupMenuActions() {
        // Menu Presensi
        LinearLayout menuPresensi = findViewById(R.id.menuPresensi);
        menuPresensi.setOnClickListener(view -> {
            Intent intent = new Intent(this, PresensiActivity.class);
            startActivity(intent);
        });

        // Menu Jadwal
        LinearLayout menuJadwal = findViewById(R.id.menuJadwal);
        menuJadwal.setOnClickListener(view -> {
            Intent intent = new Intent(this, JadwalActivity.class);
            startActivity(intent);
        });

        // Menu Tugas
        LinearLayout menuNilai = findViewById(R.id.menuTugas);
        menuNilai.setOnClickListener(view -> {
            Intent intent = new Intent(this, TugasActivity.class);
            startActivity(intent);
        });

        // Menu Riwayat
        LinearLayout menuRiwayat = findViewById(R.id.menuRiwayat);
        menuRiwayat.setOnClickListener(view -> {
            Intent intent = new Intent(this, HistoriAbsensiActivity.class);
            startActivity(intent);
        });

        LinearLayout menuSoon = findViewById(R.id.menuSoon);
        menuSoon.setOnClickListener(view -> {
            Toast.makeText(this, "Fitur Baru akan Segera hadir !!!", Toast.LENGTH_SHORT).show();
        });

        ImageView iconProfile = findViewById(R.id.iconProfile);
        iconProfile.setOnClickListener(view -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
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
                Intent intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
            } else {
                Log.e("DashboardMahasiswaActivity", "Menu tidak dikenal");
            }

            return true;
        });
    }
}
