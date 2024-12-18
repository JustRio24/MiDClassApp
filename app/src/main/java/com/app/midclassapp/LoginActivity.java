package com.app.midclassapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

// Activity untuk Login Page
public class LoginActivity extends AppCompatActivity {

    // Deklarasi komponen UI dan Firestore
    private EditText nimOrNipEditText, passwordEditText; // Input untuk NIM/NIP dan Password
    private Spinner roleSpinner; // Spinner untuk memilih peran (Mahasiswa/Dosen)
    private FirebaseFirestore db; // Instance Firestore

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity); // menghubungkan layout untuk activity login

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

    // Fungsi untuk memvalidasi input login
    private void validateLogin() {
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
            default:
                Toast.makeText(this, "Role tidak valid", Toast.LENGTH_SHORT).show(); // Role tidak dikenali
                return;
        }

        startActivity(intent); // Buka activity dashboard yang sesuai
        finish(); // Tutup activity login
    }
}
