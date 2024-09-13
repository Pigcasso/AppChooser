package io.zhuliang.appchooser.sample.data;

import androidx.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import io.zhuliang.appchooser.sample.constant.FileConsts;
import io.zhuliang.appchooser.sample.util.schedulers.ImmediateSchedulerProvider;

/**
 * @author Zhu Liang
 * @version 1.0
 * @since 2017/5/13 下午3:56
 */
@RunWith(AndroidJUnit4.class)
public class FileInfosRepositoryTest {

    private FileInfosRepository mFileInfosRepository;

    @Before
    public void setUp() throws Exception {
        mFileInfosRepository = new FileInfosRepository(new ImmediateSchedulerProvider());
    }

    @Test
    public void listFileInfos() throws Exception {
        mFileInfosRepository
                .listFileInfos(new FileInfo(new File(FileConsts.ROOT)))
                .test()
                .assertCompleted();
    }

}