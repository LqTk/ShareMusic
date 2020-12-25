package tk.com.sharemusic.network.response;

import java.util.List;

import tk.com.sharemusic.entity.SocialPublicEntity;

public class GetPublicDataTenVo {
    int status;
    String msg;
    List<SocialPublicEntity> data;

    public GetPublicDataTenVo(int status, String msg, List<SocialPublicEntity> data) {
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

    public List<SocialPublicEntity> getData() {
        return data;
    }

    public void setData(List<SocialPublicEntity> data) {
        this.data = data;
    }
}
