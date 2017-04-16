package io.julian.appchooser.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Action1;

import static io.julian.appchooser.util.Preconditions.checkNotNull;

/**
 * @author Zhu Liang
 * @version 1.0
 * @since 2017/4/15 下午1:59
 */
public class MediaTypesRepository implements MediaTypesDataSource {

    @Nullable
    private static MediaTypesRepository INSTANCE;

    @NonNull
    private final MediaTypesDataSource mMediaTypesLocalDataSource;

    private List<MediaType> mCachedMediaTypes;

    private MediaTypesRepository(@NonNull MediaTypesDataSource mediaTypesLocalDataSource) {
        mMediaTypesLocalDataSource = checkNotNull(mediaTypesLocalDataSource);
    }

    public static MediaTypesRepository getInstance(
            @NonNull MediaTypesDataSource mediaTypesLocalDataSource) {

        if (INSTANCE == null) {
            synchronized (MediaTypesRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MediaTypesRepository(mediaTypesLocalDataSource);
                }
            }
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public Observable<List<MediaType>> listMediaTypes() {
        if (mCachedMediaTypes != null) {
            return Observable.just(mCachedMediaTypes);
        }
        return mMediaTypesLocalDataSource
                .listMediaTypes()
                .doOnNext(new Action1<List<MediaType>>() {
                    @Override
                    public void call(List<MediaType> mediaTypes) {
                        mCachedMediaTypes = new ArrayList<>(mediaTypes);
                    }
                });
    }
}
