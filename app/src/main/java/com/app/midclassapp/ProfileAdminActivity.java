package com.app.midclassapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProfileAdminActivity extends AppCompatActivity {

    FirebaseFirestore db;
    DocumentReference docRef;

    TextView adminIdTextView, namaAdminTextView, roleTextView, emailAdminTextView, noTelpAdminTextView, alamatAdminTextView;
    ImageView profileImageView, editEmailIcon, editNoTelpIcon, editAlamatIcon;
    EditText emailAdminEditText, noTelpAdminEditText, alamatAdminEditText;
    Button editButton;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_admin);

        db = FirebaseFirestore.getInstance();

        String nimOrNip = getSharedPreferences("MiDClassPrefs", MODE_PRIVATE)
                .getString("nimOrNip", null);

        if (nimOrNip == null || nimOrNip.isEmpty()) {
            Toast.makeText(ProfileAdminActivity.this, "User tidak terdaftar", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        docRef = db.collection("users").document(nimOrNip);

        // Inisialisasi tampilan (views)
        adminIdTextView = findViewById(R.id.adminIdTextView);
        namaAdminTextView = findViewById(R.id.namaAdminTextView);
        roleTextView = findViewById(R.id.roleTextView);
        emailAdminTextView = findViewById(R.id.emailAdminTextView);
        noTelpAdminTextView = findViewById(R.id.noTelpAdminTextView);
        alamatAdminTextView = findViewById(R.id.alamatAdminTextView);
        profileImageView = findViewById(R.id.profileImageView);
        editEmailIcon = findViewById(R.id.editEmailIcon);
        editNoTelpIcon = findViewById(R.id.editNoTelpIcon);
        editAlamatIcon = findViewById(R.id.editAlamatIcon);
        emailAdminEditText = findViewById(R.id.emailAdminEditText);
        noTelpAdminEditText = findViewById(R.id.noTelpAdminEditText);
        alamatAdminEditText = findViewById(R.id.alamatAdminEditText);
        editButton = findViewById(R.id.editButton);

        emailAdminEditText.setVisibility(View.GONE);
        noTelpAdminEditText.setVisibility(View.GONE);
        alamatAdminEditText.setVisibility(View.GONE);
        editButton.setVisibility(View.GONE);

        getUserData();

        editEmailIcon.setOnClickListener(v -> showEditText(emailAdminEditText, emailAdminTextView.getText().toString(), emailAdminTextView));
        editNoTelpIcon.setOnClickListener(v -> showEditText(noTelpAdminEditText, noTelpAdminTextView.getText().toString(), noTelpAdminTextView));
        editAlamatIcon.setOnClickListener(v -> showEditText(alamatAdminEditText, alamatAdminTextView.getText().toString(), alamatAdminTextView));

        editButton.setOnClickListener(v -> updateUserData());

        profileImageView.setOnClickListener(v -> openImageChooser());
    }

    private void getUserData() {
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String email = document.getString("email");
                    String role = document.getString("jurusan");
                    String noTelp = document.getString("noTelp");
                    String alamat = document.getString("alamat");
                    String adminId = document.getString("nimOrNip");
                    String username = document.getString("username");
                    String base64Image = document.getString("profile_picture");

                    emailAdminTextView.setText(email);
                    noTelpAdminTextView.setText(noTelp);
                    roleTextView.setText(role);
                    alamatAdminTextView.setText(alamat);
                    adminIdTextView.setText(adminId);
                    namaAdminTextView.setText(username);

                    if (base64Image != null && !base64Image.isEmpty()) {
                        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        profileImageView.setImageBitmap(decodedByte);
                    }
                } else {
                    Toast.makeText(ProfileAdminActivity.this, "Data admin tidak ditemukan", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {
                Toast.makeText(ProfileAdminActivity.this, "Gagal mengambil data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEditText(EditText editText, String currentValue, TextView textView) {
        editText.setVisibility(View.VISIBLE);
        editText.setText(currentValue);
        editButton.setVisibility(View.VISIBLE);
        textView.setVisibility(View.GONE);
    }

    private void updateUserData() {
        String newEmail = emailAdminEditText.getVisibility() == View.VISIBLE ? emailAdminEditText.getText().toString() : emailAdminTextView.getText().toString();
        String newNoTelp = noTelpAdminEditText.getVisibility() == View.VISIBLE ? noTelpAdminEditText.getText().toString() : noTelpAdminTextView.getText().toString();
        String newAlamat = alamatAdminEditText.getVisibility() == View.VISIBLE ? alamatAdminEditText.getText().toString() : alamatAdminTextView.getText().toString();

        Map<String, Object> updates = new HashMap<>();
        updates.put("email", newEmail);
        updates.put("noTelp", newNoTelp);
        updates.put("alamat", newAlamat);

        docRef.update(updates).addOnSuccessListener(aVoid -> {
            Toast.makeText(ProfileAdminActivity.this, "Data berhasil diperbarui", Toast.LENGTH_SHORT).show();
            emailAdminTextView.setText(newEmail);
            noTelpAdminTextView.setText(newNoTelp);
            alamatAdminTextView.setText(newAlamat);
            hideEditFields();
        }).addOnFailureListener(e -> Toast.makeText(ProfileAdminActivity.this, "Gagal memperbarui data", Toast.LENGTH_SHORT).show());
    }

    private void hideEditFields() {
        emailAdminEditText.setVisibility(View.GONE);
        noTelpAdminEditText.setVisibility(View.GONE);
        alamatAdminEditText.setVisibility(View.GONE);
        editButton.setVisibility(View.GONE);
        emailAdminTextView.setVisibility(View.VISIBLE);
        noTelpAdminTextView.setVisibility(View.VISIBLE);
        alamatAdminTextView.setVisibility(View.VISIBLE);
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                profileImageView.setImageBitmap(bitmap);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

                docRef.update("profile_picture", encodedImage).addOnSuccessListener(aVoid ->
                        Toast.makeText(ProfileAdminActivity.this, "Gambar profil berhasil diperbarui", Toast.LENGTH_SHORT).show()
                ).addOnFailureListener(e ->
                        Toast.makeText(ProfileAdminActivity.this, "Gagal memperbarui gambar profil", Toast.LENGTH_SHORT).show()
                );

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
