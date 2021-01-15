package tk.com.sharemusic.network.response;

import tk.com.sharemusic.entity.ReviewEntity;

public class AddReviewVo {
    private int status;
    private String msg;
    private ReviewEntity data;

    public AddReviewVo(int status, String msg, ReviewEntity data) {
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

    public ReviewEntity getData() {
        return data;
    }

    public void setData(ReviewEntity data) {
        this.data = data;
    }
}
