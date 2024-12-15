package com.app.midclassapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AcademicCalendarActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AcademicEventAdapter adapter;
    private List<AcademicEvent> events;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private TextView noDataTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_academic_calendar);

        // Inisialisasi Firestore dan komponen tampilan
        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        noDataTextView = findViewById(R.id.noDataTextView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        events = new ArrayList<>();
        adapter = new AcademicEventAdapter(events);
        recyclerView.setAdapter(adapter);

        // Memuat data dari Firestore
        fetchAcademicCalendar();
    }

    private void fetchAcademicCalendar() {
        progressBar.setVisibility(View.VISIBLE);
        noDataTextView.setVisibility(View.GONE);

        db.collection("academic_calendar")
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            events.clear();
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                String no = document.getString("no");
                                String eventName = document.getString("event_name");
                                String dateRange = document.getString("date_range");

                                events.add(new AcademicEvent(no, eventName, dateRange));
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.e("AcademicCalendar", "Tidak ada data yang ditemukan di Firestore.");
                            noDataTextView.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Log.e("AcademicCalendar", "Gagal memuat data dari Firestore.", task.getException());
                        noDataTextView.setVisibility(View.VISIBLE);
                    }
                });
    }
}
