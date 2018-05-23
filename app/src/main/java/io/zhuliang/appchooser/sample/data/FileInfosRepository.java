package io.zhuliang.appchooser.sample.data;

import android.support.annotation.NonNull;

import java.io.File;
import java.util.List;

import io.julian.common.Preconditions;
import io.julian.mvp.util.schedulers.BaseSchedulerProvider;
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
                        File[] files = dir.listFiles();
                        if (files == null) {
                            files = new File[0];
                        }
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
