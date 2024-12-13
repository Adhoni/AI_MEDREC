package com.example.myapplication;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.view.Gravity;
import android.graphics.Color;

public class TableActivity extends AppCompatActivity {

    private TextView inputFileContent;
//    private Dialog previewDialog;
    private CardView cardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);

//        inputFileContent = findViewById(R.id.inputFileContent);
        TableLayout tableLayout = findViewById(R.id.tableLayout);
        cardView = findViewById(R.id.previewCardView);

        if (cardView != null) {
            cardView.setVisibility(View.GONE); // Default visibility
        } else {
            Log.e("TableActivity", "CardView not found in layout.");
        }

        TextView previewTextView = findViewById(R.id.previewTextView);

        // Get the JSON string and text data from the intent
        String jsonData = getIntent().getStringExtra("jsonData");
        String inputData = getIntent().getStringExtra("inputData");

        // Retrieve the edited text from the Intent
        String editedText = getIntent().getStringExtra("editedText");

        // Display the text in previewTextView if it exists
        if (editedText != null && !editedText.isEmpty()) {
            previewTextView.setText(editedText);
            previewTextView.setVisibility(View.VISIBLE); // Make the TextView visible
            cardView.setVisibility(View.VISIBLE);
        } else {
            previewTextView.setVisibility(View.GONE); // Hide it if no text is provided
        }

        if (inputData != null) {
            // Create a preview dialog to display the input file content
//            createPreviewDialog(inputData);
            createScrollableCardView(inputData);
        }

        if (jsonData != null) {
            try {
                // Parse the JSON data
                JSONArray jsonArray = new JSONArray(jsonData);

                // Iterate through the JSON array and add rows to the table
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject row = jsonArray.getJSONObject(i);

                    String name = row.isNull("name") ? "-" : row.getString("name");
                    String administration = row.isNull("administration") ? "-" : row.getString("administration");
                    String dosage = row.isNull("dosage") ? "-" : row.getString("dosage");
                    String frequency = row.isNull("frequency") ? "-" : row.getString("frequency");

                    // Create a new row
                    TableRow tableRow = new TableRow(TableActivity.this);

                    // Create TextViews for each column
                    TextView nameTextView = new TextView(TableActivity.this);
                    nameTextView.setText(name);
                    nameTextView.setPadding(8, 8, 8, 8);

                    TextView administrationTextView = new TextView(TableActivity.this);
                    administrationTextView.setText(administration);
                    administrationTextView.setPadding(8, 8, 8, 8);

                    TextView dosageTextView = new TextView(TableActivity.this);
                    dosageTextView.setText(dosage);
                    dosageTextView.setPadding(8, 8, 8, 8);

                    TextView frequencyTextView = new TextView(TableActivity.this);
                    frequencyTextView.setText(frequency);
                    frequencyTextView.setPadding(8, 8, 8, 8);

                    // Add TextViews to the row
                    tableRow.addView(nameTextView);
                    tableRow.addView(administrationTextView);
                    tableRow.addView(dosageTextView);
                    tableRow.addView(frequencyTextView);

                    nameTextView.setTextSize(20);
                    administrationTextView.setTextSize(20);
                    dosageTextView.setTextSize(20);
                    frequencyTextView.setTextSize(20);

                    nameTextView.setGravity(Gravity.CENTER);
                    administrationTextView.setGravity(Gravity.CENTER);
                    dosageTextView.setGravity(Gravity.CENTER);
                    frequencyTextView.setGravity(Gravity.CENTER);

                    nameTextView.setBackgroundColor(Color.parseColor("#ADD8E6"));
                    administrationTextView.setBackgroundColor(Color.parseColor("#ADD8E6"));
                    dosageTextView.setBackgroundColor(Color.parseColor("#ADD8E6"));
                    frequencyTextView.setBackgroundColor(Color.parseColor("#ADD8E6"));

                    // Add the row to the TableLayout
                    tableLayout.addView(tableRow);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(TableActivity.this, "Failed to parse JSON data", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Handle the case where jsonData is null
            Toast.makeText(this, "No data received", Toast.LENGTH_SHORT).show();
        }
    }

    private void createScrollableCardView(String inputData) {
        cardView.setVisibility(View.VISIBLE);

        // Clear previous content from cardView if any
        cardView.removeAllViews();

        ScrollView scrollView = new ScrollView(TableActivity.this);
        scrollView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView textView = new TextView(TableActivity.this);
        textView.setText(inputData); // Set the text content from the inputData
        textView.setTextSize(18);
        textView.setTextColor(Color.BLACK);
        textView.setPadding(16, 16, 16, 16);
        textView.setGravity(Gravity.START); // Align text to the left

        scrollView.addView(textView);
        cardView.addView(scrollView);
    }

//    private void createPreviewDialog(String inputData) {
//        previewDialog = new Dialog(TableActivity.this);
//        previewDialog.setContentView(R.layout.preview_dialog);
//        previewDialog.setTitle("Input File Content Preview");
//
//        TextView previewTextView = previewDialog.findViewById(R.id.previewTextView);
//        previewTextView.setText(inputData);
//
//        Button closeButton = previewDialog.findViewById(R.id.closeButton);
//        closeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                previewDialog.dismiss();
//            }
//        });
//
//        previewDialog.show();
//    }
}