package io.julian.appchooser.sample.data;

import android.support.annotation.NonNull;

import java.io.File;

import io.julian.appchooser.util.Preconditions;

/**
 * @author Zhu Liang
 * @version 1.0
 * @since 2017/5/13 下午2:34
 */

public class FileInfo {

    @NonNull
    private File mFile;

    public FileInfo(String pathname) {
        this(new File(pathname));
    }

    public FileInfo(@NonNull File file) {
        mFile = Preconditions.checkNotNull(file);
    }

    public boolean isDirectory() {
        return mFile.isDirectory();
    }

    public boolean isFile() {
        return mFile.isFile();
    }

    public String getName() {
        return mFile.getName();
    }

    public String getAbsolutePath() {
        return mFile.getAbsolutePath();
    }
}
