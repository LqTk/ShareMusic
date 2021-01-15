package tk.com.sharemusic.entity;

import java.util.Date;

public class ChatReviewEntity {
    private String reviewChatId;

    private String reviewId;

    private String talkId;

    private String toId;

    private String talkName;

    private String toName;

    private String chatText;

    private Date chatTime;

    public ChatReviewEntity(String reviewChatId, String reviewId, String talkId, String toId, String talkName, String toName, String chatText, Date chatTime) {
        this.reviewChatId = reviewChatId;
        this.reviewId = reviewId;
        this.talkId = talkId;
        this.toId = toId;
        this.talkName = talkName;
        this.toName = toName;
        this.chatText = chatText;
        this.chatTime = chatTime;
    }

    public String getTalkName() {
        return talkName;
    }

    public void setTalkName(String talkName) {
        this.talkName = talkName;
    }

    public String getToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }

    public ChatReviewEntity() {
        super();
    }

    public String getReviewChatId() {
        return reviewChatId;
    }

    public void setReviewChatId(String reviewChatId) {
        this.reviewChatId = reviewChatId == null ? null : reviewChatId.trim();
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId == null ? null : reviewId.trim();
    }

    public String getTalkId() {
        return talkId;
    }

    public void setTalkId(String talkId) {
        this.talkId = talkId == null ? null : talkId.trim();
    }

    public String getToid() {
        return toId;
    }

    public void setToid(String toid) {
        this.toId = toid == null ? null : toid.trim();
    }

    public String getChatText() {
        return chatText;
    }

    public void setChatText(String chatText) {
        this.chatText = chatText == null ? null : chatText.trim();
    }

    public Date getChattime() {
        return chatTime;
    }

    public void setChattime(Date chattime) {
        this.chatTime = chattime;
    }
}
