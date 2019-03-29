package io.zhuliang.appchooser.data;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static io.zhuliang.appchooser.internal.Preconditions.checkNotNull;

/**
 * @author Zhu Liang
 * @version 1.0
 * @since 2017/4/16 上午11:23
 */

public class Resolver implements Parcelable {

    public static final Creator<Resolver> CREATOR = new Creator<Resolver>() {
        @Override
        public Resolver createFromParcel(Parcel source) {
            return new Resolver(source);
        }

        @Override
        public Resolver[] newArray(int size) {
            return new Resolver[size];
        }
    };
    private ResolveInfo mResolveInfo;
    private boolean mIsDefault;

    public Resolver(ResolveInfo resolveInfo) {
        mResolveInfo = resolveInfo;
    }

    protected Resolver(Parcel in) {
        this.mResolveInfo = in.readParcelable(ResolveInfo.class.getClassLoader());
        this.mIsDefault = in.readByte() != 0;
    }

    @Nullable
    public Drawable loadIcon(@NonNull PackageManager pkgMgr) {
        checkNotNull(pkgMgr);
        return mResolveInfo.loadIcon(pkgMgr);
    }

    @NonNull
    public String loadLabel(PackageManager pkgMgr) {
        checkNotNull(pkgMgr);
        CharSequence label = mResolveInfo.loadLabel(pkgMgr);
        return label == null ? "" : label.toString();
    }

    @NonNull
    public ActivityInfo loadActivityInfo(@NonNull String mimeType) {
        checkNotNull(mimeType);
        return new ActivityInfo(mimeType,
                mResolveInfo.activityInfo.packageName,
                mResolveInfo.activityInfo.name);
    }

    @NonNull
    public ComponentName loadComponentName() {
        return new ComponentName(mResolveInfo.activityInfo.packageName,
                mResolveInfo.activityInfo.name);
    }

    public boolean isDefault() {
        return mIsDefault;
    }

    public void setDefault(boolean aDefault) {
        mIsDefault = aDefault;
    }

    @Override
    public String toString() {
        return "Resolver{" +
                "mResolveInfo=" + mResolveInfo +
                ", mIsDefault=" + mIsDefault +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.mResolveInfo, flags);
        dest.writeByte(this.mIsDefault ? (byte) 1 : (byte) 0);
    }
}
