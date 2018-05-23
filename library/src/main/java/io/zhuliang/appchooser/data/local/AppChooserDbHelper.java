package io.zhuliang.appchooser.data.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import io.zhuliang.appchooser.data.local.AppChooserPersistenceContract.ActivityInfoEntity;

import static io.zhuliang.appchooser.data.local.AppChooserPersistenceContract.COMMA_SEP;
import static io.zhuliang.appchooser.data.local.AppChooserPersistenceContract.TEXT_TYPE;

/**
 * @author Zhu Liang
 * @version 1.0
 * @since 2017/4/15 下午2:46
 */

public class AppChooserDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "appchooser.db";
    private static final int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_ENTITY =
            "CREATE TABLE " + ActivityInfoEntity.TABLE_NAME + "(" +
                    ActivityInfoEntity.MIME_TYPE + TEXT_TYPE + " PRIMARY KEY" + COMMA_SEP +
                    ActivityInfoEntity.PKG + TEXT_TYPE + " NOT NULL" + COMMA_SEP +
                    ActivityInfoEntity.CLS + TEXT_TYPE + " NOT NULL" +
                    ")";

    public AppChooserDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTITY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
