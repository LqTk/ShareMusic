package tk.com.sharemusic.network;

import java.io.Serializable;

public class BaseResult implements Serializable {
    private int status;
    private String msg;

    public BaseResult(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
