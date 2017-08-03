package io.julian.appchooser;

import java.io.File;
import java.util.List;

import io.julian.appchooser.data.ActivityInfo;
import io.julian.appchooser.data.MediaType;
import io.julian.appchooser.data.Resolver;
import io.julian.appchooser.exception.AppChooserException;
import io.julian.mvp.BasePresenter;
import io.julian.mvp.BaseView;

/**
 * @author Zhu Liang
 * @version 1.0
 * @since 2017/4/25 上午10:49
 */

interface AppChooserContract {

    int DEFAULT_REQUEST_CODE = -1;
    String DEFAULT_MIME_TYPE = "*/*";

    interface View extends BaseView<Presenter> {

        void showFileContent(ActivityInfo activityInfo, File file) throws AppChooserException;

        void showMediaTypes();

        void showResolvers(List<Resolver> resolvers);

        void hideResolvers();

        void showFileContentError(File file);

        void showNoResolvers(MediaType mediaType);
    }

    interface Presenter extends BasePresenter {
        void loadActivityInfo(String mimeType);

        void loadResolvers(String mimeType);

        void loadResolvers(MediaType mediaType);

        void loadResolver(Resolver resolver);

        void cleanAllActivityInfos();
    }
}
