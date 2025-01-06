package com.app.midclassapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminAcademicCalendarActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AcademicEventAdminAdapter adapter;
    private List<AcademicEvent> eventList;
    private FirebaseFirestore db;
    private CollectionReference eventsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_academic_calendar);

        recyclerView = findViewById(R.id.recyclerViewCalendarAdmin);
        eventList = new ArrayList<>();
        adapter = new AcademicEventAdminAdapter(this, eventList, new AcademicEventAdminAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(AcademicEvent event) {
                showEditDialog(event);  // Trigger edit dialog on item click
            }

            @Override
            public void onDeleteEvent(String eventNo) {
                deleteEvent(eventNo);  // Trigger delete on item delete
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("academic_calendar");

        // Add button listener to show the add dialog
        findViewById(R.id.addCalendarButtonAdmin).setOnClickListener(v -> showAddDialog());

        loadEvents();  // Load events from Firestore
    }

    private void loadEvents() {
        eventsRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                eventList.clear();  // Menghapus data lama
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {  // Iterasi untuk setiap dokumen
                    String no = document.getString("no");  // Mengambil 'no' dari field
                    String eventName = document.getString("event_name");  // Mengambil 'event_name' dari field
                    String dateRange = document.getString("date_range");  // Mengambil 'date_range' dari field

                    eventList.add(new AcademicEvent(no, eventName, dateRange));  // Menambahkan acara ke list
                }
                adapter.notifyDataSetChanged();  // Menyegarkan data pada adapter
            } else {
                Toast.makeText(AdminAcademicCalendarActivity.this, "No events found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(AdminAcademicCalendarActivity.this, "Error loading events", Toast.LENGTH_SHORT).show();
        });
    }

    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_academic_event_admin, null);

        final EditText noEditText = view.findViewById(R.id.noEditTextAdmin);
        final EditText eventNameEditText = view.findViewById(R.id.eventNameEditTextAdmin);
        final EditText dateRangeEditText = view.findViewById(R.id.dateRangeEditTextAdmin);

        builder.setView(view)
                .setTitle("Add New Event")
                .setPositiveButton("Save", (dialog, which) -> {
                    String no = noEditText.getText().toString();
                    String eventName = eventNameEditText.getText().toString();
                    String dateRange = dateRangeEditText.getText().toString();

                    if (no.isEmpty() || eventName.isEmpty() || dateRange.isEmpty()) {
                        Toast.makeText(AdminAcademicCalendarActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    AcademicEvent newEvent = new AcademicEvent(no, eventName, dateRange);
                    addEventToDatabase(newEvent);
                })
                .setNegativeButton("Cancel", null);

        builder.show();
    }

    private void addEventToDatabase(AcademicEvent event) {
        // Buat Map untuk menyimpan data dengan field sesuai dengan struktur Firestore
        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("no", event.getNo());
        eventMap.put("event_name", event.getEventName());  // Gunakan 'event_name' sesuai dengan struktur Firestore
        eventMap.put("date_range", event.getDateRange());  // Gunakan 'date_range' sesuai dengan struktur Firestore

        // Gunakan 'no' sebagai ID dokumen
        eventsRef.document(event.getNo())  // Menggunakan 'no' sebagai ID dokumen
                .set(eventMap)  // Simpan data menggunakan Map
                .addOnSuccessListener(aVoid -> {
                    loadEvents();
                    Toast.makeText(AdminAcademicCalendarActivity.this, "Event added", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(AdminAcademicCalendarActivity.this, "Error adding event", Toast.LENGTH_SHORT).show());
    }

    private void updateEventInDatabase(AcademicEvent event) {
        // Buat Map untuk menyimpan data dengan field sesuai dengan struktur Firestore
        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("no", event.getNo());
        eventMap.put("event_name", event.getEventName());  // Gunakan 'event_name' sesuai dengan struktur Firestore
        eventMap.put("date_range", event.getDateRange());  // Gunakan 'date_range' sesuai dengan struktur Firestore

        eventsRef.document(event.getNo())  // Menggunakan 'no' sebagai ID dokumen
                .set(eventMap)  // Simpan data menggunakan Map
                .addOnSuccessListener(aVoid -> {
                    loadEvents();
                    Toast.makeText(AdminAcademicCalendarActivity.this, "Event updated", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(AdminAcademicCalendarActivity.this, "Error updating event", Toast.LENGTH_SHORT).show());
    }


    private void showEditDialog(AcademicEvent event) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_academic_event_admin, null);

        final EditText noEditText = view.findViewById(R.id.noEditTextAdmin);
        final EditText eventNameEditText = view.findViewById(R.id.eventNameEditTextAdmin);
        final EditText dateRangeEditText = view.findViewById(R.id.dateRangeEditTextAdmin);

        // Populate the existing data for editing
        noEditText.setText(event.getNo());
        eventNameEditText.setText(event.getEventName());
        dateRangeEditText.setText(event.getDateRange());

        builder.setView(view)
                .setTitle("Edit Event")
                .setPositiveButton("Save", (dialog, which) -> {
                    String no = noEditText.getText().toString();
                    String eventName = eventNameEditText.getText().toString();
                    String dateRange = dateRangeEditText.getText().toString();

                    if (no.isEmpty() || eventName.isEmpty() || dateRange.isEmpty()) {
                        Toast.makeText(AdminAcademicCalendarActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    event.setNo(no);
                    event.setEventName(eventName);
                    event.setDateRange(dateRange);

                    updateEventInDatabase(event);
                })
                .setNegativeButton("Cancel", null);

        builder.show();
    }

    private void deleteEvent(String eventNo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Apakah anda yakin ingin Menghapus?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            eventsRef.document(eventNo).delete()
                    .addOnSuccessListener(aVoid -> {
                        loadEvents();
                        Toast.makeText(AdminAcademicCalendarActivity.this, "Event deleted", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(AdminAcademicCalendarActivity.this, "Error deleting event", Toast.LENGTH_SHORT).show());
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }
}
