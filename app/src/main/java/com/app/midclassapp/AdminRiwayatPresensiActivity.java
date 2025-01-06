package com.app.midclassapp;

import static com.app.midclassapp.ValidasiPresensiActivity.formatTimestamp;

import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdminRiwayatPresensiActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private RiwayatPresensiAdapter adapter;
    private List<Absensi> absensiList;
    private List<Absensi> filteredAbsensi;
    private ProgressBar progressBar;
    private Button buttonFilter;
    private Spinner spinnerMatkul;
    private TextView tvSelectedDate;
    private ImageView ivCalendarIcon;
    private EditText editTextNimOrNip;
    private String nimOrNip;
    private String selectedDate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_riwayat_presensi);

        db = FirebaseFirestore.getInstance();
        absensiList = new ArrayList<>(); // Inisialisasi list histori absensi
        filteredAbsensi = new ArrayList<>(); // Inisialisasi list histori absensi yang sudah difilter

        // Inisialisasi UI
        recyclerView = findViewById(R.id.recyclerViewHistori);
        progressBar = findViewById(R.id.progressBar);
        buttonFilter = findViewById(R.id.button_filter);
        spinnerMatkul = findViewById(R.id.spinner_matkul);
        tvSelectedDate = findViewById(R.id.tv_selected_date);
        ivCalendarIcon = findViewById(R.id.iv_calendar_icon);
        editTextNimOrNip = findViewById(R.id.editTextNimOrNip);

        tvSelectedDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                Calendar selectedCalendar = Calendar.getInstance();
                selectedCalendar.set(year, month, dayOfMonth);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                selectedDate = dateFormat.format(selectedCalendar.getTime());
                tvSelectedDate.setText(selectedDate);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadAbsensiData();

        buttonFilter.setOnClickListener(v -> {
            String selectedMatkul = spinnerMatkul.getSelectedItem().toString();
            String selectedDate = tvSelectedDate.getText().toString();
            String nimOrNip = editTextNimOrNip.getText().toString(); // Ambil nilai dari EditText

            if (selectedMatkul.equals("Pilih Mata Kuliah")) {
                selectedMatkul = null; // Set null jika tidak ada mata kuliah yang dipilih
            }

            // Panggil fungsi filter
            filterAbsensi(selectedMatkul, selectedDate, nimOrNip);
        });
    }

    private void loadAbsensiData() {
        absensiList = new ArrayList<>();

        db.collection("presensi")
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
                        absensiList.add(absensi); // Tambahkan absensi ke list
                    }

                    // Initialize the adapter here
                    if (adapter == null) {
                        adapter = new RiwayatPresensiAdapter(AdminRiwayatPresensiActivity.this, absensiList);
                        recyclerView.setAdapter(adapter);
                    }

                    filterAbsensi(null, null, null); // Tidak memfilter apapun di awal
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Gagal mengambil data presensi.", Toast.LENGTH_SHORT).show());
    }


    private void filterAbsensi(String matkul, String tanggal, String nimOrNip) {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        com.google.firebase.firestore.Query query = db.collection("presensi");
        boolean filterApplied = false;

        if (matkul != null && !matkul.isEmpty() && !matkul.equals("Pilih Mata Kuliah")) {
            query = query.whereEqualTo("matkul", matkul);
            filterApplied = true;
        }

        if (tanggal != null && !tanggal.isEmpty() && !tanggal.equals("Pilih Tanggal")) {
            query = query.whereEqualTo("tanggal", tanggal);
            filterApplied = true;
        }

        if (nimOrNip != null && !nimOrNip.isEmpty()) {
            query = query.whereEqualTo("nimOrNip", nimOrNip);
            filterApplied = true;
        }

        if (!filterApplied) {
            // Tidak ada filter yang diterapkan, maka tampilkan semua data
        }

        query.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        filteredAbsensi.clear();  // Kosongkan filtered list agar data yang difilter diperbarui
                        for (DocumentSnapshot document : task.getResult()) {
                            Absensi absensi = document.toObject(Absensi.class);
                            if (absensi != null) {
                                absensi.setDocumentId(document.getId());
                                filteredAbsensi.add(absensi);
                            }
                        }
                        // Memperbarui RecyclerView setelah data difilter
                        adapter.notifyDataSetChanged();
                        recyclerView.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(AdminRiwayatPresensiActivity.this, "Gagal memfilter data", Toast.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(View.GONE);
                });
    }

    // Helper method untuk mendekode gambar yang disimpan dalam format Base64
    public static Bitmap decode64(String encodedImage) {
        byte[] decodedBytes = Base64.decode(encodedImage, Base64.DEFAULT); // Decode Base64 ke byte array
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length); // Mengubah byte array menjadi gambar
    }
}
