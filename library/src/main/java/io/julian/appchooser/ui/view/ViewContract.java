package io.julian.appchooser.ui.view;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.support.annotation.NonNull;

import java.util.List;

import io.julian.appchooser.data.MediaType;
import io.julian.appchooser.ui.resolveinfos.ResolveInfosContract;

/**
 * @author Zhu Liang
 */

interface ViewContract {

    interface View extends ResolveInfosContract.View<Presenter> {

        void showActivity(Intent intent, int requestCode, boolean fromActivity)
                throws ActivityNotFoundException;

        void showMediaTypes(List<MediaType> mediaTypes);

        boolean isAsDefault();

    }

    interface Presenter extends ResolveInfosContract.Presenter {
        void loadActivityInfo();

        void loadMediaTypes();

        void openMediaType(@NonNull MediaType mediaType);
    }
}
