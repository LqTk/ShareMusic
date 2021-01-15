package tk.com.sharemusic.network.response;

import java.util.List;

import tk.com.sharemusic.entity.GoodsEntity;

public class GoodsResultVo {
    int status;
    String msg;
    GoodsEntity data;

    public GoodsResultVo(int status, String msg, GoodsEntity data) {
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

    public GoodsEntity getData() {
        return data;
    }

    public void setData(GoodsEntity data) {
        this.data = data;
    }
}
