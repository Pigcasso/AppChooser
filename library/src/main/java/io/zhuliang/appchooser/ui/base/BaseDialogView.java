package io.zhuliang.appchooser.ui.base;

/**
 * @author Zhu Liang
 */

public interface BaseDialogView<P extends BasePresenter> extends BaseView<P> {

    void dismissDialog();

}
