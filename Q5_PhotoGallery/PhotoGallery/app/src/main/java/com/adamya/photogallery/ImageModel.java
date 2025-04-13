package com.adamya.photogallery;

import android.net.Uri;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageModel implements Serializable {
    private String name;
    private String path;
    private long size;
    private long dateModified;
    private transient Uri uri;

    public ImageModel(File file) {
        this.name = file.getName();
        this.path = file.getAbsolutePath();
        this.size = file.length();
        this.dateModified = file.lastModified();
        this.uri = Uri.fromFile(file);
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public long getSize() {
        return size;
    }

    public String getFormattedSize() {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format(Locale.getDefault(), "%.2f KB", size / 1024f);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format(Locale.getDefault(), "%.2f MB", size / (1024f * 1024f));
        } else {
            return String.format(Locale.getDefault(), "%.2f GB", size / (1024f * 1024f * 1024f));
        }
    }

    public long getDateModified() {
        return dateModified;
    }

    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date(dateModified));
    }

    public Uri getUri() {
        if (uri == null) {
            uri = Uri.fromFile(new File(path));
        }
        return uri;
    }
} 