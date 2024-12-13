package com.example.myapplication;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import static com.example.myapplication.MainActivity.REQUEST_CODE_SPEECH_INPUT;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class AudioEntity extends AppCompatActivity {
    private static final int OPEN_DOCUMENT_REQUEST_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_entity);
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.CAMERA},
                PackageManager.PERMISSION_GRANTED);

//        // Find the button
//        Button button3 = findViewById(R.id.button3);
//
//        // Set OnClickListener for the button
//        button3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startVoiceRecorder();
//            }
//        });
    }
    public void onMenuItemClick(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.action_audio) {
                    startVoiceRecorder();
                    return true;
                } else if (itemId == R.id.action_files) {
                    buttonOpenFile();
                    return true;
                } else if (itemId == R.id.action_cancel) {
                    return true;
                } else {
                    return false;
                }
            }

        });

        popupMenu.inflate(R.menu.popup_audio_menu);
        popupMenu.show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            // Get the recognized speech
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && result.size() > 0) {
                // Show the recognized text in an AlertDialog
                showTextInDialog(result.get(0));
            }
        }
    }

    private void showTextInDialog(String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Text");

        // Create EditText field for text input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);
        input.setText(text); // Set the recognized text in the EditText

        // Set the EditText as the view for the AlertDialog
        builder.setView(input);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = input.getText().toString();
                saveTextToFile(text);
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

    public void saveTextToFile(String text) {
        try {
            FileOutputStream fos = openFileOutput("audio_text.txt", MODE_PRIVATE);
            fos.write(text.getBytes());
            fos.close();
            Toast.makeText(this, "Text saved to file", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save text to file", Toast.LENGTH_SHORT).show();
        }
    }
    private void openFile(Uri fileUri) {
        Intent viewIntent = new Intent(Intent.ACTION_VIEW);
        viewIntent.setData(fileUri);
        viewIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(viewIntent);
        } catch (Exception e) {
            Log.e(TAG, "Error opening file: " + e.getMessage());
            Toast.makeText(this, "Unable to open the file", Toast.LENGTH_SHORT).show();
        }


    }
    public void buttonOpenFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, OPEN_DOCUMENT_REQUEST_CODE);
    }
}
