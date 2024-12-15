package com.app.midclassapp;

import android.util.Log;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class PresensiActivity extends AppCompatActivity {

    private Spinner spinnerMatkul;
    private RadioGroup radioGroupKeterangan;
    private Button btnAmbilFoto, btnSubmitPresensi;
    private ImageView imagePreview;
    private TextView statusPresensi;
    private ProgressBar progressBar;

    private FirebaseFirestore db;
    private Bitmap photo; // Foto bukti presensi

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presensi);

        // Inisialisasi Firestore
        db = FirebaseFirestore.getInstance();

        // Inisialisasi UI
        spinnerMatkul = findViewById(R.id.spinnerMatkul);
        radioGroupKeterangan = findViewById(R.id.radioGroupKeterangan);
        btnAmbilFoto = findViewById(R.id.btnAmbilFoto);
        btnSubmitPresensi = findViewById(R.id.btnSubmitPresensi);
        imagePreview = findViewById(R.id.imagePreview);
        statusPresensi = findViewById(R.id.statusPresensi);
        progressBar = findViewById(R.id.progressBar);

        // Mengisi spinner dengan data dari strings.xml
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.matkul_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMatkul.setAdapter(adapter);

        // Tombol Ambil Foto
        btnAmbilFoto.setOnClickListener(v -> {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(takePictureIntent, 1001);
        });

        // Tombol Submit Presensi
        btnSubmitPresensi.setOnClickListener(v -> submitPresensi());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            photo = (Bitmap) data.getExtras().get("data");
            imagePreview.setImageBitmap(photo);
        } else {
            Toast.makeText(this, "Gagal mengambil foto", Toast.LENGTH_SHORT).show();
        }
    }

    private void submitPresensi() {
        int selectedId = radioGroupKeterangan.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = findViewById(selectedId);

        if (selectedRadioButton == null) {
            Toast.makeText(this, "Harap pilih keterangan kehadiran!", Toast.LENGTH_SHORT).show();
            return;
        }

        String keterangan = selectedRadioButton.getText().toString();
        final String matkul = spinnerMatkul.getSelectedItem().toString();

        if (keterangan.equals("Hadir") && photo == null) {
            Toast.makeText(this, "Harap ambil foto sebagai bukti kehadiran!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // Mendapatkan hari dalam bahasa Indonesia
        String hariIni = new SimpleDateFormat("EEEE", Locale.getDefault()).format(new Date());
        hariIni = convertHariToIndonesian(hariIni); // Mengubah hari ke bahasa Indonesia
        String waktuSekarang = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

        // Melakukan query ke Firestore untuk mengecek jadwal
        db.collection("jadwal")
                .whereEqualTo("hari", hariIni)
                .whereEqualTo("matkul", matkul)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "Jadwal tidak ditemukan untuk hari ini!", Toast.LENGTH_SHORT).show();
                    } else {
                        // Jadwal ditemukan, lanjutkan untuk cek dan simpan presensi
                        cekPresensiHariIni(matkul, keterangan);
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Log.e("SubmitPresensi", "Gagal mengambil data jadwal", e);
                    Toast.makeText(this, "Gagal memeriksa jadwal", Toast.LENGTH_SHORT).show();
                });
    }

    // Fungsi untuk mengonversi hari dalam bahasa Inggris ke bahasa Indonesia
    private String convertHariToIndonesian(String hari) {
        switch (hari) {
            case "Monday":
                return "Senin";
            case "Tuesday":
                return "Selasa";
            case "Wednesday":
                return "Rabu";
            case "Thursday":
                return "Kamis";
            case "Friday":
                return "Jumat";
            case "Saturday":
                return "Sabtu";
            case "Sunday":
                return "Minggu";
            default:
                return hari; // Kembalikan nama hari yang sama jika tidak ditemukan
        }
    }

    private void cekPresensiHariIni(String matkul, String keterangan) {
        // Ambil NIM/NIP dari SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MiDClassPrefs", MODE_PRIVATE);
        String nimOrNip = sharedPreferences.getString("nimOrNip", "");

        String tanggalHariIni = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        db.collection("presensi")
                .whereEqualTo("nimOrNip", nimOrNip)
                .whereEqualTo("matkul", matkul)
                .whereEqualTo("tanggal", tanggalHariIni)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        simpanPresensi(matkul, keterangan, nimOrNip, tanggalHariIni);
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "Anda sudah absen hari ini untuk mata kuliah ini!", Toast.LENGTH_SHORT).show();
                        statusPresensi.setText("Sudah Melakukan Absensi");
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Gagal mengecek presensi", Toast.LENGTH_SHORT).show();
                });
    }

    private void simpanPresensi(String matkul, String keterangan, String nimOrNip, String tanggal) {
        String encodedPhoto = "";

        if (photo != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 75, baos);
            byte[] byteArray = baos.toByteArray();
            encodedPhoto = Base64.encodeToString(byteArray, Base64.DEFAULT);
        }

        Map<String, Object> presensi = new HashMap<>();
        presensi.put("matkul", matkul);
        presensi.put("keterangan", keterangan);
        presensi.put("nimOrNip", nimOrNip);
        presensi.put("tanggal", tanggal);
        presensi.put("photo", encodedPhoto);
        presensi.put("timestamp", FieldValue.serverTimestamp()); // Menambahkan timestamp waktu server

        db.collection("presensi")
                .add(presensi)
                .addOnSuccessListener(documentReference -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Presensi berhasil disubmit!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Gagal mengirim data presensi!", Toast.LENGTH_SHORT).show();
                });
    }


}
