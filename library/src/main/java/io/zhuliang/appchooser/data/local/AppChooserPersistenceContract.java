package io.zhuliang.appchooser.data.local;

import android.provider.BaseColumns;

/**
 * @author Zhu Liang
 * @version 1.0
 * @since 2017/4/15 下午2:50
 */

public class AppChooserPersistenceContract {

    public static final String TEXT_TYPE = " TEXT";

    public static final String BOOLEAN_TYPE = " INTEGER";

    public static final String COMMA_SEP = ",";

    public interface ActivityInfoEntity extends BaseColumns {

        String TABLE_NAME = "activityInfo";
        String MIME_TYPE = "mimeType";
        String CLS = "cls";
        String PKG = "pkg";
    }
}
