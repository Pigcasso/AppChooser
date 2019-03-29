package io.zhuliang.appchooser.util;

import android.net.Uri;
import androidx.annotation.NonNull;
import android.webkit.MimeTypeMap;

import java.io.File;

/**
 * @author Zhu Liang
 */

public final class MimeType {

    public static final String ALL = "*/*";
    public static final String TEXT_PLAIN = "text/plain";

    public static String getMimeType(@NonNull File file) {
        FileUtils.checkFile(file);
        String extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }
}
