package tk.com.sharemusic.network.response;

import java.util.List;

import tk.com.sharemusic.entity.ChatEntity;

public class SendMsgVo {
    int status;
    String msg;
    ChatEntity data;

    public SendMsgVo(int status, String msg, ChatEntity data) {
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

    public ChatEntity getData() {
        return data;
    }

    public void setData(ChatEntity data) {
        this.data = data;
    }
}
