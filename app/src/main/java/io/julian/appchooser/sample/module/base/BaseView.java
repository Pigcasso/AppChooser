package io.julian.appchooser.sample.module.base;

/**
 * @author Zhu Liang
 * @version 1.0
 * @since 2017/5/13 下午2:46
 */

public interface BaseView<P extends BasePresenter> {

    void setPresenter(P presenter);
}
