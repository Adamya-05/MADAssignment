package com.adamya.photogallery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {

    private RecyclerView rvImages;
    private TextView tvCurrentFolder;
    private TextView tvEmptyGallery;
    private MaterialButton btnSelectFolder;
    private ImageAdapter imageAdapter;
    private List<ImageModel> imageList = new ArrayList<>();
    private File currentFolder;
    private ActivityResultLauncher<Intent> selectFolderLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gallery);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI components
        rvImages = findViewById(R.id.rvImages);
        tvCurrentFolder = findViewById(R.id.tvCurrentFolder);
        btnSelectFolder = findViewById(R.id.btnSelectFolder);
        tvEmptyGallery = findViewById(R.id.tvEmptyGallery);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        
        // Set up toolbar
        toolbar.setNavigationOnClickListener(v -> finish());

        // Set up RecyclerView with improved layout
        int spanCount = calculateSpanCount();
        rvImages.setLayoutManager(new GridLayoutManager(this, spanCount));
        imageAdapter = new ImageAdapter(this, imageList);
        rvImages.setAdapter(imageAdapter);

        // Set initial folder
        currentFolder = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        updateFolderDisplay();
        loadImagesFromFolder(currentFolder);

        // Set up folder selection
        selectFolderLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        // We'll need to implement a folder picker, for now we'll just use the Pictures directory
                        try {
                            File selectedFolder = new File(result.getData().getStringExtra("selected_folder_path"));
                            if (selectedFolder.exists() && selectedFolder.isDirectory()) {
                                currentFolder = selectedFolder;
                                updateFolderDisplay();
                                loadImagesFromFolder(currentFolder);
                            }
                        } catch (Exception e) {
                            showSnackbar("Unable to access selected folder");
                        }
                    }
                });

        // For simplicity, we'll use a folder picker dialog
        btnSelectFolder.setOnClickListener(v -> {
            // For now, we'll use a simple file browser intent
            // In a real app, you might want to implement a custom folder picker
            try {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                selectFolderLauncher.launch(intent);
            } catch (Exception e) {
                showSnackbar("Unable to open folder picker: " + e.getMessage());
            }
        });
    }

    private int calculateSpanCount() {
        // Adjust the span count based on screen width for better responsiveness
        float density = getResources().getDisplayMetrics().density;
        float dpWidth = getResources().getDisplayMetrics().widthPixels / density;
        
        // Assume each item should be about 120dp wide
        return Math.max(2, Math.round(dpWidth / 120));
    }
    
    private void updateFolderDisplay() {
        if (currentFolder != null) {
            String path = currentFolder.getAbsolutePath();
            String displayPath = path;
            
            // Truncate path if too long
            if (path.length() > 30) {
                displayPath = "..." + path.substring(path.length() - 30);
            }
            
            tvCurrentFolder.setText(displayPath);
        } else {
            tvCurrentFolder.setText(R.string.no_folder_selected);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentFolder != null) {
            loadImagesFromFolder(currentFolder);
        }
    }

    private void loadImagesFromFolder(File folder) {
        imageList.clear();
        if (folder != null && folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles((dir, name) -> {
                String lowerName = name.toLowerCase();
                return lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg") 
                        || lowerName.endsWith(".png") || lowerName.endsWith(".gif");
            });

            if (files != null && files.length > 0) {
                Arrays.sort(files, (f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));
                for (File file : files) {
                    imageList.add(new ImageModel(file));
                }
            }
        }
        
        if (imageList.isEmpty()) {
            tvEmptyGallery.setVisibility(View.VISIBLE);
            showSnackbar("No images found in this folder");
        } else {
            tvEmptyGallery.setVisibility(View.GONE);
        }
        
        imageAdapter.updateImages(imageList);
    }
    
    private void showSnackbar(String message) {
        View rootView = findViewById(android.R.id.content);
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG)
                .setBackgroundTint(getResources().getColor(R.color.primary_dark, getTheme()))
                .setTextColor(getResources().getColor(R.color.white, getTheme()))
                .show();
    }
} 