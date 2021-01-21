package tk.com.sharemusic.network.response;

import java.util.List;

import tk.com.sharemusic.entity.PublicMsgEntity;

public class PublicMsgVo {
    int status;
    String msg;
    List<PublicMsgEntity> data;

    public PublicMsgVo(int status, String msg, List<PublicMsgEntity> data) {
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

    public List<PublicMsgEntity> getData() {
        return data;
    }

    public void setData(List<PublicMsgEntity> data) {
        this.data = data;
    }
}
