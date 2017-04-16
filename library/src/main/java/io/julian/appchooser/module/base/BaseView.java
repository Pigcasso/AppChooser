package io.julian.appchooser.module.base;

import android.support.annotation.NonNull;

/**
 * @author Zhu Liang
 * @version 1.0
 * @since 2017/4/15 下午5:44
 */

public interface BaseView<T extends BasePresenter> {
    void setPresenter(@NonNull T presenter);
}
