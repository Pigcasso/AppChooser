package io.zhuliang.appchooser.data;

/**
 * @author Zhu Liang
 * @version 1.0
 * @since 2017/4/15 下午1:45
 */

public class MediaType {

    private String mimeType;
    private String displayName;

    public MediaType(String mimeType, String displayName) {
        this.mimeType = mimeType;
        this.displayName = displayName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return "MediaType{" +
                "displayName='" + displayName + '\'' +
                ", mimeType='" + mimeType + '\'' +
                '}';
    }
}
