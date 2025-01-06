package com.app.midclassapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

// Activity untuk Login Page
public class LoginActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    // Deklarasi komponen UI dan Firestore
    private EditText nimOrNipEditText, passwordEditText; // Input untuk NIM/NIP dan Password
    private Spinner roleSpinner; // Spinner untuk memilih peran
    private FirebaseFirestore db; // Instance Firestore

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity); // menghubungkan layout untuk activity login

        // Meminta izin lokasi jika diperlukan
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }

        // Cek Wi-Fi saat aplikasi dibuka
        if (!isConnectedToCampusWifi(this)) {
            showWifiWarning();
        }

        // Inisialisasi komponen UI
        nimOrNipEditText = findViewById(R.id.nimOrNip);
        passwordEditText = findViewById(R.id.passwordEditText);
        roleSpinner = findViewById(R.id.roleSpinner);

        // Inisialisasi Firestore
        db = FirebaseFirestore.getInstance();

        // Menambahkan listener pada tombol login
        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(v -> validateLogin());
    }

    // Fungsi untuk memeriksa apakah perangkat terhubung ke Wi-Fi kampus
    private boolean isConnectedToCampusWifi(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo != null) {
                String ssid = wifiInfo.getSSID();
                Log.d("WiFiSSID", "Terhubung ke SSID: " + ssid); // Menambahkan log untuk melihat SSID yang didapat
                // Ganti "Private_Wifi" dengan SSID Wi-Fi kampus Anda
                return ssid != null && ssid.replace("\"", "").equals("Private_Wifi");
            }
        }
        return false;
    }

    // Fungsi untuk menampilkan peringatan jika tidak terhubung ke Wi-Fi kampus
    private void showWifiWarning() {
        new AlertDialog.Builder(this)
                .setTitle("Koneksi Tidak Valid")
                .setMessage("Aplikasi hanya dapat digunakan jika terhubung ke Wi-Fi kampus.")
                .setPositiveButton("Keluar", (dialog, which) -> finish())
                .setNegativeButton("Coba Lagi", (dialog, which) -> {
                    // Cek koneksi lagi jika user menekan "Coba Lagi"
                    if (!isConnectedToCampusWifi(this)) {
                        Toast.makeText(this, "Masih belum terhubung ke Wi-Fi kampus.", Toast.LENGTH_SHORT).show();
                    } else {
                        // Lanjutkan jika sudah terhubung
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .show();
    }


    @Override
    protected void onResume() {
        super.onResume();

        // Cek kembali koneksi Wi-Fi saat aplikasi kembali ke foreground
        if (!isConnectedToCampusWifi(this)) {
            showWifiWarning();
        }
    }

    // Fungsi untuk memvalidasi input login
    private void validateLogin() {

        // Validasi jika Wi-Fi tidak sesuai
        if (!isConnectedToCampusWifi(this)) {
            Toast.makeText(this, "Harap terhubung ke Wi-Fi kampus terlebih dahulu.", Toast.LENGTH_SHORT).show();
            return; // Mencegah login jika Wi-Fi tidak sesuai
        }

        // Mengambil nilai input dari user
        String nimOrNip = nimOrNipEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String role = roleSpinner.getSelectedItem().toString().trim();

        // Validasi apakah NIM/NIP dan Password kosong
        if (nimOrNip.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "NIM/NIP dan Password tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        // Query ke Firestore untuk memeriksa kecocokan data login
        db.collection("users")
                .whereEqualTo("nimOrNip", nimOrNip) // Cek apakah NIM/NIP sesuai
                .whereEqualTo("password", password) // Cek apakah Password sesuai
                .whereEqualTo("role", role) // Cek apakah Role sesuai (Mahasiswa/Dosen)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Jika login berhasil
                        Toast.makeText(this, "Login berhasil", Toast.LENGTH_SHORT).show();

                        // Simpan NIM/NIP ke SharedPreferences agar tetap tersimpan setelah login
                        getSharedPreferences("MiDClassPrefs", MODE_PRIVATE)
                                .edit()
                                .putString("nimOrNip", nimOrNip)
                                .apply();

                        // Arahkan user ke dashboard berdasarkan role (Mahasiswa atau Dosen)
                        navigateToDashboard(role);
                    } else {
                        // Jika login gagal
                        Toast.makeText(this, "NIM/NIP atau Password salah atau Role tidak tepat!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // Jika ada kesalahan saat mengambil data dari Firestore
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Fungsi untuk mengarahkan user ke dashboard sesuai dengan role-nya
    private void navigateToDashboard(String role) {
        Intent intent;
        switch (role) {
            case "Mahasiswa":
                intent = new Intent(LoginActivity.this, DashboardMahasiswaActivity.class); // Dashboard Mahasiswa
                break;
            case "Dosen":
                intent = new Intent(LoginActivity.this, DashboardDosenActivity.class); // Dashboard Dosen
                break;
            case "Admin":
                intent = new Intent(LoginActivity.this, DashboardAdminActivity.class); // Dashboard Dosen
                break;
            default:
                Toast.makeText(this, "Role tidak valid", Toast.LENGTH_SHORT).show(); // Role tidak dikenali
                return;
        }

        startActivity(intent); // Buka activity dashboard yang sesuai
        finish(); // Tutup activity login
    }
}
