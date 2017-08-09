package io.julian.appchooser.module.resolvers;

import android.support.annotation.NonNull;

import static io.julian.common.Preconditions.checkNotNull;

/**
 * @author Zhu Liang
 * @version 1.0
 * @since 2017/4/16 下午12:01
 */
class ResolversPresenter implements ResolversContract.Presenter {
    ResolversPresenter(@NonNull ResolversContract.View view) {
        checkNotNull(view);

        view.setPresenter(this);
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {

    }
}
