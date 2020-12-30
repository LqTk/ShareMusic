package tk.com.sharemusic.entity;

import java.io.Serializable;

public class HeadItem implements Serializable {
    int resId;
    String name;
    int code;

    public HeadItem(int code, int resId, String name) {
        this.code = code;
        this.resId = resId;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
