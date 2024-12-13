package com.example.myapplication;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class files extends AppCompatActivity {
    private static final int OPEN_DOCUMENT_REQUEST_CODE = 123;
    private static final String TAG = "FilesActivity";

    private Button openFileButton;
    private Button submitButton;
    private Uri selectedFileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files);

        openFileButton = findViewById(R.id.buttonAddFiles);
        submitButton = findViewById(R.id.buttonSubmit);

        openFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilePicker();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedFileUri != null) {
                    uploadFile(selectedFileUri);
                } else {
                    Toast.makeText(files.this, "Please select a file first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Request permissions on activity creation
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        }
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*"); // specify the type of files you want to open here
        startActivityForResult(intent, OPEN_DOCUMENT_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OPEN_DOCUMENT_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            selectedFileUri = data.getData();
            if (selectedFileUri != null) {
                submitButton.setEnabled(true);
            }
        }
    }


    private void uploadFile(Uri fileUri) {
        OkHttpClient client = new OkHttpClient();
        ContentResolver contentResolver = getContentResolver();

        // Get the file name and MIME type
        String fileName = getFileNameFromUri(fileUri, contentResolver);
        String mimeType = contentResolver.getType(fileUri);  // Get the MIME type dynamically

        // Default to application/octet-stream if the MIME type is unknown
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }

        byte[] fileBytes = getFileBytesFromUri(fileUri, contentResolver); // Get the file bytes as binary

        if (fileName == null || fileBytes == null) {
            Toast.makeText(this, "Failed to get file data", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a RequestBody with the binary file content
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", fileName,
                        RequestBody.create(MediaType.parse(mimeType), fileBytes)) // Use correct MIME type
                .build();

        Request request = new Request.Builder()
                .url("http://172.24.18.12:8000/api/upload-file/")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(files.this, "Error uploading file", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        try {
                            String responseBody = response.body().string();

                            // Start the new activity and pass the JSON data
                            Intent intent = new Intent(files.this, TableActivity.class);
                            intent.putExtra("jsonData", responseBody);
                            startActivity(intent);

                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(files.this, "Failed to read server response", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(files.this, "Failed to upload file", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });
    }



    private String getFileNameFromUri(Uri uri, ContentResolver contentResolver) {
        String fileName = null;
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                fileName = cursor.getString(nameIndex);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return fileName;
    }

//    private String getFileBytesFromUri(Uri uri, ContentResolver contentResolver) {
//        try {
//            InputStream inputStream = contentResolver.openInputStream(uri);
//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//            byte[] buffer = new byte[1024];
//            int bytesRead;
//            while ((bytesRead = inputStream.read(buffer)) != -1) {
//                byteArrayOutputStream.write(buffer, 0, bytesRead);
//            }
//            System.out.print(byteArrayOutputStream);
//            return byteArrayOutputStream;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    private byte[] getFileBytesFromUri(Uri uri, ContentResolver contentResolver) {
        try {
            InputStream inputStream = contentResolver.openInputStream(uri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            return byteArrayOutputStream.toByteArray(); // Return the byte array instead of converting to string
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}
