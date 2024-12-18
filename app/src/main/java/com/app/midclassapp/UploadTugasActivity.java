package com.app.midclassapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UploadTugasActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private String matkulDosen; // Mata kuliah yang diajarkan oleh dosen
    private String nimOrNip; // ID dosen dari SharedPreferences

    private EditText edtTitle, edtDescription, edtDeadline;
    private Button btnUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_tugas);

        // Inisialisasi Firestore
        db = FirebaseFirestore.getInstance();

        // Ambil NIP dosen dari SharedPreferences
        nimOrNip = getSharedPreferences("MiDClassPrefs", MODE_PRIVATE).getString("nimOrNip", null);
        if (nimOrNip == null) {
            Toast.makeText(this, "Data dosen tidak ditemukan!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Inisialisasi komponen UI
        edtTitle = findViewById(R.id.edtTitle);
        edtDescription = findViewById(R.id.edtDescription);
        edtDeadline = findViewById(R.id.edtDeadline);
        btnUploadTask = findViewById(R.id.btnUploadTask);

        // Ambil mata kuliah dosen dari Firestore
        getMatkulDosen();

        // Tombol upload tugas/materi
        btnUploadTask.setOnClickListener(v -> uploadTask());
    }

    private void getMatkulDosen() {
        db.collection("users").document(nimOrNip)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        matkulDosen = documentSnapshot.getString("matkul");
                        if (TextUtils.isEmpty(matkulDosen)) {
                            Toast.makeText(this, "Mata kuliah dosen tidak ditemukan!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } else {
                        Toast.makeText(this, "Data dosen tidak ditemukan di Firestore!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Gagal memuat mata kuliah dosen!", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    finish();
                });
    }

    private void uploadTask() {
        // Validasi input
        String title = edtTitle.getText().toString().trim();
        String description = edtDescription.getText().toString().trim();
        String deadline = edtDeadline.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, "Judul tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Deskripsi tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(deadline)) {
            Toast.makeText(this, "Deadline tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(matkulDosen)) {
            Toast.makeText(this, "Mata kuliah tidak ditemukan!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Simpan data tugas/materi ke Firestore
        Map<String, Object> taskData = new HashMap<>();
        taskData.put("title", title);
        taskData.put("description", description);
        taskData.put("deadline", deadline);
        taskData.put("matkul", matkulDosen);
        taskData.put("uploadedBy", nimOrNip);

        db.collection("tugas_materi")
                .add(taskData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Tugas/Materi berhasil diunggah!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Gagal mengunggah tugas/materi!", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
    }
}
