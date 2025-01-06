package com.app.midclassapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

// Activity Untuk Dashboard Admin
public class DashboardAdminActivity extends AppCompatActivity {

    private TextView dashboardUserName, dashboardJurusan, dashboardKampus;
    private ImageView logoImageView, iconProfile;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_admin);

        // Inisialisasi instance Firestore untuk mengelola database
        db = FirebaseFirestore.getInstance();

        // Inisialisasi semua elemen UI yang diperlukan
        initializeViews();

        // Memuat data pengguna dari Firestore dan menampilkan ke dashboard
        loadUserData();

        // Menyiapkan menu klik pada dashboard
        setupMenuActions();

        // Menyiapkan Bottom Navigation untuk fitur tambahan
        setupBottomNavigation();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Ambil nimOrNip dari SharedPreferences untuk mengecek apakah pengguna sudah login
        String nimOrNip = getSharedPreferences("MiDClassPrefs", MODE_PRIVATE)
                .getString("nimOrNip", null);

        if (nimOrNip == null) {
            // Jika nimOrNip tidak ditemukan, pengguna akan dialihkan ke halaman login
            Toast.makeText(this, "Anda belum login", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Query Firestore untuk mengambil data pengguna berdasarkan nimOrNip
        db.collection("users")
                .whereEqualTo("nimOrNip", nimOrNip)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);

                        // Ambil data pengguna dari Firestore
                        String username = document.getString("username");
                        String jurusan = document.getString("jurusan");
                        String kampus = document.getString("kampus");
                        String role = document.getString("role");
                        String base64Image = document.getString("profile_picture");

                        // Jika ada gambar profil, dekode dari base64 dan tampilkan
                        if (base64Image != null && !base64Image.isEmpty()) {
                            try {
                                byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inSampleSize = 4; // Mengurangi ukuran gambar
                                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length, options);
                                iconProfile.setImageBitmap(decodedByte);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        // Menampilkan data pengguna ke elemen UI
                        dashboardUserName.setText(username != null ? username : "Nama tidak ditemukan");
                        dashboardJurusan.setText(jurusan != null ? jurusan : "Jurusan tidak ditemukan");
                        dashboardKampus.setText(kampus != null ? kampus : "Kampus tidak ditemukan");
                    } else {
                        Toast.makeText(this, "Data pengguna tidak ditemukan", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Gagal memuat data pengguna", Toast.LENGTH_SHORT).show();
                });
    }

    /*
     * Inisialisasi elemen UI dari layout activity_dashboard_admin.
     * Method ini digunakan untuk menghubungkan elemen UI di XML ke variabel dalam kode Java.
     */
    private void initializeViews() {
        dashboardUserName = findViewById(R.id.dashboardUserName);
        dashboardJurusan = findViewById(R.id.dashboardJurusan);
        dashboardKampus = findViewById(R.id.dashboardKampus);
        logoImageView = findViewById(R.id.logoImageView);
        iconProfile = findViewById(R.id.iconProfile);
    }

    /*
     * Memuat data pengguna dari Firestore berdasarkan nimOrNip yang disimpan di SharedPreferences.
     * Method ini akan menampilkan data pengguna pada elemen UI dashboard.
     */
    private void loadUserData() {
        String nimOrNip = getSharedPreferences("MiDClassPrefs", MODE_PRIVATE)
                .getString("nimOrNip", null);

        // Query Firestore untuk mendapatkan data pengguna
        db.collection("users").whereEqualTo("nimOrNip", nimOrNip)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);

                        // Ambil data dari dokumen Firestore
                        String username = document.getString("username");
                        String jurusan = document.getString("jurusan");
                        String kampus = document.getString("kampus");

                        // Tampilkan data pengguna di elemen UI
                        dashboardUserName.setText(username != null ? username : "Nama tidak ditemukan");
                        dashboardJurusan.setText(jurusan != null ? jurusan : "Jurusan tidak ditemukan");
                        dashboardKampus.setText(kampus != null ? kampus : "Kampus tidak ditemukan");
                    } else {
                        Toast.makeText(this, "Data pengguna tidak ditemukan", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Gagal memuat data pengguna", Toast.LENGTH_SHORT).show();
                });
    }

    /*
     * Menyiapkan tindakan pada menu di dashboard admin, seperti Update Jadwal, Update Kalender, Riwayat, dll.
     * Setiap menu akan diarahkan ke activity yang sesuai.
     */
    private void setupMenuActions() {
        LinearLayout menuUpdateJadwal = findViewById(R.id.menuUpdateJadwal);
        menuUpdateJadwal.setOnClickListener(view -> {
            Intent intent = new Intent(this, AdminJadwalActivity.class);
            startActivity(intent);
        });

        LinearLayout menuUpdateCalendar = findViewById(R.id.menuUpdateCalendar);
        menuUpdateCalendar.setOnClickListener(view -> {
            Intent intent = new Intent(this, AdminAcademicCalendarActivity.class);
            startActivity(intent);
        });

        LinearLayout menuRiwayat = findViewById(R.id.menuRiwayat);
        menuRiwayat.setOnClickListener(view -> {
            Intent intent = new Intent(this, AdminRiwayatPresensiActivity.class);
            startActivity(intent);
        });

        LinearLayout menuSoon = findViewById(R.id.menuSoon);
        menuSoon.setOnClickListener(view -> {
            Toast.makeText(this, "Fitur Baru akan Segera hadir !!!", Toast.LENGTH_SHORT).show();
        });

        iconProfile.setOnClickListener(view -> {
            Intent intent = new Intent(this, ProfileAdminActivity.class);
            startActivity(intent);
        });
    }

    // Menyiapkan navigasi bawah untuk berpindah antar fitur, seperti Home, Kalender Akademik, dan Profil Admin.
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
                Intent intent = new Intent(this, ProfileAdminActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Menu tidak dikenal", Toast.LENGTH_SHORT).show();
            }

            return true;
        });
    }
}
