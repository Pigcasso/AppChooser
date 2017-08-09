package io.julian.appchooser.module.mediatypes;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import io.julian.appchooser.Injection;
import io.julian.appchooser.R;
import io.julian.appchooser.data.MediaType;
import io.julian.appchooser.module.base.FragmentCallback;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;

/**
 * @author Zhu Liang
 * @version 1.0
 * @since 2017/4/15 下午5:48
 */

public class MediaTypesDelegate implements FragmentCallback, MediaTypesContract.View {

    private Context mContext;
    private MediaTypesContract.Presenter mPresenter;
    private MediaTypesAdapter mMediaTypesAdapter;

    public MediaTypesDelegate(Context context) {
        mContext = context;
        new MediaTypesPresenter(this, Injection.provideSchedulerProvider(),
                Injection.provideMediaTypesRepository(context));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mMediaTypesAdapter = new MediaTypesAdapter(getContext(), new OnMediaTypesListener() {
            @Override
            public void onMediaType(MediaType mediaType) {
                EventBus.getDefault().post(mediaType);
            }
        });
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.dialog_media_types, null);
        RecyclerView recyclerView = (RecyclerView) contentView.findViewById(R.id.recycler_view_media_types_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mMediaTypesAdapter);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle(R.string.media_types_title)
                .setView(contentView);
        return alertDialog.create();
    }

    @Override
    public void onDestroyView() {
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

    @Override
    public void setPresenter(@NonNull MediaTypesContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showMediaTypes(List<MediaType> mediaTypes) {
        mMediaTypesAdapter.replaceData(mediaTypes);
        mMediaTypesAdapter.notifyDataSetChanged();
    }

    @Nullable
    public Context getContext() {
        return mContext;
    }
}
