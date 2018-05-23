package io.zhuliang.appchooser.util;

import android.net.Uri;
import android.webkit.MimeTypeMap;

import java.io.File;

/**
 * @author Zhu Liang
 * @version 1.0
 * @since 2017/4/16 上午12:16
 */
public class MimeTypeUtils {

    private MimeTypeUtils() {
        throw new AssertionError();
    }

    public static String getMimeType(File file) {
        String extension;
        extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
        if (extension != null) {
            extension = extension.toLowerCase();
        }
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }
}