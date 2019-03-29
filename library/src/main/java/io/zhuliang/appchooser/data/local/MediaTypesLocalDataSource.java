package io.zhuliang.appchooser.data.local;

import android.content.Context;
import android.content.res.Resources;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import io.zhuliang.appchooser.R;
import io.zhuliang.appchooser.data.MediaType;
import io.zhuliang.appchooser.data.MediaTypesDataSource;
import rx.Observable;

import static io.zhuliang.appchooser.internal.Preconditions.checkNotNull;

/**
 * @author Zhu Liang
 * @version 1.0
 * @since 2017/4/15 下午1:47
 */

public class MediaTypesLocalDataSource implements MediaTypesDataSource {

    private Resources mResources;

    public MediaTypesLocalDataSource(@NonNull Context context) {
        checkNotNull(context);
        mResources = context.getResources();
    }

    @Override
    public Observable<List<MediaType>> listMediaTypes() {
        String[] displayNames = mResources.getStringArray(
                R.array.media_types_display_names);
        String[] mimeTypes = mResources.getStringArray(
                R.array.media_types_mime_types);

        int size = displayNames.length;
        List<MediaType> mediaTypes = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            mediaTypes.add(new MediaType(mimeTypes[i], displayNames[i]));
        }
        return Observable.just(mediaTypes);
    }
}
