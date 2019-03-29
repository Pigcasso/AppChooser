package io.zhuliang.appchooser.sample.module.fileinfos;

import androidx.annotation.NonNull;

import java.util.List;

import io.zhuliang.appchooser.sample.data.FileInfo;
import io.zhuliang.appchooser.sample.data.FileInfosRepository;
import io.zhuliang.appchooser.util.schedulers.BaseSchedulerProvider;
import rx.functions.Action1;

import static io.zhuliang.appchooser.internal.Preconditions.checkArgument;
import static io.zhuliang.appchooser.internal.Preconditions.checkNotNull;

class FileInfosPresenter implements FileInfosContract.Presenter {

    @NonNull
    private final FileInfo mDirectory;
    @NonNull
    private final BaseSchedulerProvider mSchedulerProvider;
    @NonNull
    private final FileInfosRepository mFileInfosRepository;
    @NonNull
    private FileInfosContract.View mView;

    FileInfosPresenter(@NonNull FileInfo directory,
                       @NonNull BaseSchedulerProvider schedulerProvider,
                       @NonNull FileInfosContract.View view,
                       @NonNull FileInfosRepository fileInfosRepository) {
        checkNotNull(directory);
        checkArgument(directory.isDirectory());
        mDirectory = directory;
        mSchedulerProvider = checkNotNull(schedulerProvider);
        mView = checkNotNull(view);
        mFileInfosRepository = checkNotNull(fileInfosRepository);

        mView.setPresenter(this);
    }

    @Override
    public void subscribe() {
        loadFileInfos();
    }

    @Override
    public void unsubscribe() {

    }

    @Override
    public void loadFileInfos() {
        mFileInfosRepository
                .listFileInfos(mDirectory)
                .observeOn(mSchedulerProvider.ui())
                .subscribe(new Action1<List<FileInfo>>() {
                    @Override
                    public void call(List<FileInfo> fileInfos) {
                        if (fileInfos.isEmpty()) {
                            mView.showNoFileInfos();
                        } else {
                            mView.showFileInfos(fileInfos);
                        }
                    }
                });
    }
}
