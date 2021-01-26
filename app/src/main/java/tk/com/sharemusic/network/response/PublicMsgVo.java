package tk.com.sharemusic.network.response;

import java.util.List;

import tk.com.sharemusic.entity.PublishMsgEntity;

public class PublicMsgVo {
    int status;
    String msg;
    List<PublishMsgEntity> data;

    public PublicMsgVo(int status, String msg, List<PublishMsgEntity> data) {
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

    public List<PublishMsgEntity> getData() {
        return data;
    }

    public void setData(List<PublishMsgEntity> data) {
        this.data = data;
    }
}
