package io.julian.appchooser.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.SharedPreferencesCompat;

import java.util.Map;

import io.julian.appchooser.BuildConfig;
import io.julian.appchooser.data.ActivityInfo;
import io.julian.appchooser.data.ActivityInfosDataSource;
import rx.Observable;

import static io.julian.common.Preconditions.checkNotNull;

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
        editor.putString(mimeType, String.format("%s|%s", pkg, cls));
        SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
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
                editor.remove(mimeType);
                SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
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
            SharedPreferences.Editor editor = mPreferences.edit();
            editor.clear();
            SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
            return map.size();
        }
    }
}
