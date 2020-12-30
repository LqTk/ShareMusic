package tk.com.sharemusic.network.response;

import tk.com.sharemusic.entity.MsgEntiti;

public class PeopleVo {
    private int status;
    private String msg;
    private MsgEntiti data;

    public PeopleVo(int status, String msg, MsgEntiti data) {
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

    public MsgEntiti getData() {
        return data;
    }

    public void setData(MsgEntiti data) {
        this.data = data;
    }
}
