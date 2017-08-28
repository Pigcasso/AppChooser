package io.julian.appchooser.module.resolvers;

import android.support.annotation.NonNull;

import java.io.File;
import java.util.List;

import io.julian.appchooser.data.ActivityInfo;
import io.julian.appchooser.data.MediaType;
import io.julian.appchooser.data.Resolver;
import io.julian.appchooser.exception.AppChooserException;
import io.julian.mvp.BaseDialogView;
import io.julian.mvp.BasePresenter;

/**
 * @author Zhu Liang
 */

public interface ResolversContract {

    interface View extends BaseDialogView<Presenter> {

        void showFileContent(ActivityInfo activityInfo, File file, int requestCode) throws AppChooserException;

        void showMediaTypes(List<MediaType> mediaTypes);

        void showResolvers(List<Resolver> resolvers);

        void showFileContentError(File file);

        void showNoResolvers(MediaType mediaType);

    }

    interface Presenter extends BasePresenter {

        void loadActivityInfo(@NonNull String mimeType);

        void loadMediaTypes();

        void loadResolvers(@NonNull String mimeType);

        void loadResolvers(@NonNull MediaType mediaType);

        void loadResolver(@NonNull Resolver resolver);
    }

}
