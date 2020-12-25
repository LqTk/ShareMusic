package tk.com.sharemusic.network.response;

import java.util.List;

import tk.com.sharemusic.entity.PeopleEntity;

public class PartnerVo {
    private int status;
    private String msg;
    private List<PeopleEntity> data;

    public PartnerVo(int status, String msg, List<PeopleEntity> data) {
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

    public List<PeopleEntity> getData() {
        return data;
    }

    public void setData(List<PeopleEntity> data) {
        this.data = data;
    }
}
