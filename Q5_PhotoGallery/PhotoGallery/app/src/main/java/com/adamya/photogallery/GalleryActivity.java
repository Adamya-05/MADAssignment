package com.adamya.photogallery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {

    private RecyclerView rvImages;
    private TextView tvCurrentFolder;
    private Button btnSelectFolder;
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

        rvImages = findViewById(R.id.rvImages);
        tvCurrentFolder = findViewById(R.id.tvCurrentFolder);
        btnSelectFolder = findViewById(R.id.btnSelectFolder);

        // Set up RecyclerView
        rvImages.setLayoutManager(new GridLayoutManager(this, 3));
        imageAdapter = new ImageAdapter(this, imageList);
        rvImages.setAdapter(imageAdapter);

        // Set initial folder
        currentFolder = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        tvCurrentFolder.setText("Current folder: " + currentFolder.getAbsolutePath());
        loadImagesFromFolder(currentFolder);

        // Set up folder selection
        selectFolderLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        // We'll need to implement a folder picker, for now we'll just use the Pictures directory
                        File selectedFolder = new File(result.getData().getStringExtra("selected_folder_path"));
                        if (selectedFolder.exists() && selectedFolder.isDirectory()) {
                            currentFolder = selectedFolder;
                            tvCurrentFolder.setText("Current folder: " + currentFolder.getAbsolutePath());
                            loadImagesFromFolder(currentFolder);
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
                Toast.makeText(this, "Unable to open folder picker: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
            Toast.makeText(this, "No images found in this folder", Toast.LENGTH_SHORT).show();
        }
        
        imageAdapter.updateImages(imageList);
    }
} 