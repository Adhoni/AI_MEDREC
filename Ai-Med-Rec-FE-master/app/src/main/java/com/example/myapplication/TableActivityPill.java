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

public class TableActivityPill extends AppCompatActivity {
    private ImageView imagePreview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_pill);
        imagePreview = findViewById(R.id.imagePreview); // Initialize the ImageView

        // Get the image data from the intent
        byte[] imageData = getIntent().getByteArrayExtra("imageData");
        if (imageData != null) {
            Bitmap capturedImageBitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
            imagePreview.setImageBitmap(capturedImageBitmap);
            imagePreview.setVisibility(View.VISIBLE); // Show the ImageView
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
                    TableRow tableRow = new TableRow(TableActivityPill.this);
                    tableRow.setGravity(Gravity.CENTER);

                    tableRow.addView(messageTextView);
                    tableLayout.addView(tableRow);
                } else {
                    // Create a new row
                    TableRow tableRow = new TableRow(TableActivityPill.this);

                    // Get the values from the JSONObject
                    String name = jsonObject.getString("name");
                    String dosage = jsonObject.getString("dosage");
                    String pillNumber = jsonObject.getString("pill_num");
                    String color = jsonObject.getString("color");
                    String shape = jsonObject.getString("shape");

                    // Create TextViews for each column
                    TextView nameTextView = new TextView(TableActivityPill.this);
                    nameTextView.setText(name);
                    nameTextView.setPadding(8, 8, 8, 8);
                    nameTextView.setTextSize(20);
                    nameTextView.setGravity(Gravity.CENTER);

                    TextView dosageTextView = new TextView(TableActivityPill.this);
                    dosageTextView.setText(dosage);
                    dosageTextView.setPadding(8, 8, 8, 8);
                    dosageTextView.setTextSize(20);
                    dosageTextView.setGravity(Gravity.CENTER);

                    TextView pillNumberTextView = new TextView(TableActivityPill.this);
                    pillNumberTextView.setText(pillNumber);
                    pillNumberTextView.setPadding(8, 8, 8, 8);
                    pillNumberTextView.setTextSize(20);
                    pillNumberTextView.setGravity(Gravity.CENTER);

                    TextView colorTextView = new TextView(TableActivityPill.this);
                    colorTextView.setText(color);
                    colorTextView.setPadding(8, 8, 8, 8);
                    colorTextView.setTextSize(20);
                    colorTextView.setGravity(Gravity.CENTER);

                    TextView shapeTextView = new TextView(TableActivityPill.this);
                    shapeTextView.setText(shape);
                    shapeTextView.setPadding(8, 8, 8, 8);
                    shapeTextView.setTextSize(20);
                    shapeTextView.setGravity(Gravity.CENTER);

                    // Add TextViews to the row
                    tableRow.addView(nameTextView);
                    tableRow.addView(dosageTextView);
                    tableRow.addView(pillNumberTextView);
                    tableRow.addView(colorTextView);
                    tableRow.addView(shapeTextView);
                    tableRow.setBackgroundColor(Color.parseColor("#ADD8E6"));

                    // Add the row to the TableLayout
                    tableLayout.addView(tableRow);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(TableActivityPill.this, "Failed to parse JSON data", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Handle the case where jsonData is null
            Toast.makeText(this, "No data received", Toast.LENGTH_SHORT).show();
        }
    }
}
