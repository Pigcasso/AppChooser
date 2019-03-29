package io.zhuliang.appchooser.data;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Action1;

import static io.zhuliang.appchooser.internal.Preconditions.checkNotNull;

/**
 * @author Zhu Liang
 * @version 1.0
 * @since 2017/4/15 下午1:59
 */
public class MediaTypesRepository implements MediaTypesDataSource {

    @NonNull
    private final MediaTypesDataSource mMediaTypesLocalDataSource;

    private List<MediaType> mCachedMediaTypes;

    public MediaTypesRepository(@NonNull MediaTypesDataSource mediaTypesLocalDataSource) {
        mMediaTypesLocalDataSource = checkNotNull(mediaTypesLocalDataSource);
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
