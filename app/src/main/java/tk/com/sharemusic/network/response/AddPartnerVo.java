package tk.com.sharemusic.network.response;

import java.util.Map;

public class AddPartnerVo {
    private int status;
    private String msg;
    private Map data;

    public AddPartnerVo(int status, String msg, Map data) {
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

    public Map getData() {
        return data;
    }

    public void setData(Map data) {
        this.data = data;
    }
}
