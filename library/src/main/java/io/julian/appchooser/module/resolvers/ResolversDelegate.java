package io.julian.appchooser.module.resolvers;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import io.julian.appchooser.R;
import io.julian.appchooser.data.Resolver;
import io.julian.appchooser.module.base.FragmentCallback;

import static io.julian.appchooser.util.Preconditions.checkNotNull;

/**
 * @author Zhu Liang
 * @version 1.0
 * @since 2017/4/16 上午11:37
 */

public class ResolversDelegate implements FragmentCallback, ResolversContract.View {

    private ResolversContract.Presenter mPresenter;
    private View mViewRoot;
    private ResolversAdapter mAdapter;
    @Nullable
    private Context mContext;
    private List<Resolver> mDatas;

    public ResolversDelegate(@NonNull Context context, @Nullable ArrayList<Resolver> resolvers) {
        mContext = checkNotNull(context);
        mDatas = checkNotNull(resolvers);

        new ResolversPresenter(this);
    }

    @Override
    public void setPresenter(@NonNull ResolversContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mAdapter = new ResolversAdapter(getContext(), mDatas, new OnResolversListener() {
            @Override
            public void onResolver(Resolver resolver) {
                if (getView() == null) return;
                CheckBox checkBox = (CheckBox) getView().findViewById(R.id.check_box_resolvers_set_as_default);
                resolver.setDefault(checkBox.isChecked());
                EventBus.getDefault().post(resolver);
            }
        });
    }

    @Override
    public void onDestroy() {
        mContext = null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewRoot = inflater.inflate(R.layout.dialog_resolvers, container, false);
        ((TextView)mViewRoot.findViewById(R.id.alertTitle)).setText(R.string.resolvers_title);
        RecyclerView recyclerView = (RecyclerView) mViewRoot.findViewById(R.id.recycler_view_resolvers_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);
        return mViewRoot;
    }

    @Override
    public void onDestroyView() {
        mViewRoot = null;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onResume() {
        mPresenter.subscribe();
    }

    @Override
    public void onPause() {
        mPresenter.unsubscribe();
    }

    @Nullable
    public View getView() {
        return mViewRoot;
    }

    @Nullable
    public Context getContext() {
        return mContext;
    }
}
