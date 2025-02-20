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
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ProfileMahasiswaActivity extends AppCompatActivity {

    FirebaseFirestore db;
    DocumentReference docRef;

    // Mendeklarasikan elemen-elemen UI yang akan digunakan
    TextView jurusanTextView, kampusTextView, nimTextView, namaTextView, emailTextView, noTelpTextView, alamatTextView;
    ImageView profileImageView, editEmailIcon, editNoTelpIcon, editAlamatIcon;
    EditText emailEditText, noTelpEditText, AlamatEditText;
    Button editButton;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_mahasiswa);

        // Inisialisasi Firestore untuk mengambil data pengguna
        db = FirebaseFirestore.getInstance();

        // Ambil nimOrNip dari SharedPreferences untuk mendapatkan identitas pengguna
        String nimOrNip = getSharedPreferences("MiDClassPrefs", MODE_PRIVATE)
                .getString("nimOrNip", null);

        // Cek apakah nimOrNip ada, jika tidak ada tampilkan pesan dan keluar dari aktivitas
        if (nimOrNip == null || nimOrNip.isEmpty()) {
            Toast.makeText(ProfileMahasiswaActivity.this, "User tidak terdaftar", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Mendapatkan referensi dokumen berdasarkan nimOrNip
        docRef = db.collection("users").document(nimOrNip);

        // Menghubungkan elemen-elemen UI dengan ID yang sesuai
        jurusanTextView = findViewById(R.id.jurusanTextView);
        kampusTextView = findViewById(R.id.kampusTextView);
        nimTextView = findViewById(R.id.nimTextView);
        namaTextView = findViewById(R.id.namaTextView);
        emailTextView = findViewById(R.id.emailTextView);
        noTelpTextView = findViewById(R.id.noTelpTextView);
        alamatTextView = findViewById(R.id.alamatTextView);
        profileImageView = findViewById(R.id.profileImageView);
        editEmailIcon = findViewById(R.id.editEmailIcon);
        editNoTelpIcon = findViewById(R.id.editNoTelpIcon);
        editAlamatIcon = findViewById(R.id.editAlamatIcon);
        emailEditText = findViewById(R.id.emailEditText);
        noTelpEditText = findViewById(R.id.noTelpEditText);
        AlamatEditText = findViewById(R.id.alamatEditText);
        editButton = findViewById(R.id.editButton);

        // Sembunyikan input untuk edit dan tombol save pada awalnya
        emailEditText.setVisibility(View.GONE);
        noTelpEditText.setVisibility(View.GONE);
        AlamatEditText.setVisibility(View.GONE);
        editButton.setVisibility(View.GONE);

        // Ambil data pengguna dari Firestore dan tampilkan di UI
        getUserData();

        // Listener untuk mengedit email, noTelp, dan alamat
        editEmailIcon.setOnClickListener(v -> showEditText(emailEditText, emailTextView.getText().toString(), emailEditText));
        editNoTelpIcon.setOnClickListener(v -> showEditText(noTelpEditText, noTelpTextView.getText().toString(), noTelpEditText));
        editAlamatIcon.setOnClickListener(v -> showEditText(AlamatEditText, alamatTextView.getText().toString(), AlamatEditText));

        // Listener untuk menyimpan perubahan yang sudah diedit
        editButton.setOnClickListener(v -> updateUserData());

        // Listener untuk mengubah foto profil
        profileImageView.setOnClickListener(v -> openImageChooser());
    }

    private void getUserData() {
        // Mengambil data pengguna dari Firestore berdasarkan nimOrNip
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Ambil data yang ada di Firestore
                    String jurusan = document.getString("jurusan");
                    String email = document.getString("email");
                    String noTelp = document.getString("noTelp");
                    String alamat = document.getString("alamat");
                    String kampus = document.getString("kampus");
                    String nimOrNip = document.getString("nimOrNip");
                    String username = document.getString("username");
                    String base64Image = document.getString("profile_picture");

                    // Menampilkan data yang diambil di UI
                    jurusanTextView.setText(jurusan);
                    emailTextView.setText(email);
                    noTelpTextView.setText(noTelp);
                    alamatTextView.setText(alamat);
                    kampusTextView.setText(kampus);
                    nimTextView.setText(nimOrNip);
                    namaTextView.setText(username);

                    // Menampilkan foto profil jika ada
                    if (base64Image != null && !base64Image.isEmpty()) {
                        try {
                            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            profileImageView.setImageBitmap(decodedByte);
                        } catch (Exception e) {
                            // Jika terjadi error saat decoding gambar
                        }
                    }

                    // Jika data tidak ada, tampilkan pesan default
                    namaTextView.setText(username != null ? username : "Nama tidak ditemukan");
                    emailTextView.setText(email != null ? email : "E-mail tidak ditemukan");
                    noTelpTextView.setText(noTelp != null ? noTelp : "Nomor Telp tidak ditemukan");
                    alamatTextView.setText(alamat != null ? alamat : "Alamat tidak ditemukan");

                }
            } else {
                // Jika gagal mengambil data
            }
        });
    }

    private void showEditText(EditText editText, String currentValue, EditText targetEditText) {
        // Menampilkan EditText dan menyembunyikan TextView untuk mengedit data
        targetEditText.setVisibility(View.VISIBLE);
        targetEditText.setText(currentValue);
        editButton.setVisibility(View.VISIBLE);

        if (editText == emailEditText) {
            findViewById(R.id.emailTextView).setVisibility(View.GONE);
        } else if (editText == noTelpEditText) {
            findViewById(R.id.noTelpTextView).setVisibility(View.GONE);
        } else if (editText == AlamatEditText) {
            findViewById(R.id.alamatTextView).setVisibility(View.GONE);
        }
    }

    private void updateUserData() {
        // Ambil data yang sudah diedit untuk disimpan kembali ke Firestore
        String newEmail = emailEditText.getVisibility() == View.VISIBLE
                ? emailEditText.getText().toString()
                : emailTextView.getText().toString();

        String newNoTelp = noTelpEditText.getVisibility() == View.VISIBLE
                ? noTelpEditText.getText().toString()
                : noTelpTextView.getText().toString();

        String newAlamat = AlamatEditText.getVisibility() == View.VISIBLE
                ? AlamatEditText.getText().toString()
                : alamatTextView.getText().toString();

        // Membuat Map untuk menyimpan perubahan yang akan disimpan ke Firestore
        Map<String, Object> updates = new HashMap<>();
        updates.put("email", newEmail);
        updates.put("noTelp", newNoTelp);
        updates.put("alamat", newAlamat);

        // Mengupdate data di Firestore
        docRef.update(updates).addOnSuccessListener(aVoid -> {
            // Jika berhasil memperbarui data, tampilkan pesan dan sembunyikan input
            Toast.makeText(ProfileMahasiswaActivity.this, "Data berhasil diperbarui", Toast.LENGTH_SHORT).show();
            emailTextView.setText(newEmail);
            noTelpTextView.setText(newNoTelp);
            alamatTextView.setText(newAlamat);
            hideEditFields();
        }).addOnFailureListener(e -> {
            // Jika gagal memperbarui data, tampilkan pesan
            Toast.makeText(ProfileMahasiswaActivity.this, "Gagal memperbarui data", Toast.LENGTH_SHORT).show();
        });
    }

    private void hideEditFields() {
        // Menyembunyikan input dan tombol simpan setelah data diperbarui
        emailEditText.setVisibility(View.GONE);
        noTelpEditText.setVisibility(View.GONE);
        AlamatEditText.setVisibility(View.GONE);
        editButton.setVisibility(View.GONE);
        findViewById(R.id.emailTextView).setVisibility(View.VISIBLE);
        findViewById(R.id.noTelpTextView).setVisibility(View.VISIBLE);
        findViewById(R.id.alamatTextView).setVisibility(View.VISIBLE);
    }

    private void openImageChooser() {
        // Membuka galeri untuk memilih gambar
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Menangani gambar yang dipilih
            imageUri = data.getData();
            try {
                // Mengonversi gambar menjadi bitmap dan menampilkannya di ImageView
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                profileImageView.setImageBitmap(bitmap);

                // Mengubah gambar ke format Base64 dan mengupdate foto profil
                updateProfileImage(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateProfileImage(Bitmap bitmap) {
        // Mengonversi gambar ke format Base64
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

        // Menyimpan gambar profil baru ke Firestore
        Map<String, Object> updates = new HashMap<>();
        updates.put("profile_picture", encodedImage);
        docRef.update(updates).addOnSuccessListener(aVoid -> {
            Toast.makeText(ProfileMahasiswaActivity.this, "Foto profil berhasil diperbarui", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(ProfileMahasiswaActivity.this, "Gagal memperbarui foto profil", Toast.LENGTH_SHORT).show();
        });
    }
}
