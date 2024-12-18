package com.app.midclassapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ValidasiPresensiActivity extends AppCompatActivity {

    private RecyclerView historyRecyclerView;
    private HistoryPresensiAdapter historyAdapter;
    private ArrayList<Absensi> historyAbsensi;
    private Map<String, Absensi> selectedAbsensiMap;

    private FirebaseFirestore db;
    private String matkulDosen;
    private String nimOrNip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validasi_presensi);

        db = FirebaseFirestore.getInstance();
        historyAbsensi = new ArrayList<>();
        selectedAbsensiMap = new HashMap<>();

        // Ambil nimOrNip dari SharedPreferences
        nimOrNip = getSharedPreferences("MiDClassPrefs", MODE_PRIVATE).getString("nimOrNip", null);
        if (nimOrNip == null) {
            Toast.makeText(this, "Data dosen tidak ditemukan.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Setup RecyclerView
        historyRecyclerView = findViewById(R.id.historyRecyclerView);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        historyAdapter = new HistoryPresensiAdapter(this, historyAbsensi, selectedAbsensiMap);
        historyRecyclerView.setAdapter(historyAdapter);

        // Tombol Validasi
        Button btnValidasi = findViewById(R.id.btnValidasi);
        btnValidasi.setOnClickListener(v -> validasiPresensi());

        // Ambil data presensi
        getMatkulDosen();
    }

    private void getMatkulDosen() {
        db.collection("users").document(nimOrNip).get().addOnSuccessListener(documentSnapshot -> {
            matkulDosen = documentSnapshot.getString("matkul");
            if (matkulDosen == null || matkulDosen.isEmpty()) {
                Toast.makeText(this, "Mata kuliah dosen tidak ditemukan.", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            getHistoryPresensi();
        }).addOnFailureListener(e -> Log.e("ValidasiPresensi", "Gagal mendapatkan mata kuliah dosen.", e));
    }

    private void getHistoryPresensi() {
        db.collection("presensi")
                .whereEqualTo("matkul", matkulDosen)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String nimOrNip = document.getString("nimOrNip");
                        Absensi absensi = new Absensi(
                                document.getId(),
                                document.getString("matkul"),
                                document.getTimestamp("timestamp"),
                                document.getString("keterangan"),
                                document.getString("photo"),
                                nimOrNip // Ganti nama mahasiswa dengan NIM
                        );
                        historyAbsensi.add(absensi);
                    }
                    historyAdapter.notifyDataSetChanged();
                }).addOnFailureListener(e -> Log.e("ValidasiPresensi", "Gagal mengambil data presensi.", e));
    }

    private void validasiPresensi() {
        for (Absensi absensi : selectedAbsensiMap.values()) {
            // Mengecek apakah sudah tervalidasi atau belum
            if (absensi.getKeterangan().contains("Tervalidasi")) {
                Toast.makeText(this, "Presensi untuk NIM " + absensi.getNimOrNip() + " sudah tervalidasi!", Toast.LENGTH_SHORT).show();
                continue;  // Jika sudah tervalidasi, lanjutkan ke absensi berikutnya
            }

            String newKeterangan = absensi.getKeterangan() + " Tervalidasi";
            db.collection("presensi").document(absensi.getDocumentId())
                    .update("keterangan", newKeterangan)
                    .addOnSuccessListener(aVoid -> {
                        absensi.setKeterangan(newKeterangan); // Update keterangan di local
                        historyAdapter.notifyDataSetChanged();
                        Toast.makeText(this, "Validasi berhasil untuk NIM " + absensi.getNimOrNip(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Log.e("ValidasiPresensi", "Gagal memvalidasi presensi.", e));
        }
    }

    public static String formatTimestamp(Timestamp timestamp) {
        if (timestamp == null) return "N/A";
        return new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(timestamp.toDate());
    }

    public static Bitmap decodeBase64(String encodedImage) {
        byte[] decodedBytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}
