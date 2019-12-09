package io.zhuliang.appchooser.sample.module.fileinfos;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.zhuliang.appchooser.BuildConfig;
import io.zhuliang.appchooser.internal.Preconditions;
import io.zhuliang.appchooser.sample.R;
import io.zhuliang.appchooser.sample.SampleInjection;
import io.zhuliang.appchooser.sample.data.FileInfo;
import io.zhuliang.appchooser.ui.base.CommonAdapter;

public class FileInfosFragment extends Fragment implements FileInfosContract.View {

    private static final String EXTRA_ABSOLUTE_PATH = BuildConfig.APPLICATION_ID + ".extra.ABSOLUTE_PATH";
    private FileInfosContract.Presenter mPresenter;
    private FileInfosAdapter mAdapter;
    private OnItemClickListener mOnItemClickListener;

    static FileInfosFragment newInstance(@NonNull String absolutePath) {
        Preconditions.checkNotNull(absolutePath);
        Bundle args = new Bundle();
        args.putString(EXTRA_ABSOLUTE_PATH, absolutePath);

        FileInfosFragment fragment = new FileInfosFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnItemClickListener) {
            mOnItemClickListener = (OnItemClickListener) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = Preconditions.checkNotNull(getArguments());
        String absolutePath = arguments.getString(EXTRA_ABSOLUTE_PATH);

        if (TextUtils.isEmpty(absolutePath)) {
            throw new IllegalStateException("Absolute path is null");
        }
        // Create the presenter
        new FileInfosPresenter(new FileInfo(new File(absolutePath)),
                SampleInjection.provideSchedulerProvider(),
                this,
                SampleInjection.provideFileInfoRepository());
        mAdapter = new FileInfosAdapter(getContext(), new ArrayList<FileInfo>());
        mAdapter.setOnItemClickListener(new CommonAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Object item, int position) {
                mOnItemClickListener.onItemClick((FileInfo) item);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_file_infos, container, false);
        RecyclerView recyclerView = root.findViewById(R.id.recycler_view_file_infos);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAdapter);
        recyclerView.hasFixedSize();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
    }

    @Override
    public void setPresenter(FileInfosContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showFileInfos(List<FileInfo> fileInfos) {
        if (getView() == null) {
            return;
        }
        mAdapter.replaceDatas(fileInfos);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showNoFileInfos() {
        mAdapter.replaceDatas(new ArrayList<FileInfo>());
        mAdapter.notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(FileInfo fileInfo);
    }
}
