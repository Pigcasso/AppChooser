# AppChooser

[![](https://jitpack.io/v/Pigcasso/AppChooser.svg)](https://jitpack.io/#Pigcasso/AppChooser)

## 描述
自定义打开指定文件的应用选择器。
## 起因
我司主营企业版云存储服务，在一段时间里经常有用户反馈点击某个文件会自动跳转到手机系统自带APP（大多是音乐播放器）的问题。结果发现是手机厂商Rom修改了底层逻辑导致的，为了绕过这个bug，只能在应用内自己实现选择器。

## 下载
[Download latest release](https://github.com/JulianAndroid/AppChooser/releases/tag/v2.0.1)

## 效果图

![其他应用打开打开文件](screenshots/Gif_20171117_095658.gif)



![分享文字到其他应用](screenshots/Gif_20171117_095855.gif)

## 依赖

`implementation 'com.github.Pigcasso:AppChooser:Tag'`

## 4.0.1 特性

- 修复 Android 10 不能打开APK文件的 bug

## 4.0.0 特性

- 移除 RxJava

## 2.0.1 特性

- 修复没有能打开文件的应用时显示空列表的bug

## 2.0.0 特性

- 重构项目，提高可扩展性
- 支持[Intent.ACTION_SEND](https://developer.android.com/training/sharing/send.html)分享文本
- 允许[Intent.ACTION_SEND](https://developer.android.com/training/sharing/send.html)过滤组件

## 1.2.0 特性
- 修复弹出应用选择器后旋转屏幕不能使用选择的打开方式打开文件的bug。

## 1.1.0 特性

- 允许屏蔽掉指定的 Component 作为选择项（详情请查看----使用方法）。

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
### 打开文件
在Activity或Fragment中：

```java
private void showFile(FileInfo file) {
    AppChooser.from(this)
      .file(new File(file.getAbsolutePath()))
      .excluded(excluded)
      .requestCode(REQUEST_CODE_OPEN_FILE)
      .load();
}

@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
  	// 处理返回结果
    if (requestCode == REQUEST_CODE_OPEN_FILE) {
        Log.d(TAG, "onActivityResult: " + requestCode + "," + resultCode + "," + data);
    }
}
```
### 分享文本
在`Activity`或`Fragment`中：

``` java
public void onShareClick(View view) {
    EditText editText = (EditText) findViewById(R.id.edit_text);
    if (editText != null) {
        CharSequence shareContent = editText.getText();
        AppChooser
          .from(this)
          .text(shareContent.toString())
          .excluded(mComponentNames)
          .load();
    }
}
```


