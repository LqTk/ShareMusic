package tk.com.sharemusic.entity;

import java.util.Date;

public class MsgEntity {
    private String chatid;

    private String talkid;

    private String toid;

    private String msgtype;

    private String msgcontent;

    private String voicetime;

    private String filepath;

    private Date chattime;

    public MsgEntity(String chatid, String talkid, String toid, String msgtype, String msgcontent, String voicetime, String filepath, Date chattime) {
        this.chatid = chatid;
        this.talkid = talkid;
        this.toid = toid;
        this.msgtype = msgtype;
        this.msgcontent = msgcontent;
        this.voicetime = voicetime;
        this.filepath = filepath;
        this.chattime = chattime;
    }

    public MsgEntity() {
        super();
    }

    public String getChatid() {
        return chatid;
    }

    public void setChatid(String chatid) {
        this.chatid = chatid == null ? null : chatid.trim();
    }

    public String getTalkid() {
        return talkid;
    }

    public void setTalkid(String talkid) {
        this.talkid = talkid == null ? null : talkid.trim();
    }

    public String getToid() {
        return toid;
    }

    public void setToid(String toid) {
        this.toid = toid == null ? null : toid.trim();
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype == null ? null : msgtype.trim();
    }

    public String getMsgcontent() {
        return msgcontent;
    }

    public void setMsgcontent(String msgcontent) {
        this.msgcontent = msgcontent == null ? null : msgcontent.trim();
    }

    public String getVoicetime() {
        return voicetime;
    }

    public void setVoicetime(String voicetime) {
        this.voicetime = voicetime;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath == null ? null : filepath.trim();
    }

    public Date getChattime() {
        return chattime;
    }

    public void setChattime(Date chattime) {
        this.chattime = chattime;
    }
}