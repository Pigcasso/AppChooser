package io.julian.appchooser.sample.module.fileinfos;

import java.util.List;

import io.julian.appchooser.sample.data.FileInfo;
import io.julian.appchooser.sample.module.base.BasePresenter;
import io.julian.appchooser.sample.module.base.BaseView;

public interface FileInfosContract {

    interface View extends BaseView<Presenter> {
        void showFileInfos(List<FileInfo> fileInfos);

        void showNoFileInfos();
    }

    interface Presenter extends BasePresenter {
        void loadFileInfos();
    }
}
