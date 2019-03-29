package io.zhuliang.appchooser.ui.resolveinfos;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import androidx.annotation.NonNull;

import java.util.List;

import io.zhuliang.appchooser.ui.base.BaseDialogView;
import io.zhuliang.appchooser.ui.base.BasePresenter;

/**
 * @author Zhu Liang
 */

public interface ResolveInfosContract {

    interface View<P extends Presenter> extends BaseDialogView<P> {

        void setLoadingIndicator(boolean active);

        void showResolveInfos(@NonNull List<ResolveInfo> resolveInfos);

        void showNoResolveInfos();

        void showActivity(Intent intent, boolean fromActivity) throws ActivityNotFoundException;
    }

    interface Presenter extends BasePresenter {

        void loadResolveInfos();

        void openResolveInfo(@NonNull ResolveInfo resolveInfo);
    }
}
