package com.adamya.photogallery;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ActivityResultLauncher<String[]> requestMultiplePermissionsLauncher;
    private ActivityResultLauncher<Uri> takePictureLauncher;
    private Uri photoUri;
    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnTakePhoto = findViewById(R.id.btnTakePhoto);
        Button btnViewGallery = findViewById(R.id.btnViewGallery);

        // Set up permission requests
        requestMultiplePermissionsLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    boolean allGranted = true;
                    for (Boolean granted : result.values()) {
                        if (!granted) {
                            allGranted = false;
                            break;
                        }
                    }
                    if (allGranted) {
                        dispatchTakePictureIntent();
                    } else {
                        Toast.makeText(this, "Permissions are required to take photos", Toast.LENGTH_SHORT).show();
                    }
                });

        // Set up camera result
        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                success -> {
                    if (success) {
                        Toast.makeText(this, "Photo saved successfully", Toast.LENGTH_SHORT).show();
                        // Notify gallery about new image
                        galleryAddPic();
                    } else {
                        Toast.makeText(this, "Failed to save photo", Toast.LENGTH_SHORT).show();
                    }
                });

        // Set button click listeners
        btnTakePhoto.setOnClickListener(v -> {
            checkPermissionsAndTakePhoto();
        });

        btnViewGallery.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(this, 
                        Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                    requestMultiplePermissionsLauncher.launch(new String[]{
                            Manifest.permission.READ_MEDIA_IMAGES
                    });
                    return;
                }
            } else if (ContextCompat.checkSelfPermission(this, 
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestMultiplePermissionsLauncher.launch(new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE
                });
                return;
            }
            startActivity(new Intent(MainActivity.this, GalleryActivity.class));
        });
    }

    private void checkPermissionsAndTakePhoto() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                requestMultiplePermissionsLauncher.launch(new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_MEDIA_IMAGES
                });
            } else {
                dispatchTakePictureIntent();
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestMultiplePermissionsLauncher.launch(new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                });
            } else {
                dispatchTakePictureIntent();
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );

        // Save a file path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        try {
            File photoFile = createImageFile();
            photoUri = FileProvider.getUriForFile(this,
                    "com.adamya.photogallery.fileprovider",
                    photoFile);
            takePictureLauncher.launch(photoUri);
        } catch (IOException ex) {
            // Error occurred while creating the File
            Toast.makeText(this, "Error creating image file", Toast.LENGTH_SHORT).show();
        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }
}