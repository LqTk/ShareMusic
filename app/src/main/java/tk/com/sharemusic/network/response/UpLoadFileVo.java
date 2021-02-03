package tk.com.sharemusic.network.response;

import java.util.HashMap;

public class UpLoadFileVo {
    int status;
    String msg;
    HashMap data;

    public UpLoadFileVo(int status, String msg, HashMap data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
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

    public HashMap getData() {
        return data;
    }

    public void setData(HashMap data) {
        this.data = data;
    }
}
