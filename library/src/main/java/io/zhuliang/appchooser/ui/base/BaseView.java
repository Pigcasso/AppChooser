package io.zhuliang.appchooser.ui.base;

import androidx.annotation.NonNull;

public interface BaseView<P extends BasePresenter> {

    void setPresenter(@NonNull P presenter);
}
