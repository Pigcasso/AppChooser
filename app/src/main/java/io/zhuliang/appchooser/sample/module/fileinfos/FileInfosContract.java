package io.zhuliang.appchooser.sample.module.fileinfos;

import java.util.List;

import io.zhuliang.appchooser.sample.data.FileInfo;
import io.zhuliang.appchooser.sample.module.base.BasePresenter;
import io.zhuliang.appchooser.sample.module.base.BaseView;

public interface FileInfosContract {

    interface View extends BaseView<Presenter> {
        void showFileInfos(List<FileInfo> fileInfos);

        void showNoFileInfos();
    }

    interface Presenter extends BasePresenter {
        void loadFileInfos();
    }
}
