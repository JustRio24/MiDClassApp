package com.app.midclassapp;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class JadwalActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private JadwalAdapter adapter;
    private List<Jadwal> jadwalList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jadwal);

        // Inisialisasi Firestore dan RecyclerView
        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recyclerViewJadwal);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        jadwalList = new ArrayList<>();

        // Ambil data dari Firestore
        loadJadwal();
    }

    private void loadJadwal() {
        db.collection("jadwal")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();

                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                String dosen = document.getString("dosen");
                                String matkul = document.getString("matkul");
                                String hari = document.getString("hari");
                                String ruang = document.getString("ruang");
                                String waktuMulai = document.getString("waktuMulai");
                                String waktuSelesai = document.getString("waktuSelesai");

                                Jadwal jadwal = new Jadwal(dosen, matkul, hari, ruang, waktuMulai, waktuSelesai);
                                jadwalList.add(jadwal);
                            }

                            // Inisialisasi adapter dan set ke RecyclerView
                            adapter = new JadwalAdapter(jadwalList);
                            recyclerView.setAdapter(adapter);
                        } else {
                            Toast.makeText(this, "Data jadwal kosong", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Gagal mengambil data", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
