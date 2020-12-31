package tk.com.sharemusic.network.response;

import java.util.List;

import tk.com.sharemusic.entity.ChatEntity;

public class ChatListVo {
    int status;
    String msg;
    List<ChatEntity> data;

    public ChatListVo(int status, String msg, List<ChatEntity> data) {
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

    public List<ChatEntity> getData() {
        return data;
    }

    public void setData(List<ChatEntity> data) {
        this.data = data;
    }
}
