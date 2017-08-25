package io.julian.appchooser.sample.module.fileinfos;

import android.Manifest;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;

import io.julian.appchooser.AppChooser;
import io.julian.appchooser.sample.R;
import io.julian.appchooser.sample.data.FileInfo;
import io.julian.common.Preconditions;

public class FileInfosActivity extends AppCompatActivity {
    private static final String TAG = FileInfosActivity.class.getSimpleName();
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 110;
    private static final String EXTRA_SELECTED_DIRECTORy = "extra_selected_directory";
    private static final String EXTRA_DIRECTORIES = "extra_directories";

    private static final int OPERATION_NONE = 0;
    private static final int OPERATION_BACK_PRESSED = 1;
    private static final int OPERATION_SELECTED_TAB = 2;
    private static final int OPERATION_CLICK_ITEM = 3;

    @IntDef(value = {OPERATION_NONE, OPERATION_BACK_PRESSED, OPERATION_SELECTED_TAB,
            OPERATION_CLICK_ITEM})
    @Retention(RetentionPolicy.SOURCE)
    private @interface Operation {
    }

    private AppChooser mAppChooser;

    ComponentName[] excluded = new ComponentName[]{
            new ComponentName("nutstore.android", "nutstore.android.SendToNutstoreIndex"),
            new ComponentName("nutstore.android.debug", "nutstore.android.SendToNutstoreIndex"),
    };

    private ArrayList<FileInfo> mDirectories = new ArrayList<>();
    private FileInfo mSelectedDirectory;

    private FragmentManager mFragmentManager;

    private TabLayout mTabLayout;

    private MyHandler mMyHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mDirectories = savedInstanceState.getParcelableArrayList(EXTRA_DIRECTORIES);
            mSelectedDirectory = savedInstanceState.getParcelable(EXTRA_SELECTED_DIRECTORy);
        }

        mMyHandler = new MyHandler(this);

        mFragmentManager = getSupportFragmentManager();

        setContentView(R.layout.activity_file_infos);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }

        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                showDirectory((FileInfo) tab.getTag(), OPERATION_SELECTED_TAB);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

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
                showDirectory(null, OPERATION_NONE);
            }
        } else {
            showDirectory(null, OPERATION_NONE);
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
                showDirectory(null, OPERATION_NONE);
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
        showDirectory(null, OPERATION_BACK_PRESSED);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFileInfo(FileInfo fileInfo) {
        if (fileInfo.isFile()) {
            showFile(fileInfo);
        } else {
            showDirectory(fileInfo, OPERATION_CLICK_ITEM);
        }
    }

    private void showFile(FileInfo file) {
        mAppChooser.file(new File(file.getAbsolutePath())).load();
    }

    private void showDirectory(FileInfo fileInfo, @Operation int operation) {
        Log.d(TAG, "showDirectory: " + operation);
        switch (operation) {
            case OPERATION_NONE:
                showDirectoryWithNone();
                break;
            case OPERATION_BACK_PRESSED:
                showDirectoryWithBackPressed();
                break;
            case OPERATION_SELECTED_TAB:
                showDirectoryWithSelectedTab(fileInfo);
                break;
            case OPERATION_CLICK_ITEM:
                showDirectoryWithClickItem(fileInfo);
                break;
        }
    }

    private void showDirectoryWithNone() {
        Log.d(TAG, "showDirectoryWithNone11111: " + (mSelectedDirectory == null ? "null" : mSelectedDirectory.getName()) + ", size: " + mDirectories.size());
        if (mSelectedDirectory == null) {
            mSelectedDirectory = new FileInfo(Environment.getExternalStorageDirectory());
            mDirectories.add(mSelectedDirectory);
        }

        mTabLayout.removeAllTabs();
        final int size = mDirectories.size();
        for (int i = 0; i < size; i++) {
            FileInfo directory = mDirectories.get(i);
            if (i == size - 1) {
                mTabLayout.addTab(mTabLayout.newTab().setCustomView(R.layout.directory_tab_view_without_arrow)
                        .setText(directory.getName()).setTag(directory), false);
            } else {
                mTabLayout.addTab(mTabLayout.newTab().setCustomView(R.layout.directory_tab_view)
                        .setText(directory.getName()).setTag(directory), false);
            }
        }

        FragmentTransaction ft = mFragmentManager.beginTransaction();
        for (FileInfo directory : mDirectories) {
            Fragment fragment = mFragmentManager.findFragmentByTag(directory.getAbsolutePath());
            if (fragment != null && !fragment.isDetached()) {
                if (!mSelectedDirectory.equals(directory)) {
                    ft.detach(fragment);
                }
            }
        }
        Fragment selected = mFragmentManager.findFragmentByTag(mSelectedDirectory.getAbsolutePath());
        if (selected == null) {
            selected = FileInfosFragment.newInstance(mSelectedDirectory.getAbsolutePath());
            ft.add(R.id.contentFrame, selected, mSelectedDirectory.getAbsolutePath());
        } else {
            ft.attach(selected);
        }
        ft.commit();

        Message msg = mMyHandler.obtainMessage();
        msg.arg1 = mDirectories.indexOf(mSelectedDirectory);
        Log.d(TAG, "showDirectoryWithNone22222: " + (mSelectedDirectory == null ? "null" : mSelectedDirectory.getName()) + ", size: " + mDirectories.size());
        mMyHandler.sendMessageDelayed(msg, 100L);
    }

    private void showDirectoryWithBackPressed() {
        Preconditions.checkNotNull(mSelectedDirectory, "mSelectedDirectory == null");
        Preconditions.checkArgument(mDirectories.contains(mSelectedDirectory),
                "mDirectories not contain:" + mSelectedDirectory.getAbsolutePath());
        final int selectedPosition = mDirectories.indexOf(mSelectedDirectory);
        if (selectedPosition == 0) {
            finish();
        } else {
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            Fragment f = mFragmentManager.findFragmentByTag(mSelectedDirectory.getAbsolutePath());
            if (f != null && !f.isDetached()) {
                ft.detach(f);
            }
            int previousPosition = selectedPosition - 1;
            FileInfo previous = mSelectedDirectory = mDirectories.get(previousPosition);
            f = mFragmentManager.findFragmentByTag(previous.getAbsolutePath());
            if (f == null) {
                f = FileInfosFragment.newInstance(previous.getAbsolutePath());
                ft.add(R.id.contentFrame, f, previous.getAbsolutePath());
            } else {
                ft.attach(f);
            }
            ft.commit();

            Message msg = mMyHandler.obtainMessage();
            msg.arg1 = previousPosition;
            mMyHandler.sendMessageDelayed(msg, 100L);
        }
    }

    private void showDirectoryWithSelectedTab(FileInfo fileInfo) {
        Preconditions.checkNotNull(fileInfo, "fileInfo == null");
        Preconditions.checkNotNull(mSelectedDirectory, "mSelectedDirectory == null");
        Log.d(TAG, "showDirectoryWithSelectedTab: " + fileInfo.getName());
        if (mSelectedDirectory != fileInfo) {
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            Fragment f = mFragmentManager.findFragmentByTag(mSelectedDirectory.getAbsolutePath());
            if (f != null && !f.isDetached()) {
                ft.detach(f);
            }

            mSelectedDirectory = fileInfo;

            f = mFragmentManager.findFragmentByTag(fileInfo.getAbsolutePath());
            if (f == null) {
                f = FileInfosFragment.newInstance(fileInfo.getAbsolutePath());
                ft.add(R.id.contentFrame, f, fileInfo.getAbsolutePath());
            } else {
                ft.attach(f);
            }
            ft.commit();
        }
    }

    private void showDirectoryWithClickItem(@NonNull FileInfo fileInfo) {
        Preconditions.checkNotNull(fileInfo, "fileInfo == null");
        // 如果用户当前选中的文件夹已经添加到 mDirectories 中
        String selectedPath = fileInfo.getAbsolutePath();
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        Iterator<FileInfo> iterator = mDirectories.iterator();
        while (iterator.hasNext()) {
            FileInfo child = iterator.next();
            String childPath = child.getAbsolutePath();
            if (!selectedPath.contains(childPath)) {
                Fragment f = mFragmentManager.findFragmentByTag(childPath);
                if (f != null) {
                    ft.remove(f);
                }
                iterator.remove();
                int tabPosition = getPositionForTab(child);
                if (tabPosition != -1) {
                    mTabLayout.removeTabAt(tabPosition);
                }
            }
        }
        if (mDirectories.contains(mSelectedDirectory)) {
            Fragment f = mFragmentManager.findFragmentByTag(mSelectedDirectory.getAbsolutePath());
            if (f != null && !f.isDetached()) {
                ft.detach(f);
            }
        } else {
            Fragment f = mFragmentManager.findFragmentByTag(mSelectedDirectory.getAbsolutePath());
            if (f != null) {
                ft.remove(f);
            }
        }

        if (!mDirectories.contains(fileInfo)) {
            int insertedPosition = mDirectories.size();
            if (insertedPosition > 0) {
                int lastPosition = insertedPosition - 1;
                TabLayout.Tab lastTab = mTabLayout.getTabAt(lastPosition);
                if (lastTab == null) {
                    throw new NullPointerException("lastTab == null");
                }
                lastTab.setCustomView(null);
                lastTab.setCustomView(R.layout.directory_tab_view);
            }
            mDirectories.add(insertedPosition, fileInfo);
            mTabLayout.addTab(mTabLayout.newTab().setCustomView(R.layout.directory_tab_view_without_arrow)
                    .setText(fileInfo.getName()).setTag(fileInfo), insertedPosition);
        } else {
            int position = mDirectories.indexOf(fileInfo);
            if (position != mDirectories.size() - 1) {
                throw new IllegalStateException(position + " is not last one");
            }
            TabLayout.Tab lastTab = mTabLayout.getTabAt(position);
            if (lastTab == null) {
                throw new NullPointerException("lastTab == null");
            }
            lastTab.setCustomView(null);
            lastTab.setCustomView(R.layout.directory_tab_view_without_arrow);
        }

        Fragment f = mFragmentManager.findFragmentByTag(fileInfo.getAbsolutePath());
        if (f == null) {
            f = FileInfosFragment.newInstance(fileInfo.getAbsolutePath());
            ft.add(R.id.contentFrame, f, fileInfo.getAbsolutePath());
        } else {
            ft.attach(f);
        }
        mSelectedDirectory = fileInfo;
        ft.commit();

        Message msg = mMyHandler.obtainMessage();
        msg.arg1 = mDirectories.indexOf(fileInfo);
        mMyHandler.sendMessageDelayed(msg, 100L);
    }

    private int getPositionForTab(FileInfo directory) {
        int tabCount = mTabLayout.getTabCount();
        for (int i = 0; i < tabCount; i++) {
            FileInfo tag = getTabTag(mTabLayout.getTabAt(i));
            if (tag.equals(directory)) {
                return i;
            }
        }
        return -1;
    }

    @NonNull
    private FileInfo getTabTag(TabLayout.Tab tab) {
        FileInfo tabTag = (FileInfo) tab.getTag();
        if (tabTag == null) {
            throw new NullPointerException("tabTag == null");
        }
        return tabTag;
    }

    private void selectTab(int position) {
        if (position < 0) {
            return;
        }
        TabLayout.Tab tab = position < mTabLayout.getTabCount()
                ? mTabLayout.getTabAt(position)
                : null;
        if (tab == null) {
            return;
        }
        try {
            Method method = TabLayout.class.getDeclaredMethod("selectTab", TabLayout.Tab.class);
            method.setAccessible(true);
            method.invoke(mTabLayout, tab);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static class MyHandler extends Handler {

        private WeakReference<FileInfosActivity> mReference;

        private MyHandler(FileInfosActivity fileInfosActivity) {
            mReference = new WeakReference<>(fileInfosActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            FileInfosActivity fileInfosActivity = mReference.get();
            if (fileInfosActivity != null) {
                int selectedTabPosition = msg.arg1;
                fileInfosActivity.selectTab(selectedTabPosition);
            }
        }
    }
}
