package io.julian.appchooser.module.resolvers;

import io.julian.appchooser.data.Resolver;
import io.julian.appchooser.module.base.BasePresenter;
import io.julian.appchooser.module.base.BaseView;

/**
 * @author Zhu Liang
 * @version 1.0
 * @since 2017/4/16 上午11:37
 */

public interface ResolversContract {

    interface View extends BaseView<Presenter> {
        
    }

    interface Presenter extends BasePresenter {
    }
}
