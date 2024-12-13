package com.example.myapplication;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.MultipartBody;
import okhttp3.MediaType;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.io.ByteArrayOutputStream;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class CameraEntity extends AppCompatActivity {

    private static final int CAMERA_CAPTURE_REQUEST_CODE = 124;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_camera_entity);
    }

    public void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.action_camera) {
                    openCamera();
                    return true;
                } else {
                    return false;
                }
            }
        });

        popupMenu.inflate(R.menu.popup_menu);
        popupMenu.show();
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, CAMERA_CAPTURE_REQUEST_CODE);
        } else {
            Toast.makeText(this, "Camera app not found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_CAPTURE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            if (extras != null && extras.containsKey("data")) {
                Bitmap capturedImageBitmap = (Bitmap) extras.get("data");
                capturedImageBitmap = rotateBitmap(capturedImageBitmap, 90); // Rotate bitmap if necessary

                // Convert the bitmap to a byte array
                byte[] imageBytes = bitmapToByteArray(capturedImageBitmap);

                // Upload the image bytes directly to the backend
                if (imageBytes != null) {
                    uploadFile(imageBytes);
                } else {
                    Toast.makeText(this, "Failed to process image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void uploadFile(byte[] imageBytes) {
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", "captured_image.jpg",
                        RequestBody.create(MediaType.parse("image/jpeg"), imageBytes))
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
                    Toast.makeText(CameraEntity.this, "Error uploading file", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        try {
                            String responseBody = response.body().string();

                            // Create an Intent to start the next activity
                            Intent intent = new Intent(CameraEntity.this, NextActivity.class);
                            intent.putExtra("response", responseBody);
                            startActivity(intent);

                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(CameraEntity.this, "Failed to read server response", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(CameraEntity.this, "Failed to upload file", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });
    }

    private Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
}
