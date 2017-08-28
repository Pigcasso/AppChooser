package io.julian.appchooser.module.resolvers.v4;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.julian.appchooser.Injection;
import io.julian.appchooser.R;
import io.julian.appchooser.data.ActivityInfo;
import io.julian.appchooser.data.MediaType;
import io.julian.appchooser.data.Resolver;
import io.julian.appchooser.exception.AppChooserException;
import io.julian.appchooser.module.resolvers.MediaTypesAdapter;
import io.julian.appchooser.module.resolvers.OnMediaTypesListener;
import io.julian.appchooser.module.resolvers.OnResolversListener;
import io.julian.appchooser.module.resolvers.ResolversAdapter;
import io.julian.appchooser.module.resolvers.ResolversConsts;
import io.julian.appchooser.module.resolvers.ResolversContract;
import io.julian.appchooser.module.resolvers.ResolversPresenter;
import io.julian.appchooser.util.MimeTypeUtils;
import io.julian.mvp.v4.BaseDialogFragment;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;

/**
 * @author Zhu Liang
 */

public class ResolversFragment extends BaseDialogFragment<ResolversContract.Presenter> implements ResolversContract.View {

    private static final String EXTRA_FILE = "extra.path";
    private static final String EXTRA_REQUEST_CODE = "extra.request_code";
    private static final String EXTRA_EXCLUDED = "extra.EXCLUDED";
    private static final String EXTRA_MIME_TYPE = "extra.MIME_TYPE";

    private RecyclerView mRecyclerView;
    private FrameLayout mAsDefaultContainer;
    private CheckBox mAsDefaultCheckBox;

    public static ResolversFragment newInstance(File file, int requestCode,
                                                ArrayList<ComponentName> excluded) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_FILE, file);
        args.putInt(EXTRA_REQUEST_CODE, requestCode);
        args.putParcelableArrayList(EXTRA_EXCLUDED, excluded);
        ResolversFragment fragment = new ResolversFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (getArguments() != null) {
            Bundle args = getArguments();

            File file = (File) args.getSerializable(EXTRA_FILE);
            if (file == null) {
                throw new NullPointerException("file == null");
            }
            String mimeType = args.getString(EXTRA_MIME_TYPE);
            if (mimeType == null) {
                mimeType = MimeTypeUtils.getMimeType(file);
            }
            int requestCode = args.getInt(EXTRA_REQUEST_CODE);
            ArrayList<ComponentName> excluded = args.getParcelableArrayList(EXTRA_EXCLUDED);

            new ResolversPresenter(this, Injection.provideSchedulerProvider(),
                    file, mimeType, requestCode, excluded,
                    Injection.provideActivityInfosRepository(activity),
                    Injection.provideMediaTypesRepository(activity),
                    Injection.providerResolversRepository(activity));
        } else {
            throw new NullPointerException("Not found arguments");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_resolvers, null);
        mAsDefaultContainer = (FrameLayout) contentView.findViewById(R.id.frame_resolvers_check_container);
        mAsDefaultCheckBox = (CheckBox) contentView.findViewById(R.id.check_resolvers_set_as_default);
        mRecyclerView = (RecyclerView) contentView.findViewById(R.id.recycler_resolvers_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(R.string.media_types_title)
                .setView(contentView);
        return alertDialog.create();
    }

    @Override
    public void showFileContent(ActivityInfo activityInfo, File file,
                                int requestCode) throws AppChooserException {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setComponent(new ComponentName(activityInfo.getPkg(), activityInfo.getCls()));
        intent.setDataAndType(Uri.fromFile(file), activityInfo.getMimeType());
        ComponentName componentName = intent.resolveActivity(getActivity().getPackageManager());
        if (componentName != null) {
            try {
                if (requestCode == ResolversConsts.DEFAULT_REQUEST_CODE) {
                    getActivity().startActivity(intent);
                } else {
                    getActivity().startActivityForResult(intent, requestCode);
                }
            } catch (ActivityNotFoundException e) {
                throw new AppChooserException(e);
            }
        } else {
            throw new AppChooserException();
        }
    }

    @Override
    public void showMediaTypes(List<MediaType> mediaTypes) {
        getDialog().setTitle(R.string.media_types_title);
        mAsDefaultContainer.setVisibility(View.GONE);
        mRecyclerView.setAdapter(new MediaTypesAdapter(getActivity(), mediaTypes,
                new OnMediaTypesListener() {
                    @Override
                    public void onMediaType(MediaType mediaType) {
                        // 保存用户选择的"媒体类型"，避免用户旋转屏幕后，仍然显示"媒体类型选择器"
                        getArguments().putString(EXTRA_MIME_TYPE, mediaType.getMimeType());
                        mPresenter.loadResolvers(mediaType);
                    }
                }));
    }

    @Override
    public void showResolvers(List<Resolver> resolvers) {
        getDialog().setTitle(R.string.resolvers_title);
        mAsDefaultContainer.setVisibility(View.VISIBLE);
        mRecyclerView.setAdapter(new ResolversAdapter(getActivity(), resolvers,
                new OnResolversListener() {
                    @Override
                    public void onResolver(Resolver resolver) {
                        resolver.setDefault(mAsDefaultCheckBox.isChecked());
                        mPresenter.loadResolver(resolver);
                    }
                }));
    }

    @Override
    public void showFileContentError(File file) {
        Toast.makeText(getActivity(), getString(R.string.app_chooser_failed_to_open_file, file.getName()), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showNoResolvers(MediaType mediaType) {
        Toast.makeText(getActivity(),
                getString(R.string.app_chooser_did_not_find_an_app_that_can_open_this_file,
                        mediaType.getDisplayName()), Toast.LENGTH_SHORT).show();

    }
}
