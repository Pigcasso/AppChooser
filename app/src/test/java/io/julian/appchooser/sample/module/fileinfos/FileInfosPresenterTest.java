package io.julian.appchooser.sample.module.fileinfos;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import io.julian.appchooser.sample.data.FileInfo;
import io.julian.appchooser.sample.data.FileInfosDataSource;
import io.julian.appchooser.sample.data.FileInfosRepository;
import io.julian.appchooser.util.schedulers.ImmediateSchedulerProvider;
import rx.Observable;

/**
 * @author Zhu Liang
 * @version 1.0
 * @since 2017/5/13 下午5:14
 */
public class FileInfosPresenterTest {
    private static final FileInfo TEST_FILE_INFOS_1 = new FileInfo("1");
    private static final FileInfo TEST_FILE_INFOS_2 = new FileInfo("2");
    private static final FileInfo TEST_FILE_INFOS_3 = new FileInfo("3");
    private static final FileInfo TEST_FILE_INFOS_4 = new FileInfo("4");

    @Mock
    private FileInfo mMockFileInfo;
    @Mock
    private FileInfosContract.View mMockView;
    @Mock
    private FileInfosRepository mMockFileInfosRepository;
    private FileInfosPresenter mFileInfosPresenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        Mockito.when(mMockFileInfo.isDirectory()).thenReturn(true);
        mFileInfosPresenter = new FileInfosPresenter(mMockFileInfo, new ImmediateSchedulerProvider(),
                mMockView, mMockFileInfosRepository);
    }

    @Test
    public void loadFileInfos_success() throws Exception {
        setAvailableFileInfos(mMockFileInfosRepository);
        mFileInfosPresenter.loadFileInfos();
        Mockito.verify(mMockView, Mockito.times(1)).showFileInfos(Mockito.anyListOf(FileInfo.class));
    }

    private void setAvailableFileInfos(FileInfosDataSource fileInfosDataSource) {
        Mockito.when(fileInfosDataSource.listFileInfos(Mockito.any(FileInfo.class))).thenReturn(
                Observable.just(TEST_FILE_INFOS_1, TEST_FILE_INFOS_2, TEST_FILE_INFOS_3,
                        TEST_FILE_INFOS_4).toList());
    }

}