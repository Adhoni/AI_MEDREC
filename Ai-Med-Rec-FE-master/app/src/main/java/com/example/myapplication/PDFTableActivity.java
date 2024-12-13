package com.example.myapplication;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.widget.ImageView;

import android.graphics.Color;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class PDFTableActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table); // Reuse the same XML layout as TableActivity

        TableLayout tableLayout = findViewById(R.id.tableLayout);
        ImageView pdfPreviewImageView = findViewById(R.id.pdfPreviewImageView);

        // Get the JSON string from the intent
        String jsonData = getIntent().getStringExtra("jsonData");
        Uri pdfFileUri = getIntent().getParcelableExtra("pdfFileUri");

        if (pdfFileUri != null) {
            try {
                File pdfFile = copyPdfToInternalStorage(pdfFileUri);
                showPdfPreview(pdfFile, pdfPreviewImageView);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load PDF preview", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No PDF file received", Toast.LENGTH_SHORT).show();
        }

        Log.d("ActivityName", "Received JSON data: " + jsonData);

        if (jsonData == null || jsonData.isEmpty()) {
            Toast.makeText(this, "No data received", Toast.LENGTH_SHORT).show();
            return;
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
                    TableRow tableRow = new TableRow(PDFTableActivity.this);

                    // Create TextViews for each column
                    TextView nameTextView = new TextView(PDFTableActivity.this);
                    nameTextView.setText(name);
                    nameTextView.setPadding(8, 8, 8, 8);

                    TextView administrationTextView = new TextView(PDFTableActivity.this);
                    administrationTextView.setText(administration);
                    administrationTextView.setPadding(8, 8, 8, 8);

                    TextView dosageTextView = new TextView(PDFTableActivity.this);
                    dosageTextView.setText(dosage);
                    dosageTextView.setPadding(8, 8, 8, 8);

                    TextView frequencyTextView = new TextView(PDFTableActivity.this);
                    frequencyTextView.setText(frequency);
                    frequencyTextView.setPadding(8, 8, 8, 8);

                    // Add TextViews to the row
                    tableRow.addView(nameTextView);
                    tableRow.addView(administrationTextView);
                    tableRow.addView(dosageTextView);
                    tableRow.addView(frequencyTextView);

                    // Apply styles to match TableActivity
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
                Toast.makeText(PDFTableActivity.this, "Failed to parse JSON data", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Handle the case where jsonData is null
            Toast.makeText(this, "No data received", Toast.LENGTH_SHORT).show();
        }
    }

    private File copyPdfToInternalStorage(Uri pdfUri) throws IOException {
        File pdfFile = new File(getCacheDir(), "preview.pdf");
        try (InputStream inputStream = getContentResolver().openInputStream(pdfUri);
             FileOutputStream outputStream = new FileOutputStream(pdfFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
        return pdfFile;
    }

    private void showPdfPreview(File pdfFile, ImageView pdfPreviewImageView) throws IOException {
        ParcelFileDescriptor fileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY);
        PdfRenderer pdfRenderer = new PdfRenderer(fileDescriptor);

        if (pdfRenderer.getPageCount() > 0) {
            PdfRenderer.Page page = pdfRenderer.openPage(0);
            Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

            pdfPreviewImageView.setImageBitmap(bitmap);
            page.close();
        }

        pdfRenderer.close();
        fileDescriptor.close();
    }
}
