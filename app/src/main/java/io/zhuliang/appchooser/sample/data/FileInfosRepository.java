package io.zhuliang.appchooser.sample.data;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import io.zhuliang.appchooser.internal.Preconditions;
import io.zhuliang.appchooser.sample.util.schedulers.BaseSchedulerProvider;
import rx.Observable;
import rx.functions.Func1;

/**
 * @author Zhu Liang
 * @version 1.0
 * @since 2017/5/13 下午3:00
 */

public class FileInfosRepository implements FileInfosDataSource {

    @NonNull
    private BaseSchedulerProvider mSchedulerProvider;

    public FileInfosRepository(@NonNull BaseSchedulerProvider schedulerProvider) {
        mSchedulerProvider = Preconditions.checkNotNull(schedulerProvider, "schedulerProvider == null");
    }

    @Override
    public Observable<List<FileInfo>> listFileInfos(@NonNull FileInfo directory) {
        Preconditions.checkNotNull(directory);
        Preconditions.checkArgument(directory.isDirectory());
        return Observable.just(directory)
                .subscribeOn(mSchedulerProvider.io())
                .map(new Func1<FileInfo, File>() {
                    @Override
                    public File call(FileInfo fileInfo) {
                        return new File(fileInfo.getAbsolutePath());
                    }
                })
                .flatMap(new Func1<File, Observable<File>>() {
                    @Override
                    public Observable<File> call(File dir) {
                        File[] files = dir.listFiles(new FileFilter() {
                            @Override
                            public boolean accept(File pathname) {
                                return !pathname.isHidden();
                            }
                        });
                        if (files == null) {
                            files = new File[0];
                        }
                        Arrays.sort(files, new Comparator<File>() {
                            @Override
                            public int compare(File o1, File o2) {
                                return o1.getName().compareToIgnoreCase(o2.getName());
                            }
                        });
                        return Observable.from(files);
                    }
                })
                .map(new Func1<File, FileInfo>() {
                    @Override
                    public FileInfo call(File file) {
                        return new FileInfo(file);
                    }
                })
                .toList();
    }
}
