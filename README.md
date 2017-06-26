# AppChooser
## 描述
自定义打开指定文件的应用选择器。
## 起因
我司主营企业版云存储服务，在一段时间里经常有用户反馈点击某个文件会自动跳转到手机系统自带APP（大多是音乐播放器）的问题。结果发现是手机厂商Rom修改了底层逻辑导致的，为了绕过这个bug，只能在应用内自己实现选择器。

## 效果图

![](http://upload-images.jianshu.io/upload_images/2275760-88d914b06eb4bbb6.gif?imageMogr2/auto-orient/strip)

## 依赖

`compile 'io.julian:appchooser:1.0.4'`
## 使用方法

在Activity或Fragment中：

```java
@NonNull
private AppChooser mAppChooser;
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_file_infos);
  	// 初始化 AppChooser
    mAppChooser = AppChooser.with(this); 
}

@Override
public void onStart() {
    super.onStart();
  	// 绑定 AppChooser
    mAppChooser.bind();
}
@Override
public void onStop() {
    super.onStop();
   	// 解绑 AppChooser
    mAppChooser.unbind();
}

/**
 * 打开文件
 *
 * @param file 待打开的文件
 */
private void showFile(@NonNull File file) {
    // 检查文件非空
    Preconditions.checkNotNull(file);
    // 必须是文件
    Preconditions.checkArgument(file.isFile());
    mAppChooser.file(file).load();
}
/**
 * 打开文件并将编辑的结果回传给 Activity 或 Fragment
 *
 * @param file 待打开的文件
 * @see android.app.Activity#onActivityResult(int, int, Intent)
 * @see android.support.v4.app.Fragment#onActivityResult(int, int, Intent)
 */
private void modifyFile(@NonNull File file) {
    // 检查文件非空
    Preconditions.checkNotNull(file);
    // 必须是文件
    Preconditions.checkArgument(file.isFile());
    mAppChooser.file(file).requestCode(REQUEST_CODE_MODIFY_FILE).load();
}
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == RESULT_OK
            && requestCode == REQUEST_CODE_MODIFY_FILE) {
        // 编辑结果的回调
    }
}
```
