package com.app.midclassapp;

import static com.app.midclassapp.ValidasiPresensiActivity.formatTimestamp;

import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AdminRiwayatPresensiActivity extends AppCompatActivity {

    private Spinner spinnerMatkul;
    private EditText editTextNimOrNip;
    private TextView tvSelectedDate;
    private Button buttonFilter;
    private RecyclerView recyclerViewHistori;
    private ProgressBar progressBar;

    private FirebaseFirestore db;
    private RiwayatPresensiAdapter adapter;
    private List<Absensi> absensiList;

    private String selectedMatkul = "";
    private String selectedNimOrNip = "";
    private String selectedDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_riwayat_presensi);

        // Inisialisasi UI
        spinnerMatkul = findViewById(R.id.spinner_matkul);
        editTextNimOrNip = findViewById(R.id.editTextNimOrNip);
        tvSelectedDate = findViewById(R.id.tv_selected_date);
        buttonFilter = findViewById(R.id.button_filter);
        recyclerViewHistori = findViewById(R.id.recyclerViewHistori);
        progressBar = findViewById(R.id.progressBar);

        recyclerViewHistori.setLayoutManager(new LinearLayoutManager(this));
        absensiList = new ArrayList<>();
        adapter = new RiwayatPresensiAdapter(absensiList);
        recyclerViewHistori.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        // Mengambil histori presensi awal saat activity pertama kali dibuat
        getHistoryPresensi();

        // Event listener untuk memilih tanggal
        tvSelectedDate.setOnClickListener(v -> showDatePicker());

        // Event listener untuk tombol filter
        buttonFilter.setOnClickListener(v -> applyFilter());
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            // Format tanggal menjadi "YYYY-MM-DD"
            selectedDate = String.format("%04d-%02d-%02d", year1, month1 + 1, dayOfMonth);
            tvSelectedDate.setText(selectedDate);
        }, year, month, day);

        datePickerDialog.show();
    }

    // Mengambil histori presensi dari Firestore
    private void getHistoryPresensi() {
        db.collection("presensi")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    absensiList.clear(); // Clear daftar absensi sebelum mengisi dengan data baru
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        // Ambil data presensi dari setiap dokumen
                        String nimOrNip = document.getString("nimOrNip");
                        String matkul = document.getString("matkul");
                        String keterangan = document.getString("keterangan");
                        String fotoBase64 = document.getString("fotoBase64");  // Pastikan nama field sesuai
                        String tanggal = formatTimestamp(document.getTimestamp("timestamp"));
                        Absensi absensi = new Absensi(
                                document.getId(),  // ID dokumen sebagai documentId
                                matkul,
                                document.getTimestamp("timestamp"),
                                keterangan,
                                fotoBase64,
                                nimOrNip,  // Ganti nama mahasiswa dengan NIM
                                tanggal // Menambahkan tanggal sebagai string
                        );
                        absensiList.add(absensi); // Tambahkan absensi ke list
                    }
                    adapter.notifyDataSetChanged(); // Beri tahu adapter untuk memperbarui tampilan
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Gagal mengambil data presensi.", Toast.LENGTH_SHORT).show()); // Menangani kegagalan
    }

    private void applyFilter() {
        selectedMatkul = spinnerMatkul.getSelectedItem().toString();
        selectedNimOrNip = editTextNimOrNip.getText().toString().trim();

        // Menampilkan ProgressBar
        progressBar.setVisibility(View.VISIBLE);
        recyclerViewHistori.setVisibility(View.GONE);

        Query query = db.collection("presensi");

        // Validasi dan penambahan filter
        if (!selectedMatkul.isEmpty() && !selectedMatkul.equals("Pilih Mata Kuliah")) {
            query = query.whereEqualTo("matkul", selectedMatkul);
        }

        if (!selectedNimOrNip.isEmpty()) {
            query = query.whereEqualTo("nimOrNip", selectedNimOrNip);
        }

        if (!selectedDate.isEmpty()) {
            query = query.whereEqualTo("tanggal", selectedDate);
        }

        // Batasi hasil query untuk mengoptimalkan waktu loading (dengan limit)
        query.limit(100).get().addOnSuccessListener(queryDocumentSnapshots -> {
            progressBar.setVisibility(View.GONE);
            absensiList.clear(); // Clear previous results
            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Absensi absensi = document.toObject(Absensi.class);
                    absensiList.add(absensi); // Menambahkan absensi ke daftar
                }
                recyclerViewHistori.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged(); // Memperbarui tampilan
            } else {
                Toast.makeText(this, "Data tidak ditemukan.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Gagal memuat data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

}
