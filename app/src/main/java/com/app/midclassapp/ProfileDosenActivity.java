package com.app.midclassapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
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
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ProfileDosenActivity extends AppCompatActivity {

    FirebaseFirestore db;
    DocumentReference docRef;

    TextView jurusanTextView, kampusTextView, nipTextView, namaDosenTextView, matkulPengampuTextView, emailTextView, noTelpTextView, alamatTextView;
    ImageView profileImageView, editEmailIcon, editNoTelpIcon, editAlamatIcon;
    EditText emailEditText, noTelpEditText, alamatEditText;
    Button editButton;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_dosen);

        db = FirebaseFirestore.getInstance();

        String nimOrNip = getSharedPreferences("MiDClassPrefs", MODE_PRIVATE)
                .getString("nimOrNip", null);

        if (nimOrNip == null || nimOrNip.isEmpty()) {
            Toast.makeText(ProfileDosenActivity.this, "User tidak terdaftar", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        docRef = db.collection("users").document(nimOrNip);

        // Inisialisasi tampilan (views)
        nipTextView = findViewById(R.id.nipTextView);
        namaDosenTextView = findViewById(R.id.namaDosenTextView);
        matkulPengampuTextView = findViewById(R.id.matkulPengampuTextView);
        emailTextView = findViewById(R.id.emailTextView);
        noTelpTextView = findViewById(R.id.noTelpTextView);
        alamatTextView = findViewById(R.id.alamatTextView);
        profileImageView = findViewById(R.id.profileImageView);
        editEmailIcon = findViewById(R.id.editEmailIcon);
        editNoTelpIcon = findViewById(R.id.editNoTelpIcon);
        editAlamatIcon = findViewById(R.id.editAlamatIcon);
        emailEditText = findViewById(R.id.emailEditText);
        noTelpEditText = findViewById(R.id.noTelpEditText);
        alamatEditText = findViewById(R.id.alamatEditText);
        editButton = findViewById(R.id.editButton);

        emailEditText.setVisibility(View.GONE);
        noTelpEditText.setVisibility(View.GONE);
        alamatEditText.setVisibility(View.GONE);
        editButton.setVisibility(View.GONE);

        // Mengambil data dosen
        getUserData();

        // Menambahkan click listeners
        editEmailIcon.setOnClickListener(v -> showEditText(emailEditText, emailTextView.getText().toString(), emailEditText));
        editNoTelpIcon.setOnClickListener(v -> showEditText(noTelpEditText, noTelpTextView.getText().toString(), noTelpEditText));
        editAlamatIcon.setOnClickListener(v -> showEditText(alamatEditText, alamatTextView.getText().toString(), alamatEditText));

        editButton.setOnClickListener(v -> updateUserData());

        // Menambahkan listener untuk mengganti foto profil
        profileImageView.setOnClickListener(v -> openImageChooser());
    }

    private void getUserData() {
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Ambil data dosen dari Firestore
                    String email = document.getString("email");
                    String matkul = document.getString("matkul");
                    String noTelp = document.getString("noTelp");
                    String alamat = document.getString("alamat");
                    String nimOrNip = document.getString("nimOrNip");
                    String username = document.getString("nama");
                    String base64Image = document.getString("profile_picture");

                    // Setel data ke tampilan
                    emailTextView.setText(email);
                    noTelpTextView.setText(noTelp);
                    matkulPengampuTextView.setText(matkul);
                    alamatTextView.setText(alamat);
                    nipTextView.setText(nimOrNip);
                    namaDosenTextView.setText(username);

                    if (base64Image != null && !base64Image.isEmpty()) {
                        try {
                            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inSampleSize = 4;
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length, options);
                            profileImageView.setImageBitmap(decodedByte);
                        } catch (Exception e) {
                            Log.e("ProfileDosenActivity", "Error decoding Base64 image", e);
                        }
                    }

                    // Menampilkan data pada TextView
                    namaDosenTextView.setText(username != null ? username : "Nama tidak ditemukan");
                    emailTextView.setText(email != null ? email : "E-mail tidak ditemukan");
                    noTelpTextView.setText(noTelp != null ? noTelp : "Nomor Telp tidak ditemukan");
                    alamatTextView.setText(alamat != null ? alamat : "Alamat tidak ditemukan");

                } else {
                    Log.d("Firestore", "Dokumen tidak ditemukan!");
                }
            } else {
                Log.d("Firestore", "Gagal dengan: ", task.getException());
            }
        });
    }

    private void showEditText(EditText editText, String currentValue, EditText targetEditText) {
        targetEditText.setVisibility(View.VISIBLE);
        targetEditText.setText(currentValue);
        editButton.setVisibility(View.VISIBLE);

        if (editText == emailEditText) {
            findViewById(R.id.emailTextView).setVisibility(View.GONE);
        } else if (editText == noTelpEditText) {
            findViewById(R.id.noTelpTextView).setVisibility(View.GONE);
        } else if (editText == alamatEditText) {
            findViewById(R.id.alamatTextView).setVisibility(View.GONE);
        }
    }

    private void updateUserData() {
        String newEmail = emailEditText.getVisibility() == View.VISIBLE
                ? emailEditText.getText().toString()
                : emailTextView.getText().toString();

        String newNoTelp = noTelpEditText.getVisibility() == View.VISIBLE
                ? noTelpEditText.getText().toString()
                : noTelpTextView.getText().toString();

        String newAlamat = alamatEditText.getVisibility() == View.VISIBLE
                ? alamatEditText.getText().toString()
                : alamatTextView.getText().toString();

        Map<String, Object> updates = new HashMap<>();
        updates.put("email", newEmail);
        updates.put("noTelp", newNoTelp);
        updates.put("alamat", newAlamat);

        docRef.update(updates).addOnSuccessListener(aVoid -> {
            Toast.makeText(ProfileDosenActivity.this, "Data berhasil diperbarui", Toast.LENGTH_SHORT).show();
            emailTextView.setText(newEmail);
            noTelpTextView.setText(newNoTelp);
            alamatTextView.setText(newAlamat);
            hideEditFields();
        }).addOnFailureListener(e -> {
            Toast.makeText(ProfileDosenActivity.this, "Gagal memperbarui data", Toast.LENGTH_SHORT).show();
        });
    }

    private void hideEditFields() {
        emailEditText.setVisibility(View.GONE);
        noTelpEditText.setVisibility(View.GONE);
        alamatEditText.setVisibility(View.GONE);
        editButton.setVisibility(View.GONE);
        findViewById(R.id.emailTextView).setVisibility(View.VISIBLE);
        findViewById(R.id.noTelpTextView).setVisibility(View.VISIBLE);
        findViewById(R.id.alamatTextView).setVisibility(View.VISIBLE);
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Method untuk menangani hasil pemilihan gambar
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();  // Menyimpan URI gambar yang dipilih

            // Menampilkan gambar yang dipilih di ImageView
            try {
                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                profileImageView.setImageBitmap(selectedImage);

                // Mengonversi gambar ke format Base64 dan mengupdate Firestore
                uploadImageToFirestore(selectedImage);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Method untuk mengupload gambar ke Firestore setelah diubah
    private void uploadImageToFirestore(Bitmap selectedImage) {
        // Mengonversi gambar Bitmap ke Base64
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

        // Update gambar profil di Firestore
        Map<String, Object> updates = new HashMap<>();
        updates.put("profile_picture", encodedImage);

        docRef.update(updates).addOnSuccessListener(aVoid -> {
            Toast.makeText(ProfileDosenActivity.this, "Foto profil berhasil diperbarui", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(ProfileDosenActivity.this, "Gagal memperbarui foto profil", Toast.LENGTH_SHORT).show();
        });
    }
}
