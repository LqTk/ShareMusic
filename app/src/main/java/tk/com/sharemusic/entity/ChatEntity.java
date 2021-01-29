package tk.com.sharemusic.entity;

import tk.com.sharemusic.ShareApplication;
import tk.com.sharemusic.config.Constants;

public class ChatEntity {
    public String chatId;

    public String msgContent;

    public String msgType = Constants.MODE_TEXT; //

    public String voiceTime;
    public String senderId;
    public String senderAvatar;
    public String senderName;
    public String localPath;
    public long chatTime;
    public int count;

    private boolean isSending = false;
    private boolean sendSuccess = true;

    public ChatEntity(String chatId, String msgContent, String msgType, String voiceTime, String senderId, String senderAvatar, String senderName, long chatTime) {
        this.chatId = chatId;
        this.msgContent = msgContent;
        this.msgType = msgType;
        this.voiceTime = voiceTime;
        this.senderId = senderId;
        this.senderAvatar = senderAvatar;
        this.senderName = senderName;
        this.chatTime = chatTime;
    }

    public ChatEntity() {
    }

    public boolean isSendSuccess() {
        return sendSuccess;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public void setSendSuccess(boolean sendSuccess) {
        this.sendSuccess = sendSuccess;
    }

    public boolean isSending() {
        return isSending;
    }

    public void setSending(boolean sending) {
        isSending = sending;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
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

    public String getVoiceTime() {
        return voiceTime;
    }

    public void setVoiceTime(String voiceTime) {
        this.voiceTime = voiceTime;
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

    public long getChatTime() {
        return chatTime;
    }

    public void setChatTime(long chatTime) {
        this.chatTime = chatTime;
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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}