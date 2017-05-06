package io.julian.appchooser;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.File;

import io.julian.appchooser.data.ActivityInfo;
import io.julian.appchooser.data.ActivityInfosRepository;
import io.julian.appchooser.data.Resolver;
import io.julian.appchooser.data.ResolversRepository;
import io.julian.appchooser.exception.AppChooserException;
import io.julian.appchooser.util.schedulers.ImmediateSchedulerProvider;
import rx.Observable;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Zhu Liang
 * @version 1.0
 * @since 2017/4/25 上午11:59
 */
public class AppChooserPresenterTest {

    private static final ActivityInfo ACTIVITY_INFO = new ActivityInfo("mimeType", "pkg", "cls");
    private static final String MIME_TYPE_TEXT = "text/plain";

    @Mock
    private AppChooserContract.View mMockView;
    @Mock
    private File mMockFile;
    @Mock
    private ActivityInfosRepository mMockActivityInfosRepository;
    @Mock
    private ResolversRepository mMockResolversRepository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void loadActivityInfo_whenActivityInfoExists() throws Exception {
        returnValidActivityInfo();

        AppChooserContract.Presenter presenter = getPresenter(null);
        presenter.loadActivityInfo(Mockito.anyString());

        verify(mMockView).showFileContent(ACTIVITY_INFO, mMockFile);
    }

    @Test
    public void loadActivityInfo_throwsAppChooserExceptionAndShowMediaTypes() throws Exception {
        Mockito
                .doAnswer(new Answer() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        throw new AppChooserException();
                    }
                })
                .when(mMockView).showFileContent((ActivityInfo) Mockito.any(), (File) Mockito.any());

        AppChooserContract.Presenter presenter = getPresenter(null);
        returnValidActivityInfo();

        presenter.loadActivityInfo(AppChooserContract.DEFAULT_MIME_TYPE);

        verify(mMockActivityInfosRepository).deleteActivityInfo(Mockito.anyString());
        verify(mMockView).showMediaTypes();
    }

    @Test
    public void loadActivityInfo_whenActivityInfoNotExistsAndMimeTypeIsDefault() throws Exception {
        AppChooserContract.Presenter presenter = getPresenter(AppChooserContract.DEFAULT_MIME_TYPE);
        returnInvalidActivityInfo();

        presenter.loadActivityInfo(AppChooserContract.DEFAULT_MIME_TYPE);

        verify(mMockView).showMediaTypes();
    }

    /**
     * Resolver 有效，但没有设置为默认
     */
    @Test
    public void loadResolver_whenResolveIsValidButNotDefault() throws Exception {
        AppChooserContract.Presenter presenter = getPresenter(MIME_TYPE_TEXT);
        Resolver mockResolver = mock(Resolver.class);
        when(mockResolver.loadActivityInfo(Mockito.anyString())).thenReturn(ACTIVITY_INFO);

        presenter.loadResolver(mockResolver);

        verify(mMockActivityInfosRepository, never()).saveActivityInfo((ActivityInfo) Mockito.any());
        verify(mMockView).showFileContent((ActivityInfo) Mockito.any(), (File) Mockito.any());
    }

    /**
     * Resolver 有效，并且设置为默认
     */
    @Test
    public void loadResolver_whenResolveIsValidAndDefault() throws Exception {
        AppChooserContract.Presenter presenter = getPresenter(MIME_TYPE_TEXT);
        Resolver mockResolver = mock(Resolver.class);
        when(mockResolver.isDefault()).thenReturn(true);
        when(mockResolver.loadActivityInfo(Mockito.anyString())).thenReturn(ACTIVITY_INFO);

        presenter.loadResolver(mockResolver);

        verify(mMockActivityInfosRepository, times(1)).saveActivityInfo((ActivityInfo) Mockito.any());
        verify(mMockView).showFileContent((ActivityInfo) Mockito.any(), (File) Mockito.any());
    }

    private void returnValidActivityInfo() {
        when(mMockActivityInfosRepository.getActivityInfo(Mockito.anyString())).thenReturn(Observable.just(ACTIVITY_INFO));
    }

    private void returnInvalidActivityInfo() {
        when(mMockActivityInfosRepository.getActivityInfo(Mockito.anyString())).thenReturn(Observable.<ActivityInfo>just(null));
    }

    private AppChooserContract.Presenter getPresenter(String mimeType) {
        return new AppChooserPresenter(mMockView, new ImmediateSchedulerProvider(),
                mMockFile, mimeType, mMockActivityInfosRepository, mMockResolversRepository);
    }
}