package com.app.midclassapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.app.midclassapp.Jadwal; // Sesuaikan dengan model Jadwal Anda
import com.app.midclassapp.JadwalAdminAdapter; // Sesuaikan dengan adapter Anda

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class AdminJadwalActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private JadwalAdminAdapter adapter;
    private List<Jadwal> jadwalList;
    private FirebaseFirestore firestore;
    private CollectionReference jadwalRef;
    private Button addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_jadwal);

        recyclerView = findViewById(R.id.recyclerViewAdmin);
        addButton = findViewById(R.id.addButtonAdmin);

        firestore = FirebaseFirestore.getInstance();
        jadwalRef = firestore.collection("jadwal");

        jadwalList = new ArrayList<>();
        adapter = new JadwalAdminAdapter(this, jadwalList, new JadwalAdminAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Jadwal jadwal) {
                showEditDialog(jadwal);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        loadJadwalData();

        addButton.setOnClickListener(v -> {
            showAddDialog();
        });
    }

    private void loadJadwalData() {
        jadwalRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                jadwalList.clear();
                for (DocumentSnapshot document : task.getResult()) {
                    Jadwal jadwal = document.toObject(Jadwal.class);
                    jadwal.setId(document.getId());
                    jadwalList.add(jadwal);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    // Fungsi delete jadwal
    public void deleteJadwal(String id) {
        firestore.collection("jadwal")
                .document(id)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Jadwal Deleted", Toast.LENGTH_SHORT).show();
                    loadJadwalData();  // Setelah penghapusan, muat ulang jadwal
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to delete jadwal", Toast.LENGTH_SHORT).show());
    }

    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tambah Jadwal");
        View view = getLayoutInflater().inflate(R.layout.dialog_add_jadwal_admin, null);
        builder.setView(view);

        EditText dosenEditText = view.findViewById(R.id.dosenEditTextAdmin);
        EditText matkulEditText = view.findViewById(R.id.matkulEditTextAdmin);
        EditText hariEditText = view.findViewById(R.id.hariEditTextAdmin);
        EditText ruangEditText = view.findViewById(R.id.ruangEditTextAdmin);
        EditText waktuMulaiEditText = view.findViewById(R.id.waktuMulaiEditTextAdmin);
        EditText waktuSelesaiEditText = view.findViewById(R.id.waktuSelesaiEditTextAdmin);

        builder.setPositiveButton("Simpan", (dialog, which) -> {
            String dosen = dosenEditText.getText().toString();
            String matkul = matkulEditText.getText().toString();
            String hari = hariEditText.getText().toString();
            String ruang = ruangEditText.getText().toString();
            String waktuMulai = waktuMulaiEditText.getText().toString();
            String waktuSelesai = waktuSelesaiEditText.getText().toString();

            Jadwal jadwal = new Jadwal(UUID.randomUUID().toString(), dosen, matkul, hari, ruang, waktuMulai, waktuSelesai);
            jadwalRef.add(jadwal).addOnSuccessListener(documentReference -> {
                Toast.makeText(AdminJadwalActivity.this, "Jadwal berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                loadJadwalData();
            }).addOnFailureListener(e -> {
                Toast.makeText(AdminJadwalActivity.this, "Gagal menambahkan jadwal", Toast.LENGTH_SHORT).show();
            });
        });

        builder.setNegativeButton("Batal", null);
        builder.show();
    }

    private void showEditDialog(Jadwal jadwal) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Jadwal");
        View view = getLayoutInflater().inflate(R.layout.dialog_add_jadwal_admin, null);
        builder.setView(view);

        EditText dosenEditText = view.findViewById(R.id.dosenEditTextAdmin);
        EditText matkulEditText = view.findViewById(R.id.matkulEditTextAdmin);
        EditText hariEditText = view.findViewById(R.id.hariEditTextAdmin);
        EditText ruangEditText = view.findViewById(R.id.ruangEditTextAdmin);
        EditText waktuMulaiEditText = view.findViewById(R.id.waktuMulaiEditTextAdmin);
        EditText waktuSelesaiEditText = view.findViewById(R.id.waktuSelesaiEditTextAdmin);

        dosenEditText.setText(jadwal.getDosen());
        matkulEditText.setText(jadwal.getMatkul());
        hariEditText.setText(jadwal.getHari());
        ruangEditText.setText(jadwal.getRuang());
        waktuMulaiEditText.setText(jadwal.getWaktuMulai());
        waktuSelesaiEditText.setText(jadwal.getWaktuSelesai());

        builder.setPositiveButton("Update", (dialog, which) -> {
            Map<String, Object> updatedData = new HashMap<>();
            updatedData.put("dosen", dosenEditText.getText().toString());
            updatedData.put("matkul", matkulEditText.getText().toString());
            updatedData.put("hari", hariEditText.getText().toString());
            updatedData.put("ruang", ruangEditText.getText().toString());
            updatedData.put("waktuMulai", waktuMulaiEditText.getText().toString());
            updatedData.put("waktuSelesai", waktuSelesaiEditText.getText().toString());

            jadwalRef.document(jadwal.getId()).update(updatedData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(AdminJadwalActivity.this, "Jadwal berhasil diupdate", Toast.LENGTH_SHORT).show();
                        loadJadwalData();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(AdminJadwalActivity.this, "Gagal memperbarui jadwal", Toast.LENGTH_SHORT).show();
                    });
        });

        builder.setNegativeButton("Batal", null);
        builder.show();
    }
}
