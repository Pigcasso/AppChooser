package io.julian.appchooser.module.resolvers;

import io.julian.mvp.BasePresenter;
import io.julian.mvp.BaseView;

/**
 * @author Zhu Liang
 * @version 1.0
 * @since 2017/4/16 上午11:37
 */

interface ResolversContract {

    interface View extends BaseView<Presenter> {

    }

    interface Presenter extends BasePresenter {
    }
}
