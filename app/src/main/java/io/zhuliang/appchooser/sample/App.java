package io.zhuliang.appchooser.sample;

import android.app.Application;
import android.os.StrictMode;

/**
 * @author Du Wenyu
 * 2018/9/5
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // 从Android N开始，通过其他应用打开文件时，默认不再支持文件绝对路径的Url（会抛出FileUriExposedException）。
        // 参考了 Amaze File Manager 的解决方案，添加了下面的代码。
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }
}
