package io.julian.appchooser.sample;


import io.julian.appchooser.sample.data.FileInfosRepository;

/**
 * @author Zhu Liang
 * @version 1.0
 * @since 2017/5/13 下午3:53
 */

public class Injection extends io.julian.appchooser.Injection {
    public static FileInfosRepository provideFileInfoRepository() {
        return FileInfosRepository.getInstance(provideSchedulerProvider());
    }
}
