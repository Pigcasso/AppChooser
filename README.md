# AppChooser
## 描述
自定义打开指定文件的应用选择器。
## 起因
我司主营企业版云存储服务，在一段时间里经常有用户反馈点击某个文件会自动跳转到手机系统自带APP（大多是音乐播放器）的问题。结果发现是手机厂商Rom修改了底层逻辑导致的，为了绕过这个bug，只能在应用内自己实现选择器。

## 效果图

![](screenshots/Gif_20170624_154149.gif)

## 依赖

`compile 'io.julian:appchooser:1.0.6'`

## 1.1.0 特性
- 允许屏蔽掉制定的 Component 作为选择项。

## 1.0.6 特性

把我写的另外两个库：`common-1.0.9` , `mvp-1.0.7` 引入 `appchooser` 中。

### 已知问题

写这个库其实是工作需要，我在[这篇文章](http://www.jianshu.com/p/3f65576f89b7) 中提到过写这个库的初衷。我负责的这个项目的 compileSdkVersion 还是 23，也就是说没有引入 Android N 的新特性。关于 `compileSdkVersion` 、`minSdkVersion`、`targetSdkVersion` 的区别可以看下这篇文章[如何选择 compileSdkVersion, minSdkVersion 和 targetSdkVersion](http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2016/0110/3854.html)。在 Android N 中引入了一个新特性：应用间共享文件禁用 **file://URI**。也就是说不能再像以前那样：

```java
Intent intent = new Intent(Intent.ACTION_VIEW);
intent.setDataAndType(Uri.fromFile(new File("文件绝对路径")), "文件的MimeType");
context.startActivity(intent);
```

大家可以看看这篇文章：[Android7.0须知--应用间共享文件（FileProvider）](http://www.jianshu.com/p/3f9e3fc38eae)。目前我司的软件还是使用`targetSdkVersion = 19`。

## 1.0.5 特性

修复bug：如果设置某个Activity为某类型文件的默认打开方式，然后将这个Activity所属的App卸载，再次点击这种类型的文件会导致App闪退。

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
