package io.zhuliang.appchooser.sample.data;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import java.util.Objects;

public class MediaStoreImage {

    public final long id;
    public final String displayName;
    public final String mimeType;
    public final Uri contentUri;

    public static DiffUtil.ItemCallback<MediaStoreImage> DiffCallback = new DiffUtil.ItemCallback<MediaStoreImage>() {
        @Override
        public boolean areItemsTheSame(@NonNull MediaStoreImage oldItem, @NonNull MediaStoreImage newItem) {
            return oldItem.id == newItem.id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull MediaStoreImage oldItem, @NonNull MediaStoreImage newItem) {
            return oldItem.equals(newItem);
        }
    };

    public MediaStoreImage(long id, String displayName, String mimeType, Uri contentUri) {
        this.id = id;
        this.displayName = displayName;
        this.mimeType = mimeType;
        this.contentUri = contentUri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MediaStoreImage that = (MediaStoreImage) o;
        return id == that.id &&
                displayName.equals(that.displayName) &&
                contentUri.equals(that.contentUri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, displayName, contentUri);
    }
}
