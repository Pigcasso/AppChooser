package io.zhuliang.appchooser.util;

import androidx.annotation.NonNull;

import java.io.File;

/**
 * @author Zhu Liang
 */

public final class FileUtils {
    public static void checkFile(@NonNull File file) {
        if (file == null) {
            throw new IllegalArgumentException("file == null");
        }
        if (!file.exists()) {
            throw new IllegalArgumentException(file + " not found");
        }
        if (!file.isFile()) {
            throw new IllegalArgumentException(file + " is not file");
        }
    }
}
