package io.zhuliang.appchooser.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 针对常见的打开 app 时有推荐应用的场景
 * 对推荐应用进行封装
 *
 * @author Du Wenyu
 * 2018/9/5
 */
public class RecommendApp implements Parcelable {

    /**
     * 推荐应用名，可能包含【荐】之类的强调图标
     * 所以使用 CharSequence
     */
    public CharSequence name;

    /**
     * 推荐应用的包名，唯一标识
     */
    public String packageName;

    /**
     * 推荐应用的详细描述
     */
    public String description;

    /**
     * 推荐应用的下载链接。一般点击后在浏览器中打开
     */
    public String downloadUrl;

    /**
     * 推荐应用的图标资源 id
     */
    public int iconResourceId;

    public RecommendApp(CharSequence name, String packageName, String description, String downloadUrl, int iconResourceId) {
        this.name = name;
        this.packageName = packageName;
        this.description = description;
        this.downloadUrl = downloadUrl;
        this.iconResourceId = iconResourceId;
    }

    // ----------- Parcelable 模板代码 -----------
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.packageName);
        dest.writeString(this.downloadUrl);
        dest.writeInt(this.iconResourceId);
    }

    protected RecommendApp(Parcel in) {
        this.packageName = in.readString();
        this.downloadUrl = in.readString();
        this.iconResourceId = in.readInt();
    }

    public static final Parcelable.Creator<RecommendApp> CREATOR = new Parcelable.Creator<RecommendApp>() {
        @Override
        public RecommendApp createFromParcel(Parcel source) {
            return new RecommendApp(source);
        }

        @Override
        public RecommendApp[] newArray(int size) {
            return new RecommendApp[size];
        }
    };
}
