package com.adamya.photogallery;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;

public class ImageDetailsActivity extends AppCompatActivity {

    private ImageView ivDetailImage;
    private TextView tvImageName, tvImagePath, tvImageSize, tvImageDate;
    private MaterialButton btnDeleteImage;
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
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        
        // Set up toolbar
        toolbar.setNavigationOnClickListener(v -> finish());

        // Get image data from intent
        if (getIntent().hasExtra("image")) {
            imageModel = (ImageModel) getIntent().getSerializableExtra("image");
            displayImageDetails();
            // Set toolbar title to image name
            toolbar.setTitle(imageModel.getName());
        } else {
            showSnackbar("Error: No image data found");
            finish();
            return;
        }

        // Set up delete button
        btnDeleteImage.setOnClickListener(v -> confirmAndDeleteImage());
    }

    private void displayImageDetails() {
        // Load image with Glide with improved loading
        Glide.with(this)
                .load(imageModel.getUri())
                .transition(DrawableTransitionOptions.withCrossFade())
                .centerCrop()
                .into(ivDetailImage);

        // Set text details
        tvImageName.setText(imageModel.getName());
        tvImagePath.setText(imageModel.getPath());
        tvImageSize.setText(imageModel.getFormattedSize());
        tvImageDate.setText(imageModel.getFormattedDate());
    }

    private void confirmAndDeleteImage() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.delete_confirmation_title))
                .setMessage(getString(R.string.delete_confirmation))
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> deleteImage())
                .setNegativeButton(getString(R.string.no), null)
                .create()
                .show();
    }

    private void deleteImage() {
        File file = new File(imageModel.getPath());
        if (file.exists()) {
            boolean deleted = file.delete();
            if (deleted) {
                try {
                    // Notify the MediaStore about the deletion
                    getContentResolver().delete(
                            imageModel.getUri(),
                            null,
                            null
                    );
                } catch (Exception e) {
                    // Continue even if MediaStore update fails
                }
                
                showSnackbar(getString(R.string.image_deleted));
                finish(); // Return to gallery
            } else {
                showSnackbar("Failed to delete image. Check app permissions.");
            }
        } else {
            showSnackbar("Image no longer exists");
            finish();
        }
    }
    
    private void showSnackbar(String message) {
        View rootView = findViewById(android.R.id.content);
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG)
                .setBackgroundTint(getResources().getColor(R.color.primary_dark, getTheme()))
                .setTextColor(getResources().getColor(R.color.white, getTheme()))
                .show();
    }
} 