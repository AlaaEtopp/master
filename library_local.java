package com.example.projet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class library_local extends AppCompatActivity {

    private Button btnDownload, btnUpload, btnReturnToLibrary;
    private Spinner spinnerCategory;
    private RecyclerView recyclerViewImages;
    private ImageAdapter imageAdapter;
    private List<Uri> imageUris;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.library_local);

        // Initialize imageUris list
        imageUris = new ArrayList<>();

        // Initialize views
        btnDownload = findViewById(R.id.btnDownload);
        btnUpload = findViewById(R.id.btnUpload);
        btnReturnToLibrary = findViewById(R.id.btnReturnToLibrary); // Find the return button
        spinnerCategory = findViewById(R.id.spinnerCategory);
        recyclerViewImages = findViewById(R.id.recyclerViewImages);

        // Initialize RecyclerView
        recyclerViewImages.setLayoutManager(new LinearLayoutManager(this));
        imageAdapter = new ImageAdapter(this, imageUris);
        recyclerViewImages.setAdapter(imageAdapter);

        // Populate spinner with categories
        List<String> categories = new ArrayList<>();
        categories.add("Nature");
        categories.add("Object");
        categories.add("Food");
        categories.add("Others");
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(spinnerAdapter);

        // Set click listeners
        btnDownload.setOnClickListener(v -> downloadSelectedImage());
        btnUpload.setOnClickListener(v -> selectImageFromGallery());

        // Set click listener for returning to Library
        btnReturnToLibrary.setOnClickListener(v -> finish()); // Finish the activity to return to Library
    }


    private void downloadSelectedImage() {
        int selectedPosition = imageAdapter.getSelectedPosition();
        Log.d("Download", "Selected position: " + selectedPosition); // Log selected position
        if (selectedPosition != RecyclerView.NO_POSITION) {
            Uri imageUri = imageUris.get(selectedPosition);
            Log.d("Download", "Image URI: " + imageUri); // Log image URI

            // Download the local image
            downloadLocalImage(imageUri);
        } else {
            Toast.makeText(this, "Please select an image to download", Toast.LENGTH_SHORT).show();
        }
    }

    private void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            imageUris.add(selectedImageUri);
            imageAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Selected Image: " + selectedImageUri.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadLocalImage(Uri imageUri) {
        try {
            // Open an input stream to read from the content URI
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            if (inputStream != null) {
                // Create a destination file in the downloads directory
                File destinationDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "MyAppImages");
                if (!destinationDir.exists()) {
                    if (!destinationDir.mkdirs()) {
                        Log.e("Download", "Failed to create destination directory");
                        return;
                    }
                }

                String displayName = getFileNameFromUri(imageUri); // Get the display name of the file
                File destinationFile = new File(destinationDir, displayName);

                // Log the destination file path
                Log.d("Download", "Destination file path: " + destinationFile.getAbsolutePath());

                // Copy the data from the input stream to the destination file
                copyFile(inputStream, destinationFile);

                // Check if the downloaded file size is greater than zero
                if (destinationFile.length() > 0) {
                    Toast.makeText(this, "File downloaded successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to download file. File size is zero", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("Download", "Failed to open input stream");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Download", "Failed to download file: " + e.getMessage());
            Toast.makeText(this, "Failed to download file. Check logcat for details", Toast.LENGTH_SHORT).show();
        }
    }



    @SuppressLint("Range")
    private String getFileNameFromUri(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        String displayName = "";
        if (cursor != null && cursor.moveToFirst()) {
            displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            cursor.close();
        }
        return displayName;
    }


    private void copyFile(InputStream sourceFile, File destFile) throws IOException {
        try (OutputStream out = new FileOutputStream(destFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = sourceFile.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            Log.d("Download", "File copied successfully");
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Failed to copy file", e);
        } finally {
            sourceFile.close(); // Close the input stream in the finally block
        }
    }





}
