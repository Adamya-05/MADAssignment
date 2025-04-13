package com.adamya.photogallery;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

import java.io.File;

public class ImageDetailsActivity extends AppCompatActivity {

    private ImageView ivDetailImage;
    private TextView tvImageName, tvImagePath, tvImageSize, tvImageDate;
    private Button btnDeleteImage;
    private ImageModel imageModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_image_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Find views
        ivDetailImage = findViewById(R.id.ivDetailImage);
        tvImageName = findViewById(R.id.tvImageName);
        tvImagePath = findViewById(R.id.tvImagePath);
        tvImageSize = findViewById(R.id.tvImageSize);
        tvImageDate = findViewById(R.id.tvImageDate);
        btnDeleteImage = findViewById(R.id.btnDeleteImage);

        // Get image data from intent
        if (getIntent().hasExtra("image")) {
            imageModel = (ImageModel) getIntent().getSerializableExtra("image");
            displayImageDetails();
        } else {
            Toast.makeText(this, "Error: No image data found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set up delete button
        btnDeleteImage.setOnClickListener(v -> {
            confirmAndDeleteImage();
        });
    }

    private void displayImageDetails() {
        // Load image with Glide
        Glide.with(this)
                .load(imageModel.getUri())
                .into(ivDetailImage);

        // Set text details
        tvImageName.setText(imageModel.getName());
        tvImagePath.setText(imageModel.getPath());
        tvImageSize.setText(imageModel.getFormattedSize());
        tvImageDate.setText(imageModel.getFormattedDate());
    }

    private void confirmAndDeleteImage() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Image")
                .setMessage("Are you sure you want to delete this image? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteImage();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteImage() {
        File file = new File(imageModel.getPath());
        if (file.exists()) {
            boolean deleted = file.delete();
            if (deleted) {
                // Notify the MediaStore about the deletion
                getContentResolver().delete(
                        imageModel.getUri(),
                        null,
                        null
                );
                Toast.makeText(this, "Image deleted successfully", Toast.LENGTH_SHORT).show();
                finish(); // Return to gallery
            } else {
                Toast.makeText(this, "Failed to delete image", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Image no longer exists", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
} 