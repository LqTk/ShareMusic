package tk.com.sharemusic.network.response;

import tk.com.sharemusic.entity.User;

public class LoginVo {
    private int status;
    private String msg;
    private User data;

    public LoginVo(int status, String msg, User data) {
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

    public User getData() {
        return data;
    }

    public void setData(User data) {
        this.data = data;
    }
}
