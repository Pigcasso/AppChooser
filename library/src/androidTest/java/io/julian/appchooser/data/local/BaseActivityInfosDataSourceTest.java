package io.julian.appchooser.data.local;

import android.support.annotation.NonNull;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.julian.appchooser.data.ActivityInfo;
import io.julian.appchooser.data.ActivityInfosDataSource;

/**
 * @author Zhu Liang
 * @version 1.0
 * @since 2017/5/5 下午5:11
 */
@RunWith(AndroidJUnit4.class)
public abstract class BaseActivityInfosDataSourceTest {

    private static final ActivityInfo TEST_CASE_1 = new ActivityInfo("mime_type_1", "pkg_1", "cls_1");
    private static final ActivityInfo TEST_CASE_2 = new ActivityInfo("mime_type_2", "pkg_2", "cls_2");
    private static final ActivityInfo TEST_CASE_3 = new ActivityInfo("mime_type_3", "pkg_3", "cls_3");
    private static final ActivityInfo TEST_CASE_4 = new ActivityInfo("mime_type_4", "pkg_4", "cls_4");
    private static final String UNKNOWN_MIME_TYPE = "unknown_mime_type";

    private ActivityInfosDataSource mDataSource;

    @Before
    public void setUp() throws Exception {
        mDataSource = getDataSource();
    }

    @NonNull
    abstract ActivityInfosDataSource getDataSource();

    @Test
    public void saveActivityInfo() throws Exception {
        mDataSource.saveActivityInfo(TEST_CASE_1);
        mDataSource
                .getActivityInfo(TEST_CASE_1.getMimeType())
                .test()
                .assertValues(TEST_CASE_1);
        int delete = mDataSource
                .deleteActivityInfo(TEST_CASE_1.getMimeType());
        Assert.assertTrue(delete == 1);
    }

    @Test
    public void getActivityInfo_whenSavedFourTestCase1s() throws Exception {
        mDataSource.saveActivityInfo(TEST_CASE_1);
        mDataSource.saveActivityInfo(TEST_CASE_1);
        mDataSource.saveActivityInfo(TEST_CASE_1);
        mDataSource.saveActivityInfo(TEST_CASE_1);

        mDataSource
                .getActivityInfo(TEST_CASE_1.getMimeType())
                .test()
                .assertValues(TEST_CASE_1);
        int delete = mDataSource
                .deleteActivityInfo(TEST_CASE_1.getMimeType());
        Assert.assertTrue(delete == 1);
    }

    @Test
    public void getActivityInfo_UnknownMimeType() throws Exception {
        mDataSource.deleteAllActivityInfos();
        mDataSource.getActivityInfo(UNKNOWN_MIME_TYPE)
                .test()
                .assertValue(null);
    }

    @Test
    public void deleteActivityInfo_whenMimeTypeIsNull() throws Exception {
        int delete = mDataSource.deleteActivityInfo(null);
        Assert.assertTrue(delete == 0);
    }

    @Test
    public void deleteActivityInfo_whenMimeTypeExisted() throws Exception {
        mDataSource.saveActivityInfo(TEST_CASE_1);
        int delete = mDataSource.deleteActivityInfo(TEST_CASE_1.getMimeType());
        Assert.assertEquals(1, delete);
    }

    @Test
    public void deleteActivityInfo_whenMimeTypeNotExisted() throws Exception {
        int delete = mDataSource.deleteActivityInfo(UNKNOWN_MIME_TYPE);
        Assert.assertEquals(0, delete);
    }

    @Test
    public void deleteAllActivityInfos() throws Exception {
        mDataSource.saveActivityInfo(TEST_CASE_1);
        mDataSource.saveActivityInfo(TEST_CASE_2);
        mDataSource.saveActivityInfo(TEST_CASE_3);
        mDataSource.saveActivityInfo(TEST_CASE_4);

        int delete = mDataSource.deleteAllActivityInfos();
        Assert.assertEquals(4, delete);
    }

    @Test
    public void deleteAllActivityInfos_whenActivityInfosAreEmpty() throws Exception {
        mDataSource.deleteAllActivityInfos();
        int delete = mDataSource.deleteAllActivityInfos();
        Assert.assertEquals(0, delete);
    }
}
