package com.app.midclassapp;

import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ValidasiPresensiActivity extends AppCompatActivity {

    private RecyclerView historyRecyclerView; // RecyclerView untuk menampilkan data histori presensi
    private ValidasiPresensiAdapter historyAdapter; // Adapter untuk RecyclerView
    private ArrayList<Absensi> historyAbsensi; // List untuk menyimpan data histori presensi
    private ArrayList<Absensi> filteredAbsensi; // List untuk menyimpan data histori presensi yang sudah difilter
    private Map<String, Absensi> selectedAbsensiMap; // Map untuk menyimpan absensi yang dipilih untuk divalidasi

    private FirebaseFirestore db; // Instans Firestore untuk berinteraksi dengan database
    private String matkulDosen; // Mata kuliah yang diajarkan oleh dosen
    private String nimOrNip; // NIM/NIP dosen

    private TextView tvSelectedDate; // TextView untuk menampilkan tanggal yang dipilih

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validasi_presensi);

        db = FirebaseFirestore.getInstance(); // Inisialisasi Firestore
        historyAbsensi = new ArrayList<>(); // Inisialisasi list histori absensi
        filteredAbsensi = new ArrayList<>(); // Inisialisasi list histori absensi yang sudah difilter
        selectedAbsensiMap = new HashMap<>(); // Inisialisasi map untuk menyimpan absensi yang dipilih

        // Ambil nimOrNip dari SharedPreferences untuk mengidentifikasi dosen
        nimOrNip = getSharedPreferences("MiDClassPrefs", MODE_PRIVATE).getString("nimOrNip", null);
        if (nimOrNip == null) {
            Toast.makeText(this, "Data dosen tidak ditemukan.", Toast.LENGTH_SHORT).show();
            finish(); // Jika nimOrNip tidak ditemukan, keluar dari activity
            return;
        }

        // Setup RecyclerView
        historyRecyclerView = findViewById(R.id.historyRecyclerView); // Menemukan RecyclerView dari layout
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this)); // Set layout manager
        historyAdapter = new ValidasiPresensiAdapter(this, filteredAbsensi, selectedAbsensiMap); // Inisialisasi adapter
        historyRecyclerView.setAdapter(historyAdapter); // Set adapter ke RecyclerView

        // Tombol Validasi yang digunakan untuk memvalidasi absensi yang dipilih
        Button btnValidasi = findViewById(R.id.btnValidasi);
        btnValidasi.setOnClickListener(v -> validasiPresensi()); // Menambahkan event listener ke tombol

        // TextView untuk memilih tanggal
        tvSelectedDate = findViewById(R.id.tv_selected_date);

        // Listener untuk TextView (memunculkan DatePicker)
        tvSelectedDate.setOnClickListener(v -> showDatePickerDialog(date -> tvSelectedDate.setText(date)));

        // Ambil mata kuliah dosen dari Firestore
        getMatkulDosen();
    }

    // Mengambil data mata kuliah dosen dari Firestore
    private void getMatkulDosen() {
        db.collection("users").document(nimOrNip).get() // Ambil dokumen dosen berdasarkan nimOrNip
                .addOnSuccessListener(documentSnapshot -> {
                    matkulDosen = documentSnapshot.getString("matkul"); // Ambil mata kuliah dari dokumen
                    if (matkulDosen == null || matkulDosen.isEmpty()) {
                        Toast.makeText(this, "Mata kuliah dosen tidak ditemukan.", Toast.LENGTH_SHORT).show();
                        finish(); // Jika mata kuliah kosong, keluar dari activity
                        return;
                    }
                    getHistoryPresensi(); // Jika berhasil, ambil histori presensi
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Gagal mendapatkan mata kuliah dosen.", Toast.LENGTH_SHORT).show()); // Menangani kegagalan
    }

    // Mengambil histori presensi dari Firestore
    private void getHistoryPresensi() {
        db.collection("presensi")
                .whereEqualTo("matkul", matkulDosen) // Ambil data presensi berdasarkan mata kuliah dosen
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        // Ambil data presensi dari setiap dokumen
                        String nimOrNip = document.getString("nimOrNip");
                        Absensi absensi = new Absensi(
                                document.getId(), // ID dokumen sebagai documentId
                                document.getString("matkul"),
                                document.getTimestamp("timestamp"),
                                document.getString("keterangan"),
                                document.getString("photo"),
                                nimOrNip, // Ganti nama mahasiswa dengan NIM
                                formatTimestamp(document.getTimestamp("timestamp")) // Menambahkan tanggal sebagai string
                        );
                        historyAbsensi.add(absensi); // Tambahkan absensi ke list
                    }
                    filterByDate(); // Filter data berdasarkan tanggal jika sudah ada histori presensi
                    historyAdapter.notifyDataSetChanged(); // Beri tahu adapter untuk memperbarui tampilan
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Gagal mengambil data presensi.", Toast.LENGTH_SHORT).show()); // Menangani kegagalan
    }

    // Fungsi untuk memvalidasi presensi yang dipilih
    private void validasiPresensi() {
        for (Absensi absensi : selectedAbsensiMap.values()) {
            // Mengecek apakah presensi sudah tervalidasi atau belum
            if (absensi.getKeterangan().contains("Tervalidasi")) {
                Toast.makeText(this, "Presensi untuk NIM " + absensi.getNimOrNip() + " sudah tervalidasi!", Toast.LENGTH_SHORT).show();
                continue;  // Jika sudah tervalidasi, lanjutkan ke absensi berikutnya
            }

            String newKeterangan = absensi.getKeterangan() + " Tervalidasi"; // Menambahkan keterangan 'Tervalidasi'
            db.collection("presensi").document(absensi.getDocumentId())
                    .update("keterangan", newKeterangan) // Update keterangan di Firestore
                    .addOnSuccessListener(aVoid -> {
                        absensi.setKeterangan(newKeterangan); // Update keterangan di local
                        historyAdapter.notifyDataSetChanged(); // Beri tahu adapter untuk memperbarui tampilan
                        Toast.makeText(this, "Validasi berhasil untuk NIM " + absensi.getNimOrNip(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Gagal memvalidasi presensi.", Toast.LENGTH_SHORT).show()); // Menangani kegagalan
        }
    }

    // Fungsi untuk memfilter histori berdasarkan tanggal yang dipilih
    private void filterByDate() {
        String selectedDate = tvSelectedDate.getText().toString();
        if (!selectedDate.equals("Pilih Tanggal")) {
            filteredAbsensi.clear();
            for (Absensi absensi : historyAbsensi) {
                if (absensi.getTanggal().equals(selectedDate)) {
                    filteredAbsensi.add(absensi); // Menambahkan absensi yang cocok dengan tanggal ke filteredAbsensi
                }
            }
        } else {
            filteredAbsensi.clear();
            filteredAbsensi.addAll(historyAbsensi); // Jika tidak ada filter, tampilkan semua histori
        }
        historyAdapter.notifyDataSetChanged();
    }

    // Fungsi untuk memunculkan DatePickerDialog
    private void showDatePickerDialog(DatePickerCallback callback) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    // Format tanggal ke bentuk "YYYY-MM-DD"
                    String formattedDate = year + "-" + String.format("%02d", (month + 1)) + "-" + String.format("%02d", dayOfMonth);
                    callback.onDateSelected(formattedDate);
                    filterByDate(); // Filter data setelah tanggal dipilih
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    // Helper method untuk memformat timestamp menjadi string dengan format yang diinginkan
    public static String formatTimestamp(Timestamp timestamp) {
        if (timestamp == null) return "N/A"; // Jika timestamp null, return "N/A"
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(timestamp.toDate()); // Format timestamp
    }

    // Interface untuk callback tanggal
    interface DatePickerCallback {
        void onDateSelected(String date);
    }

    // Helper method untuk mendekode gambar yang disimpan dalam format Base64
    public static Bitmap decodeBase64(String encodedImage) {
        byte[] decodedBytes = Base64.decode(encodedImage, Base64.DEFAULT); // Decode Base64 ke byte array
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length); // Mengubah byte array menjadi gambar
    }
}
