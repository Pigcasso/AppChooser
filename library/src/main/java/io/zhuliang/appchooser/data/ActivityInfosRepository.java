package io.zhuliang.appchooser.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import rx.Observable;

import static io.zhuliang.appchooser.internal.Preconditions.checkNotNull;

/**
 * @author Zhu Liang
 * @version 1.0
 * @since 2017/4/15 下午1:41
 */

public class ActivityInfosRepository implements ActivityInfosDataSource {

    private ActivityInfosDataSource mActivityInfosLocalDataSource;

    public ActivityInfosRepository(@NonNull ActivityInfosDataSource activityInfosDataSource) {
        mActivityInfosLocalDataSource = checkNotNull(activityInfosDataSource);
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
