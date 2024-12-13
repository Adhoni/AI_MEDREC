package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class NextActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);

        // Retrieve the response from the Intent
        Intent intent = getIntent();
        String response = intent.getStringExtra("response");

        // Display the response in a TextView
        TextView responseTextView = findViewById(R.id.responseTextView);
        if (response != null) {
            responseTextView.setText(response);
        } else {
            responseTextView.setText("No response received");
        }
    }
}
