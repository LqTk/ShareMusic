package tk.com.sharemusic.network.response;

import tk.com.sharemusic.entity.ChatReviewEntity;

public class ChatReviewVo {
    private int status;
    private String msg;
    private ChatReviewEntity data;

    public ChatReviewVo(int status, String msg, ChatReviewEntity data) {
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

    public ChatReviewEntity getData() {
        return data;
    }

    public void setData(ChatReviewEntity data) {
        this.data = data;
    }
}
