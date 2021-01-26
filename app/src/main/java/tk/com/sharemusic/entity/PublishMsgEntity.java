package tk.com.sharemusic.entity;

import java.util.Date;

public class PublishMsgEntity {
    public String msgId;

    public String msgType;

    public String publishId;
    public String publishTitle;
    public String publishText;

    public String peopleName;
    public String peopleHead;
    public String peopleId;

    public String reviewId;
    public String reviewText;

    public String chatReviewId;
    public String chatText;

    public int isReaded;

    public String goodsId;

    public long msgTime;

    public PublishMsgEntity(String msgId, String msgType, String publishId, String publishTitle,
                            String publishText, String peopleName, String peopleHead, String peopleId, int isReaded, String goodsId, long msgTime) {
        this.msgId = msgId;
        this.msgType = msgType;
        this.publishId = publishId;
        this.publishTitle = publishTitle;
        this.publishText = publishText;
        this.peopleName = peopleName;
        this.peopleHead = peopleHead;
        this.peopleId = peopleId;
        this.isReaded = isReaded;
        this.goodsId = goodsId;
        this.msgTime = msgTime;
    }

    public PublishMsgEntity(String msgId, String msgType, String publishId, String publishTitle, String publishText,
                            String peopleName, String peopleHead, String peopleId, String reviewId, String reviewText, int isReaded, long msgTime) {
        this.msgId = msgId;
        this.msgType = msgType;
        this.publishId = publishId;
        this.publishTitle = publishTitle;
        this.publishText = publishText;
        this.peopleName = peopleName;
        this.peopleHead = peopleHead;
        this.peopleId = peopleId;
        this.reviewId = reviewId;
        this.reviewText = reviewText;
        this.isReaded = isReaded;
        this.msgTime = msgTime;
    }

    public PublishMsgEntity(String msgId, String msgType, String publishId, String publishTitle, String publishText,
                            String peopleName, String peopleHead, String peopleId, String reviewId,
                            String chatReviewId, String chatText, int isReaded, long msgTime) {
        this.msgId = msgId;
        this.msgType = msgType;
        this.publishId = publishId;
        this.publishTitle = publishTitle;
        this.publishText = publishText;
        this.peopleName = peopleName;
        this.peopleHead = peopleHead;
        this.peopleId = peopleId;
        this.reviewId = reviewId;
        this.chatReviewId = chatReviewId;
        this.chatText = chatText;
        this.isReaded = isReaded;
        this.msgTime = msgTime;
    }

    public int getIsReaded() {
        return isReaded;
    }

    public void setIsReaded(int isReaded) {
        this.isReaded = isReaded;
    }

    public String getPublishTitle() {
        return publishTitle;
    }

    public void setPublishTitle(String publishTitle) {
        this.publishTitle = publishTitle;
    }

    public String getPublishText() {
        return publishText;
    }

    public void setPublishText(String publishText) {
        this.publishText = publishText;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getPublishId() {
        return publishId;
    }

    public void setPublishId(String publishId) {
        this.publishId = publishId;
    }

    public String getPeopleName() {
        return peopleName;
    }

    public void setPeopleName(String peopleName) {
        this.peopleName = peopleName;
    }

    public String getPeopleHead() {
        return peopleHead;
    }

    public void setPeopleHead(String peopleHead) {
        this.peopleHead = peopleHead;
    }

    public String getPeopleId() {
        return peopleId;
    }

    public void setPeopleId(String peopleId) {
        this.peopleId = peopleId;
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public String getChatReviewId() {
        return chatReviewId;
    }

    public void setChatReviewId(String chatReviewId) {
        this.chatReviewId = chatReviewId;
    }

    public String getChatText() {
        return chatText;
    }

    public void setChatText(String chatText) {
        this.chatText = chatText;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public long getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(long msgTime) {
        this.msgTime = msgTime;
    }
}
