package com.example.myapplication;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.speech.RecognizerIntent;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

//import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
//import org.docx4j.convert.in.xhtml.XhtmlImporter;

public class MainActivity extends AppCompatActivity {
    static final int REQUEST_CODE_SPEECH_INPUT = 100;
    private Button move;
    private Button submitButton;
    private Uri selectedFileUri;
    private TextView successMessageTextView;
    private ProgressBar progressBar;
    private MediaPlayer mediaPlayer;
    private SeekBar audioSeekBar;
    private Button playPauseButton;
    private Uri audioUri;
    private boolean isPlaying = false;
    private SwitchCompat buttonCmoveSwitch;
    private String imageType = "pill";

    private static final int OPEN_DOCUMENT_REQUEST_CODE = 123;
    private static final int CAMERA_CAPTURE_REQUEST_CODE = 124;
    private static final int SUBMIT_REQUEST_CODE = 125;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        move = findViewById(R.id.buttonCmove);

        move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });

        move = findViewById(R.id.buttonFile);
        move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilePicker();
            }
        });

        move = findViewById(R.id.buttonCAudio);
        move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVoiceRecorder();
            }
        });
//        Button submitButton = findViewById(R.id.buttonSubmit);
        progressBar = findViewById(R.id.progressBar);
        successMessageTextView = findViewById(R.id.successMessageTextView);
        submitButton = findViewById(R.id.buttonSubmit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedFileUri != null) {
                    uploadFile(selectedFileUri);
                } else {
                    Toast.makeText(MainActivity.this, "Please select a file first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set the listener for the toggle switch
        buttonCmoveSwitch = findViewById(R.id.buttonCmoveSwitch);
        buttonCmoveSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                imageType = "pillbox"; // Set image type to pillbox
            } else {
                imageType = "pill"; // Set image type to pill
            }
            Log.d(TAG, "Current image type: " + imageType); // Log for debugging
        });

        successMessageTextView = findViewById(R.id.successMessageTextView);
    }

    private void startVoiceRecorder() {
        Intent intent = null;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // Request the RECORD_AUDIO permission if not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_CODE_SPEECH_INPUT);
        } else {
            // Start the speech recognition activity
            intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        }
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, CAMERA_CAPTURE_REQUEST_CODE);
        } else {
            Toast.makeText(this, "Camera app not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImageFile(byte[] imageBytes) {
//        OkHttpClient client = new OkHttpClient();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.SECONDS)  // Increase connection timeout
                .readTimeout(120, TimeUnit.SECONDS)     // Increase read timeout
                .writeTimeout(120, TimeUnit.SECONDS)    // Increase write timeout
                .build();

        // Determine the image file type (jpg, jpeg, png, etc.)
        String fileType = getFileType(imageBytes);
        Log.d(TAG, "Image byte size: " + imageBytes.length);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", "captured_image." + fileType,
                        RequestBody.create(MediaType.parse("image/" + fileType), imageBytes))
                .build();
        successMessageTextView.setText("Image Uploaded Successfully.");
        successMessageTextView.setVisibility(View.VISIBLE);

        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE); // Show the progress bar

        Request request = new Request.Builder()
                .url("http://172.24.18.12:8000/api/upload-file/")
                .post(requestBody)
                .addHeader("Image-Type", imageType)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Error uploading file", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        try {
                            String responseBody = response.body().string();

                            // Determine the activity to start based on the image type
                            Class<?> targetActivity;
                            if ("pill".equals(imageType)) {
                                targetActivity = TableActivityPill.class; // For Pill
                            } else {
                                targetActivity = TableActivityPillBox.class; // For PillBox
                            }

                            // Delay showing the result to give the user time to see the success message
                            successMessageTextView.postDelayed(() -> {
                                // Hide the success message after starting the new activity
                                successMessageTextView.setVisibility(View.GONE);

                                // Start the new activity and pass the JSON data
                                Intent intent = new Intent(MainActivity.this,targetActivity);
                                intent.putExtra("jsonData", responseBody);
                                intent.putExtra("imageData", imageBytes); // Pass the image data
                                startActivity(intent);
                            }, 1000);
                            progressBar.setVisibility(View.GONE); // Adjust the delay time (in milliseconds) as needed
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Failed to read server response", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    // Helper method to determine the image file type
    private String getFileType(byte[] imageBytes) {
        // Use a library like Apache Commons IO or Java 7's built-in method to determine the file type
        // For simplicity, I'll use a basic approach to detect common image file types
        if (imageBytes[0] == (byte) 0xFF && imageBytes[1] == (byte) 0xD8) {
            return "jpg";
        } else if (imageBytes[0] == (byte) 0x89 && imageBytes[1] == (byte) 0x50) {
            return "png";
        } else if (imageBytes[0] == (byte) 0xFF && imageBytes[1] == (byte) 0xE0) {
            return "jpeg";
        } else {
            return "unknown";
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_CAPTURE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            if (extras != null && extras.containsKey("data")) {
                Bitmap capturedImageBitmap = (Bitmap) extras.get("data");
                capturedImageBitmap = rotateBitmap(capturedImageBitmap, 90);

                // Convert the bitmap to a byte array
                byte[] imageBytes = bitmapToByteArray(capturedImageBitmap);

                // Upload the image bytes directly to the backend
                if (imageBytes != null) {
                    uploadImageFile(imageBytes);
                } else {
                    Toast.makeText(this, "Failed to process image", Toast.LENGTH_SHORT).show();
                }
//                // Pass the image bytes to the next activity
//                Intent intent = new Intent(MainActivity.this, TableActivityPill.class);
//                intent.putExtra("imageData", imageBytes);
//                // Pass other data as needed
//                startActivity(intent);

            }
        } else if (requestCode == OPEN_DOCUMENT_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            selectedFileUri = data.getData();
            if (submitButton != null && selectedFileUri != null) {
                submitButton.setEnabled(true); // Enable the submit button
                successMessageTextView.setText("File Selected Successfully. Click on Submit.");
                successMessageTextView.setVisibility(View.VISIBLE);

                convertDocxToTxt(selectedFileUri);
            }
        } else if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            // Get the recognized speech
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && result.size()> 0) {


                // Show the recognized text in an AlertDialog
              showTextInDialog(result.get(0));
//                audioUri = data.getData();
//                setupAudioPreview(audioUri);
//                switchToAudioLayout();
            } else {
                Toast.makeText(this, "Failed to capture audio.", Toast.LENGTH_SHORT).show();
            }

        }
    }
//    private void switchToAudioLayout() {
//        setContentView(R.layout.audio_preview);
//
//        // Access the audio preview components
//        LinearLayout audioPreviewContainer = findViewById(R.id.audioPreviewContainer);
//        playPauseButton = findViewById(R.id.playPauseButton);
//        audioSeekBar = findViewById(R.id.audioSeekBar);
//
//        // Setup the audio preview
//        if (audioUri != null) {
//            setupAudioPreview(audioUri);
//        }
//    }
//    private void setupAudioPreview(Uri audioUri) {
//        // Ensure the correct layout is active
//        LinearLayout audioPreviewContainer = findViewById(R.id.audioPreviewContainer);
//        playPauseButton = findViewById(R.id.playPauseButton);
//        audioSeekBar = findViewById(R.id.audioSeekBar);
//
//        if (audioUri != null && audioPreviewContainer != null) {
//            audioPreviewContainer.setVisibility(View.VISIBLE);
//
//            mediaPlayer = MediaPlayer.create(this, audioUri);
//            mediaPlayer.setOnPreparedListener(mp -> {
//                audioSeekBar.setMax(mp.getDuration());
//                playPauseButton.setOnClickListener(v -> toggleAudioPlayback());
//            });
//
//            mediaPlayer.setOnCompletionListener(mp -> {
//                isPlaying = false;
//                playPauseButton.setText("Play");
//            });
//
//            // Update SeekBar progress
//            audioSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//                @Override
//                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                    if (fromUser) {
//                        mediaPlayer.seekTo(progress);
//                    }
//                }
//
//                @Override
//                public void onStartTrackingTouch(SeekBar seekBar) {}
//
//                @Override
//                public void onStopTrackingTouch(SeekBar seekBar) {}
//            });
//
//            new Thread(() -> {
//                while (mediaPlayer != null) {
//                    try {
//                        if (mediaPlayer.isPlaying()) {
//                            audioSeekBar.setProgress(mediaPlayer.getCurrentPosition());
//                        }
//                        Thread.sleep(100);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }).start();
//        } else {
//            Log.e(TAG, "audioPreviewContainer or audioUri is null.");
//        }
//    }


    private void toggleAudioPlayback() {
        if (mediaPlayer != null) {
            if (isPlaying) {
                mediaPlayer.pause();
                playPauseButton.setText("Play");
            } else {
                mediaPlayer.start();
                playPauseButton.setText("Pause");
            }
            isPlaying = !isPlaying;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }



    private void convertDocxToTxt(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            ZipInputStream zipInputStream = new ZipInputStream(inputStream);
            ZipEntry zipEntry;
            StringBuilder textContent = new StringBuilder();

            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if (zipEntry.getName().equals("word/document.xml")) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(zipInputStream));
                    String line;
                    boolean isParagraph = false; // To track if we are in a paragraph

                    while ((line = reader.readLine()) != null) {
                        // Check for paragraph start
                        if (line.contains("<w:p")) {
                            if (isParagraph) {
                                textContent.append("\n"); // Add a new line before a new paragraph
                            }
                            isParagraph = true; // We are now in a paragraph
                        }

                        // Replace XML tags with an empty string, preserving line breaks
                        line = line.replaceAll("<[^>]+>", ""); // Remove XML tags
                        line = line.replaceAll("&lt;", "<").replaceAll("&gt;", ">"); // Decode HTML entities

                        // Add line to content, preserving line breaks
                        textContent.append(line).append("\n");

                        // Check for line breaks within the paragraph
                        if (line.contains("<w:br")) {
                            textContent.append("\n"); // Add a new line for line breaks
                        }
                    }
                    break;
                }
            }

            zipInputStream.close();
            writeTextToFile(textContent.toString());
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error converting file", Toast.LENGTH_SHORT).show();
        }
    }

    private void writeTextToFile(String text) {
        File file = new File(getExternalFilesDir(null), "converted_text.txt");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(text.getBytes());
            fos.flush();
            Toast.makeText(this, "File converted and saved as converted_text.txt", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving text file", Toast.LENGTH_SHORT).show();
        }
    }

    private void showTextInDialog(String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Text");

        // Create EditText field for text input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE );
        input.setText(text); // Set the recognized text in the EditText

        // Set the EditText as the view for the AlertDialog
        builder.setView(input);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = input.getText().toString();

                // Create a text file with the recognized text
                File file = new File(getExternalFilesDir(null), "recognized_text.txt");
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    fileOutputStream.write(text.getBytes());
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Upload the text file
                Uri fileUri = Uri.fromFile(file);
                uploadTextFile(fileUri, text);

                // Display the success message
                successMessageTextView.setText("Text Uploaded Successfully.");
                successMessageTextView.setVisibility(View.VISIBLE);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void uploadFile(Uri fileUri) {
        if (fileUri == null) {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentResolver contentResolver = getContentResolver();
        String fileName = getFileNameFromUri(fileUri, contentResolver);

        if (fileName == null) {
            Toast.makeText(this, "Failed to get file name", Toast.LENGTH_SHORT).show();
            return;
        }

        String mimeType = contentResolver.getType(fileUri);
        byte[] fileBytes = getFileBytesFromUri(fileUri, contentResolver);

        if (fileBytes == null) {
            Toast.makeText(this, "Failed to read file data", Toast.LENGTH_SHORT).show();
            return;
        }

        // Determine the file type
        boolean isDocx = fileName.endsWith(".docx");
        boolean isPdf = fileName.endsWith(".pdf");

        if (!isDocx && !isPdf) {
            Toast.makeText(this, "Unsupported file type", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // Prepare the request body
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", fileName, RequestBody.create(MediaType.parse(mimeType), fileBytes))
                .build();

        // Send the file to the backend
//        OkHttpClient client = new OkHttpClient();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)  // Increase connection timeout
                .writeTimeout(120, TimeUnit.SECONDS)   // Increase write timeout
                .readTimeout(120, TimeUnit.SECONDS)    // Increase read timeout
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
                    if (e instanceof java.net.SocketTimeoutException) {
                        // Specific handling for timeout exception
                        Toast.makeText(MainActivity.this, "Network timeout. Please try again.", Toast.LENGTH_SHORT).show();
                    } else if (e instanceof java.net.UnknownHostException) {
                        // Specific handling for no internet connection
                        Toast.makeText(MainActivity.this, "No internet connection. Check your network settings.", Toast.LENGTH_SHORT).show();
                    } else {
                        // Generic error handling
                        Toast.makeText(MainActivity.this, "Error uploading file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(View.GONE);
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        try {
                            String responseBody = response.body().string();

                            // Start the appropriate activity
                            Intent intent;
                            if (isDocx) {
                                String inputData = readInputFileContent(fileUri); // Read converted `.docx` text
                                intent = new Intent(MainActivity.this, TableActivity.class);
                                intent.putExtra("inputData", inputData);
                            } else { // PDF
                                intent = new Intent(MainActivity.this, PDFTableActivity.class);
                                intent.putExtra("pdfFileUri", fileUri);
                            }

                            intent.putExtra("jsonData", responseBody); // Pass the JSON data
                            startActivity(intent);

                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Failed to parse server response", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Failed to upload file", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

//    private String readInputFileContent(Uri fileUri) {
//        StringBuilder content = new StringBuilder();
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getContentResolver().openInputStream(fileUri)))) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                content.append(line).append("\n");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return content.toString().trim(); // Return the content as a String
//    }

    private String readInputFileContent(Uri fileUri) {
        StringBuilder content = new StringBuilder();
        File file = new File(getExternalFilesDir(null), "converted_text.txt");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error reading input file", Toast.LENGTH_SHORT).show();
        }
        return content.toString().trim(); // Return the content as a String
    }

    private void uploadTextFile(Uri fileUri, String text) {
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", "recognized_text.txt",
                        RequestBody.create(MediaType.parse("text/plain"), getFileBytesFromUri(fileUri, getContentResolver())))
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
                    Toast.makeText(MainActivity.this, "Error uploading text file", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        try {
                            String responseBody = response.body().string();

                            // Start the new activity and pass the JSON data
                            Intent intent = new Intent(MainActivity.this, TableActivity.class);
                            intent.putExtra("jsonData", responseBody);
                            intent.putExtra("editedText", text); // Pass the text to TableActivity
                            startActivity(intent);

                            successMessageTextView.setVisibility(View.GONE); // Hide the success message after starting the new activity
                            progressBar.setVisibility(View.GONE);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Failed to read server response", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Failed to upload text file", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
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

    private byte[] getFileBytesFromUri(Uri uri, ContentResolver contentResolver) {
        try {
            InputStream inputStream = contentResolver.openInputStream(uri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, OPEN_DOCUMENT_REQUEST_CODE);
    }

    private byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
}
