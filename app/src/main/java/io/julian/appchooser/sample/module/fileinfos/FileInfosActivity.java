package io.julian.appchooser.sample.module.fileinfos;

import android.Manifest;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import io.julian.appchooser.AppChooser;
import io.julian.appchooser.sample.R;
import io.julian.appchooser.sample.data.FileInfo;

public class FileInfosActivity extends AppCompatActivity {
    private static final String TAG = FileInfosActivity.class.getSimpleName();
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 110;
    private static final String EXTRA_SELECTED_DIRECTORy = "extra_selected_directory";
    private static final String EXTRA_DIRECTORIES = "extra_directories";

    private AppChooser mAppChooser;

    ComponentName[] excluded = new ComponentName[]{
            new ComponentName("nutstore.android", "nutstore.android.SendToNutstoreIndex"),
            new ComponentName("nutstore.android.debug", "nutstore.android.SendToNutstoreIndex"),
    };

    private ArrayList<FileInfo> mDirectories = new ArrayList<>();
    private FileInfo mSelectedDirectory;

    private FragmentManager mFragmentManager;

    private ArrayAdapter<FileInfo> mAdapter;
    private Spinner mSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mDirectories = savedInstanceState.getParcelableArrayList(EXTRA_DIRECTORIES);
            mSelectedDirectory = savedInstanceState.getParcelable(EXTRA_SELECTED_DIRECTORy);
        }

        mFragmentManager = getSupportFragmentManager();

        setContentView(R.layout.activity_file_infos);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }

        mSpinner = (Spinner) findViewById(R.id.spinner);
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mDirectories);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(mAdapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                showDirectory(mDirectories.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mAppChooser = AppChooser.with(this).excluded(excluded);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            } else {
                showDirectory(mSelectedDirectory);
            }
        } else {
            showDirectory(mSelectedDirectory);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(EXTRA_DIRECTORIES, mDirectories);
        outState.putParcelable(EXTRA_SELECTED_DIRECTORy, mSelectedDirectory);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        mAppChooser.bind();
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        mAppChooser.unbind();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            int grantResult = grantResults[0];
            boolean granted = grantResult == PackageManager.PERMISSION_GRANTED;
            Log.i(TAG, "onRequestPermissionsResult granted=" + granted);
            if (granted) {
                showDirectory(mSelectedDirectory);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_file_infos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_file_infos_clear_defaults:
                mAppChooser.cleanDefaults();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        int selectedPosition = mDirectories.indexOf(mSelectedDirectory);
        if (selectedPosition == 0) {
            super.onBackPressed();
        } else {
            int previousPosiotn = selectedPosition - 1;
            FileInfo previousDirectory = mDirectories.get(previousPosiotn);
            showDirectory(previousDirectory);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFileInfo(FileInfo fileInfo) {
        if (fileInfo.isFile()) {
            showFile(fileInfo);
        } else {
            showDirectory(fileInfo);
        }
    }

    private void showFile(FileInfo file) {
        mAppChooser.file(new File(file.getAbsolutePath())).load();
    }

    private void showDirectory(FileInfo fileInfo) {
        if (fileInfo == null) {
            fileInfo = new FileInfo(Environment.getExternalStorageDirectory());
        }
        if (mSelectedDirectory != fileInfo) {
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            if (mSelectedDirectory != null) {
                Fragment f = mFragmentManager.findFragmentByTag(mSelectedDirectory.getAbsolutePath());
                if (f != null) {
                    ft.detach(f);
                }
            }
            Fragment f = mFragmentManager.findFragmentByTag(fileInfo.getAbsolutePath());
            if (f == null) {
                f = FileInfosFragment.newInstance(fileInfo.getAbsolutePath());
                ft.add(R.id.contentFrame, f, fileInfo.getAbsolutePath());
            } else {
                ft.attach(f);
            }

            String selectedPath = fileInfo.getAbsolutePath();
            Iterator<FileInfo> iterator = mDirectories.iterator();
            while (iterator.hasNext()) {
                FileInfo info = iterator.next();
                String path = info.getAbsolutePath();
                if (!path.equals(selectedPath)
                        && path.indexOf(selectedPath) == 0) {
                    Fragment removedFragment = mFragmentManager.findFragmentByTag(path);
                    if (removedFragment != null) {
                        ft.remove(removedFragment);
                    }
                    iterator.remove();
                }
            }

            if (!mDirectories.contains(fileInfo)) {
                mDirectories.add(fileInfo);
            }

            ft.commit();
            mSelectedDirectory = fileInfo;

            mAdapter.notifyDataSetChanged();

            mSpinner.setSelection(mDirectories.indexOf(fileInfo));
        }
    }
}
