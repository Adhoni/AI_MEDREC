package com.example.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Gravity;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;

public class TableActivityPillBox extends AppCompatActivity {
    private ImageView imagePreview1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_pill_box);
        imagePreview1 = findViewById(R.id.imagePreview1); // Initialize the ImageView
        byte[] imageData = getIntent().getByteArrayExtra("imageData");
        if (imageData != null) {
            Bitmap capturedImageBitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
            imagePreview1.setImageBitmap(capturedImageBitmap);
            imagePreview1.setVisibility(View.VISIBLE); // Show the ImageView
        }
        TableLayout tableLayout = findViewById(R.id.tableLayout);
        TextView messageTextView = new TextView(this);
        messageTextView.setGravity(Gravity.CENTER);
        messageTextView.setTextSize(18);
        messageTextView.setTextColor(Color.RED);
        messageTextView.setPadding(16, 16, 16, 16);

        // Get the JSON string from the intent
        String jsonData = getIntent().getStringExtra("jsonData");

        if (jsonData != null) {
            try {
                // Parse the JSON data as a JSONObject
                JSONObject jsonObject = new JSONObject(jsonData);

                // Check for "Imprint response" key
                if (jsonObject.has("Imprint response")) {
                    // Clear the table and show the message
//                    setContentView(R.layout.empty_layout); // Set a new layout with a RelativeLayout
//                    RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
                    tableLayout.removeAllViews();
                    messageTextView.setText("No medicine found");
                    messageTextView.setTextSize(30); // Increase font size
                    messageTextView.setGravity(Gravity.CENTER);
                    messageTextView.setTextColor(Color.RED);
                    messageTextView.setPadding(16, 16, 16, 16);

                    // Create a new TableRow to center the message
                    TableRow tableRow = new TableRow(TableActivityPillBox.this);
                    tableRow.setGravity(Gravity.CENTER);

                    tableRow.addView(messageTextView);
                    tableLayout.addView(tableRow);
                } else {
                    // Create a new row
                    TableRow tableRow = new TableRow(TableActivityPillBox.this);

                    // Get the values from the JSONObject
                    String extracted_text = jsonObject.getString("extracted_text");
                    String name = jsonObject.getString("name");
                    String dosage = jsonObject.getString("dosage");
                    String number_of_pills = jsonObject.getString("number_of_pills");
//                    String color = jsonObject.getString("color");
//                    String shape = jsonObject.getString("shape");

                    // Create TextViews for each column
                    TextView extractedTextView = new TextView(TableActivityPillBox.this);
                    extractedTextView.setText(extracted_text);
                    extractedTextView.setPadding(8, 8, 8, 8);
                    extractedTextView.setTextSize(20);
                    extractedTextView.setGravity(Gravity.CENTER);

                    TextView nameTextView = new TextView(TableActivityPillBox.this);
                    nameTextView.setText(name);
                    nameTextView.setPadding(8, 8, 8, 8);
                    nameTextView.setTextSize(20);
                    nameTextView.setGravity(Gravity.CENTER);

                    TextView dosageTextView = new TextView(TableActivityPillBox.this);
                    dosageTextView.setText(dosage);
                    dosageTextView.setPadding(8, 8, 8, 8);
                    dosageTextView.setTextSize(20);
                    dosageTextView.setGravity(Gravity.CENTER);

                    TextView numberOfPillsTextView = new TextView(TableActivityPillBox.this);
                    numberOfPillsTextView.setText(number_of_pills);
                    numberOfPillsTextView.setPadding(8, 8, 8, 8);
                    numberOfPillsTextView.setTextSize(20);
                    numberOfPillsTextView.setGravity(Gravity.CENTER);

                    // Add TextViews to the row
                    tableRow.addView(extractedTextView);
                    tableRow.addView(nameTextView);
                    tableRow.addView(dosageTextView);
                    tableRow.addView(numberOfPillsTextView);
                    tableRow.setBackgroundColor(Color.parseColor("#ADD8E6"));

                    // Add the row to the TableLayout
                    tableLayout.addView(tableRow);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(TableActivityPillBox.this, "Failed to parse JSON data", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Handle the case where jsonData is null
            Toast.makeText(this, "No data received", Toast.LENGTH_SHORT).show();
        }
    }
}
