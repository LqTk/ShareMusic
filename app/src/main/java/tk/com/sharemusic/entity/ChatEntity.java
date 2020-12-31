package tk.com.sharemusic.entity;

import tk.com.sharemusic.ShareApplication;
import tk.com.sharemusic.config.Constants;

public class ChatEntity {
    public String chatid;

    public String msgContent;

    public String msgType = Constants.MODE_TEXT; //

    public String voicetime;
    public String senderId;
    public String senderAvatar;
    public String senderName;
    public long chattime;

    public ChatEntity(String chatid, String msgContent, String msgType, String voicetime, String senderId, String senderAvatar, String senderName, long chattime) {
        this.chatid = chatid;
        this.msgContent = msgContent;
        this.msgType = msgType;
        this.voicetime = voicetime;
        this.senderId = senderId;
        this.senderAvatar = senderAvatar;
        this.senderName = senderName;
        this.chattime = chattime;
    }

    public String getChatid() {
        return chatid;
    }

    public void setChatid(String chatid) {
        this.chatid = chatid;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getVoicetime() {
        return voicetime;
    }

    public void setVoicetime(String voicetime) {
        this.voicetime = voicetime;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderAvatar() {
        return senderAvatar;
    }

    public void setSenderAvatar(String senderAvatar) {
        this.senderAvatar = senderAvatar;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public long getChattime() {
        return chattime;
    }

    public void setChattime(long chattime) {
        this.chattime = chattime;
    }

    private boolean isMyContent = false;

    public boolean isMyContent(){
        User user = ShareApplication.getUser();
        if (user!=null){
            if (user.getUserId().equals(senderId)){
                isMyContent = true;
            } else {
                isMyContent = false;
            }
        }
        return isMyContent;
    }

}