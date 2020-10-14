package io.zhuliang.appchooser.sample.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

public class MediaStoreImagesRepository {

    private Context context;

    public MediaStoreImagesRepository(Context context) {
        this.context = context;
    }

    public List<MediaStoreImage> listImages() {
        ContentResolver resolver = context.getContentResolver();
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[]{
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DISPLAY_NAME,
                MediaStore.Images.ImageColumns.MIME_TYPE
        };
        String sortOrder = MediaStore.Images.Media.DATE_MODIFIED + "DESC";
        Cursor cursor = null;
        List<MediaStoreImage> images = new ArrayList<>();
        try {
            cursor = resolver.query(uri, projection, null, null, sortOrder);
            if (cursor != null) {
                int idColumn = cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                int displayNameColumn = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME);
                int mimeTypeColumn = cursor.getColumnIndex(MediaStore.Images.ImageColumns.MIME_TYPE);

                while (cursor.moveToNext()) {
                    long id = cursor.getLong(idColumn);
                    String displayName = cursor.getString(displayNameColumn);
                    String mimeType = cursor.getString(mimeTypeColumn);
                    images.add(new MediaStoreImage(id, displayName, mimeType, ContentUris.withAppendedId(uri, id)));
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return images;
    }
}
