package io.zhuliang.appchooser.data;

/**
 * 用于保存 "设置为默认应用" 的操作历史
 *
 * @author Zhu Liang
 * @version 1.0
 * @since 2017/4/15 下午1:28
 */

public class ActivityInfo {

    private String mimeType;
    private String pkg;
    private String cls;

    public ActivityInfo(String mimeType, String pkg, String cls) {
        this.mimeType = mimeType;
        this.pkg = pkg;
        this.cls = cls;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getCls() {
        return cls;
    }

    public void setCls(String cls) {
        this.cls = cls;
    }

    public String getPkg() {
        return pkg;
    }

    public void setPkg(String pkg) {
        this.pkg = pkg;
    }

    @Override
    public String toString() {
        return "ActivityInfo{" +
                "mimeType='" + mimeType + '\'' +
                ", cls='" + cls + '\'' +
                ", pkg='" + pkg + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ActivityInfo that = (ActivityInfo) o;

        if (!mimeType.equals(that.mimeType)) return false;
        if (!cls.equals(that.cls)) return false;
        return pkg.equals(that.pkg);

    }

    @Override
    public int hashCode() {
        int result = mimeType.hashCode();
        result = 31 * result + cls.hashCode();
        result = 31 * result + pkg.hashCode();
        return result;
    }
}
