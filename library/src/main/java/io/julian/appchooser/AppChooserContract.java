package io.julian.appchooser;

import io.julian.mvp.BasePresenter;
import io.julian.mvp.BaseView;

/**
 * @author Zhu Liang
 * @version 1.0
 * @since 2017/4/25 上午10:49
 */

interface AppChooserContract {

    interface View extends BaseView<Presenter> {
        void showDisplayings();
    }

    interface Presenter extends BasePresenter {
        void cleanAllActivityInfos();
    }
}
