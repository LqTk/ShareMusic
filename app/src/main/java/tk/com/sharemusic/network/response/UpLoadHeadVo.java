package tk.com.sharemusic.network.response;

import tk.com.sharemusic.entity.UpLoadHead;
import tk.com.sharemusic.network.BaseResult;

public class UpLoadHeadVo  {
    int status;
    String msg;
    UpLoadHead data;

    public UpLoadHeadVo(int status, String msg, UpLoadHead data) {
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

    public UpLoadHead getData() {
        return data;
    }

    public void setData(UpLoadHead data) {
        this.data = data;
    }
}
