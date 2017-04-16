package io.julian.appchooser.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import io.julian.appchooser.data.ActivityInfo;
import io.julian.appchooser.data.ActivityInfosDataSource;
import io.julian.appchooser.util.schedulers.BaseSchedulerProvider;
import rx.Observable;
import rx.functions.Func1;

import static android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE;
import static io.julian.appchooser.data.local.AppChooserPersistenceContract.ActivityInfoEntity.CLS;
import static io.julian.appchooser.data.local.AppChooserPersistenceContract.ActivityInfoEntity.MIME_TYPE;
import static io.julian.appchooser.data.local.AppChooserPersistenceContract.ActivityInfoEntity.PKG;
import static io.julian.appchooser.data.local.AppChooserPersistenceContract.ActivityInfoEntity.TABLE_NAME;
import static io.julian.appchooser.util.Preconditions.checkNotNull;

/**
 * @author Zhu Liang
 * @version 1.0
 * @since 2017/4/15 下午1:41
 */

public class ActivityInfosLocalDataSource implements ActivityInfosDataSource {

    @Nullable
    private static ActivityInfosLocalDataSource INSTANCE;
    private BriteDatabase mBriteDatabase;

    private ActivityInfosLocalDataSource(@NonNull Context context,
                                         @NonNull BaseSchedulerProvider schedulerProvider) {
        checkNotNull(context);
        checkNotNull(schedulerProvider);
        SqlBrite sqlBrite = new SqlBrite.Builder().build();
        mBriteDatabase = sqlBrite.wrapDatabaseHelper(new AppChooserDbHelper(context),
                schedulerProvider.io());
    }

    public static ActivityInfosLocalDataSource getInstance(@NonNull Context context,
                                                           @NonNull BaseSchedulerProvider schedulerProvider) {
        if (INSTANCE == null) {
            synchronized (ActivityInfosLocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ActivityInfosLocalDataSource(context, schedulerProvider);
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
        ContentValues values = new ContentValues();
        values.put(MIME_TYPE, activityInfo.getMimeType());
        values.put(PKG, activityInfo.getPkg());
        values.put(CLS, activityInfo.getCls());
        mBriteDatabase.insert(TABLE_NAME, values, CONFLICT_REPLACE);
    }

    @NonNull
    @Override
    public Observable<ActivityInfo> getActivityInfo(@Nullable String mimeType) {
        return mBriteDatabase.createQuery(TABLE_NAME, "select * from " + TABLE_NAME + " where " +
                MIME_TYPE + "=?", mimeType)
                .mapToOneOrDefault(new Func1<Cursor, ActivityInfo>() {
                    @Override
                    public ActivityInfo call(Cursor cursor) {
                        String mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MIME_TYPE));
                        String pkg = cursor.getString(cursor.getColumnIndexOrThrow(PKG));
                        String cls = cursor.getString(cursor.getColumnIndexOrThrow(CLS));
                        return new ActivityInfo(mimeType, pkg, cls);
                    }
                }, null)
                .limit(1);
    }

    @Override
    public int deleteActivityInfo(@Nullable String mimeType) {
        if (mimeType == null) {
            return 0;
        }
        return mBriteDatabase.delete(TABLE_NAME, MIME_TYPE + "=?", mimeType);
    }

    @Override
    public int deleteAllActivityInfos() {
        return mBriteDatabase.delete(TABLE_NAME, null);
    }
}
