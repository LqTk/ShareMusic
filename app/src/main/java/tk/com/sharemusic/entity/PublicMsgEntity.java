package tk.com.sharemusic.entity;

import java.util.Date;

public class PublicMsgEntity {
    private String msgId;

    private String msgType;

    private String publishId;

    private String peopleId;

    private String reviewId;

    private String chatReviewId;

    private String goodsId;

    private long msgTime;

    public PublicMsgEntity(String msgId, String msgType, String publishId, String peopleId, String reviewId, String chatReviewId, String goodsId, long msgTime) {
        this.msgId = msgId;
        this.msgType = msgType;
        this.publishId = publishId;
        this.peopleId = peopleId;
        this.reviewId = reviewId;
        this.chatReviewId = chatReviewId;
        this.goodsId = goodsId;
        this.msgTime = msgTime;
    }

    public PublicMsgEntity() {
        super();
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId == null ? null : msgId.trim();
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType == null ? null : msgType.trim();
    }

    public String getPublishId() {
        return publishId;
    }

    public void setPublishId(String publishId) {
        this.publishId = publishId == null ? null : publishId.trim();
    }

    public String getPeopleId() {
        return peopleId;
    }

    public void setPeopleId(String peopleId) {
        this.peopleId = peopleId == null ? null : peopleId.trim();
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId == null ? null : reviewId.trim();
    }

    public String getChatReviewId() {
        return chatReviewId;
    }

    public void setChatReviewId(String chatReviewId) {
        this.chatReviewId = chatReviewId == null ? null : chatReviewId.trim();
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId == null ? null : goodsId.trim();
    }

    public long getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(long msgTime) {
        this.msgTime = msgTime;
    }
}
