package io.zhuliang.appchooser.sample.module.mediastoreimages;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import io.zhuliang.appchooser.AppChooser;
import io.zhuliang.appchooser.sample.R;
import io.zhuliang.appchooser.sample.data.MediaStoreImage;

public class MediaStoreImagesActivity extends AppCompatActivity {

    private MediaStoreImagesViewModel viewModel;

    public static void start(Context context) {
        Intent starter = new Intent(context, MediaStoreImagesActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_media_store_images);

        viewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(MediaStoreImagesViewModel.class);

        final GalleryAdapter galleryAdapter = new GalleryAdapter(new OnImageClickListener() {
            @Override
            public void onClick(MediaStoreImage image) {
                AppChooser.from(MediaStoreImagesActivity.this)
                        .edit(image.contentUri)
                        .mimeType(image.mimeType)
                        .load();
            }
        });

        RecyclerView gallery = findViewById(R.id.gallery);
        gallery.setLayoutManager(new GridLayoutManager(this, 3));
        gallery.setAdapter(galleryAdapter);

        viewModel.mediaStoreImages.observe(this, new Observer<List<MediaStoreImage>>() {
            @Override
            public void onChanged(List<MediaStoreImage> mediaStoreImages) {
                galleryAdapter.submitList(mediaStoreImages);
            }
        });

        showImages();
    }

    private void showImages() {
        viewModel.loadImages();
    }

    private static class GalleryAdapter extends ListAdapter<MediaStoreImage, ImageViewHolder> {

        private OnImageClickListener listener;

        protected GalleryAdapter(OnImageClickListener listener) {
            super(MediaStoreImage.DiffCallback);
            this.listener = listener;
        }

        @NonNull
        @Override
        public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.gallery_layout, parent, false);
            return new ImageViewHolder(view, listener);
        }

        @Override
        public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
            MediaStoreImage mediaStoreImage = getItem(position);
            holder.itemView.setTag(mediaStoreImage);
            Glide.with(holder.imageView)
                    .load(mediaStoreImage.contentUri)
                    .thumbnail(0.33f)
                    .centerCrop()
                    .into(holder.imageView);
        }
    }

    private static class ImageViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;

        public ImageViewHolder(@NonNull final View itemView, final OnImageClickListener listener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick((MediaStoreImage) itemView.getTag());
                }
            });
        }
    }

    private interface OnImageClickListener {
        void onClick(MediaStoreImage image);
    }
}
