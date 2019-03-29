package io.zhuliang.appchooser.action;

import android.content.ComponentName;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

/**
 * @author Zhu Liang
 */

public final class ActionConfig implements Parcelable {

    public String actionName;
    public String pathname;
    public int requestCode;
    public ComponentName[] excluded;
    public String mimeType;
    public String text;
    // Activity.startActivity 还是 Fragment.startActivity
    public boolean fromActivity;
    /**
     * 用于 {@link androidx.core.content.FileProvider#getUriForFile(Context, String, File)}
     * 的第二个参数
     */
    public String authority;
    public boolean isUriExposed = true;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.actionName);
        dest.writeString(this.pathname);
        dest.writeInt(this.requestCode);
        dest.writeTypedArray(this.excluded, flags);
        dest.writeString(this.mimeType);
        dest.writeString(this.text);
        dest.writeByte(this.fromActivity ? (byte) 1 : (byte) 0);
        dest.writeString(this.authority);
        dest.writeByte(this.isUriExposed ? (byte) 1 : (byte) 0);
    }

    public ActionConfig() {
    }

    protected ActionConfig(Parcel in) {
        this.actionName = in.readString();
        this.pathname = in.readString();
        this.requestCode = in.readInt();
        this.excluded = in.createTypedArray(ComponentName.CREATOR);
        this.mimeType = in.readString();
        this.text = in.readString();
        this.fromActivity = in.readByte() != 0;
        this.authority = in.readString();
        this.isUriExposed = in.readByte() != 0;
    }

    public static final Creator<ActionConfig> CREATOR = new Creator<ActionConfig>() {
        @Override
        public ActionConfig createFromParcel(Parcel source) {
            return new ActionConfig(source);
        }

        @Override
        public ActionConfig[] newArray(int size) {
            return new ActionConfig[size];
        }
    };
}
