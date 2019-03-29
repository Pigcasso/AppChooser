package io.zhuliang.appchooser.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Map;

import io.zhuliang.appchooser.BuildConfig;
import io.zhuliang.appchooser.data.ActivityInfo;
import io.zhuliang.appchooser.data.ActivityInfosDataSource;
import rx.Observable;

import static io.zhuliang.appchooser.internal.Preconditions.checkNotNull;

/**
 * @author Zhu Liang
 * @version 1.0
 * @since 2017/5/5 下午3:26
 */

public class ActivityInfosSharedPreferencesDataSource implements ActivityInfosDataSource {

    private static final String SP_NAME = BuildConfig.APPLICATION_ID + "_preferences";

    private SharedPreferences mPreferences;

    public ActivityInfosSharedPreferencesDataSource(@NonNull Context applicationContext) {
        checkNotNull(applicationContext);
        mPreferences = applicationContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public void saveActivityInfo(@NonNull ActivityInfo activityInfo) {
        checkNotNull(activityInfo);
        String mimeType = checkNotNull(activityInfo.getMimeType());
        String pkg = checkNotNull(activityInfo.getPkg());
        String cls = checkNotNull(activityInfo.getCls());

        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(mimeType, String.format("%s|%s", pkg, cls)).apply();
    }

    @NonNull
    @Override
    public Observable<ActivityInfo> getActivityInfo(@Nullable String mimeType) {
        String value = mPreferences.getString(mimeType, null);
        if (value == null) {
            return Observable.just(null);
        }
        String[] pkgAndCls = value.split("\\|");
        String pkg = pkgAndCls[0];
        String cls = pkgAndCls[1];
        return Observable.just(new ActivityInfo(mimeType, pkg, cls));
    }

    @Override
    public int deleteActivityInfo(@Nullable String mimeType) {
        if (mimeType == null) {
            return 0;
        } else {
            if (mPreferences.contains(mimeType)) {
                SharedPreferences.Editor editor = mPreferences.edit();
                editor.remove(mimeType).apply();
                return 1;
            } else {
                return 0;
            }
        }
    }

    @Override
    public int deleteAllActivityInfos() {
        Map<String, ?> map = mPreferences.getAll();
        if (map == null || map.size() == 0) {
            return 0;
        } else {
            mPreferences.edit().clear().apply();
            return map.size();
        }
    }
}
