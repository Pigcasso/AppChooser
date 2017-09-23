package io.julian.appchooser.ui.send;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import io.julian.appchooser.BuildConfig;
import io.julian.appchooser.Injection;
import io.julian.appchooser.R;
import io.julian.appchooser.action.ActionConfig;
import io.julian.appchooser.data.ResolveInfosRepository;
import io.julian.appchooser.ui.resolveinfos.ResolveInfosFragment;

/**
 * @author Zhu Liang
 */

public class SendFragment extends ResolveInfosFragment<SendContract.Presenter>
        implements SendContract.View {

    private static final String EXTRA_ACTION_CONFIG = BuildConfig.APPLICATION_ID + ".fragment.extra.ACTION_CONFIG";

    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;

    public static SendFragment newInstance(ActionConfig actionConfig) {
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_ACTION_CONFIG, actionConfig);
        SendFragment fragment = new SendFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionConfig actionConfig = null;
        if (getArguments() != null) {
            actionConfig = getArguments().getParcelable(EXTRA_ACTION_CONFIG);
        }
        if (actionConfig == null) {
            throw new NullPointerException("actionConfig == null");
        }
        new SendPresenter(this,
                Injection.provideSchedulerProvider(), actionConfig,
                new ResolveInfosRepository(getContext(), Injection.provideSchedulerProvider()));
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getContext();
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_send, null);
        mRecyclerView = (RecyclerView) contentView.findViewById(R.id.recycler_view);
        mProgressBar = (ProgressBar) contentView.findViewById(R.id.progress_bar);
        AlertDialog.Builder bottomSheetDialog = new AlertDialog.Builder(context);
        bottomSheetDialog.setView(contentView)
                .setTitle(R.string.send_title);
        return bottomSheetDialog.create();
    }

    @Override
    protected RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    @Override
    protected ProgressBar getProgressBar() {
        return mProgressBar;
    }

    @Override
    public void showNoResolveInfos() {
        Toast.makeText(getActivity(), getString(R.string.send_no_apps_can_share_text),
                Toast.LENGTH_SHORT).show();
    }
}
