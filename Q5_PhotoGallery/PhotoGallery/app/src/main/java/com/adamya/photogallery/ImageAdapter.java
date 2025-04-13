package com.adamya.photogallery;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private List<ImageModel> images;
    private Context context;

    public ImageAdapter(Context context, List<ImageModel> images) {
        this.context = context;
        this.images = images;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ImageModel image = images.get(position);
        
        // Set image name
        holder.tvImageName.setText(image.getName());
        
        // Load image with Glide
        Glide.with(context)
                .load(image.getUri())
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(holder.ivImage);

        // Set click listener for the entire item
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ImageDetailsActivity.class);
            intent.putExtra("image", image);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public void updateImages(List<ImageModel> newImages) {
        this.images = newImages;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvImageName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvImageName = itemView.findViewById(R.id.tvImageName);
        }
    }
} 