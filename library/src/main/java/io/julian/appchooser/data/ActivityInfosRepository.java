package io.julian.appchooser.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import rx.Observable;

import static io.julian.appchooser.util.Preconditions.checkNotNull;

/**
 * @author Zhu Liang
 * @version 1.0
 * @since 2017/4/15 下午1:41
 */

public class ActivityInfosRepository implements ActivityInfosDataSource {

    private static ActivityInfosRepository INSTANCE;
    private ActivityInfosDataSource mActivityInfosLocalDataSource;

    private ActivityInfosRepository(@NonNull ActivityInfosDataSource activityInfosDataSource) {
        mActivityInfosLocalDataSource = checkNotNull(activityInfosDataSource);
    }

    public static ActivityInfosRepository getInstance(@NonNull ActivityInfosDataSource activityInfosDataSource) {
        if (INSTANCE == null) {
            synchronized (ActivityInfosRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ActivityInfosRepository(activityInfosDataSource);
                }
            }
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public void saveActivityInfo(@NonNull ActivityInfo activityInfo) {
        mActivityInfosLocalDataSource.saveActivityInfo(activityInfo);
    }

    @NonNull
    @Override
    public Observable<ActivityInfo> getActivityInfo(@Nullable String mimeType) {
        return mActivityInfosLocalDataSource.getActivityInfo(mimeType);
    }

    @Override
    public int deleteActivityInfo(@Nullable String mimeType) {
        return mActivityInfosLocalDataSource.deleteActivityInfo(mimeType);
    }

    @Override
    public int deleteAllActivityInfos() {
        return mActivityInfosLocalDataSource.deleteAllActivityInfos();
    }
}
