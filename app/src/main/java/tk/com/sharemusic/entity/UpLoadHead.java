package tk.com.sharemusic.entity;

public class UpLoadHead {
    boolean success;
    String msg;
    String url;
    String uri;

    public UpLoadHead(boolean success, String msg, String url, String uri) {
        this.success = success;
        this.msg = msg;
        this.url = url;
        this.uri = uri;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
