package io.zhuliang.appchooser.ui.view;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import io.zhuliang.appchooser.data.MediaType;
import io.zhuliang.appchooser.ui.resolveinfos.ResolveInfosContract;

/**
 * @author Zhu Liang
 */

interface ViewContract {

    interface View extends ResolveInfosContract.View<Presenter> {

        void showActivity(Intent intent, int requestCode, boolean fromActivity)
                throws ActivityNotFoundException;

        void showMediaTypes(List<MediaType> mediaTypes);

        boolean isAsDefault();

        @Nullable
        Context getContext();

    }

    interface Presenter extends ResolveInfosContract.Presenter {
        void loadActivityInfo();

        void loadMediaTypes();

        void openMediaType(@NonNull MediaType mediaType);
    }
}
