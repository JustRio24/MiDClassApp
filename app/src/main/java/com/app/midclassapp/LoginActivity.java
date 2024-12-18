package com.app.midclassapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText nimOrNipEditText, passwordEditText;
    private Spinner roleSpinner;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        // Initialize views
        nimOrNipEditText = findViewById(R.id.nimOrNip);
        passwordEditText = findViewById(R.id.passwordEditText);
        roleSpinner = findViewById(R.id.roleSpinner);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Set click listener for login button
        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(v -> validateLogin());
    }

    private void validateLogin() {
        // Get input values
        String nimOrNip = nimOrNipEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String role = roleSpinner.getSelectedItem().toString().trim();

        if (nimOrNip.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "NIM/NIP dan Password tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        // Query Firestore to validate login
        db.collection("users")
                .whereEqualTo("nimOrNip", nimOrNip)
                .whereEqualTo("password", password)
                .whereEqualTo("role", role)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Login berhasil
                        Toast.makeText(this, "Login berhasil", Toast.LENGTH_SHORT).show();

                        // Simpan nimOrNip ke SharedPreferences
                        getSharedPreferences("MiDClassPrefs", MODE_PRIVATE)
                                .edit()
                                .putString("nimOrNip", nimOrNip)
                                .apply();

                        // Pindah ke Dashboard sesuai role
                        navigateToDashboard(role);
                    } else {
                        // Login gagal
                        Toast.makeText(this, "NIM/NIP atau Password salah atau Role tidak tepat!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle error
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }



    private void navigateToDashboard(String role) {
        Intent intent;
        switch (role) {
            case "Mahasiswa":
                intent = new Intent(LoginActivity.this, DashboardMahasiswaActivity.class);
                break;
            case "Dosen":
                intent = new Intent(LoginActivity.this, DashboardDosenActivity.class);
                break;
            default:
                Toast.makeText(this, "Role tidak valid", Toast.LENGTH_SHORT).show();
                return;
        }

        startActivity(intent);
        finish(); // Close the login activity
    }
}
