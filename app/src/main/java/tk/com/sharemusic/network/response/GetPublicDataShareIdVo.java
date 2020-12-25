package tk.com.sharemusic.network.response;

import tk.com.sharemusic.entity.SocialPublicEntity;

public class GetPublicDataShareIdVo {
    int status;
    String msg;
    SocialPublicEntity data;

    public GetPublicDataShareIdVo(int status, String msg, SocialPublicEntity data) {
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

    public SocialPublicEntity getData() {
        return data;
    }

    public void setData(SocialPublicEntity data) {
        this.data = data;
    }
}
