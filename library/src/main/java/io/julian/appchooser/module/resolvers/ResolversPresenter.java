package io.julian.appchooser.module.resolvers;

import android.support.annotation.NonNull;

import static io.julian.appchooser.util.Preconditions.checkNotNull;

/**
 * @author Zhu Liang
 * @version 1.0
 * @since 2017/4/16 下午12:01
 */
public class ResolversPresenter implements ResolversContract.Presenter {
    private ResolversContract.View mView;

    public ResolversPresenter(@NonNull ResolversContract.View view) {
        mView = checkNotNull(view);

        mView.setPresenter(this);
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {

    }
}
