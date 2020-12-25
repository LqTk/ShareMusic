package tk.com.sharemusic.network.response;

import tk.com.sharemusic.entity.PeopleEntity;

public class PeopleVo {
    private int status;
    private String msg;
    private PeopleEntity data;

    public PeopleVo(int status, String msg, PeopleEntity data) {
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

    public PeopleEntity getData() {
        return data;
    }

    public void setData(PeopleEntity data) {
        this.data = data;
    }
}
