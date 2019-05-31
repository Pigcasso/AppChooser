package io.zhuliang.appchooser.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author Zhu Liang
 * @version 1.0
 * @since 2017/4/15 下午1:41
 */

public interface ActivityInfosDataSource {

    void saveActivityInfo(@NonNull ActivityInfo activityInfo);

    @Nullable
    ActivityInfo getActivityInfo(@Nullable String mimeType);

    int deleteActivityInfo(@Nullable String mimeType);

    int deleteAllActivityInfos();

}
