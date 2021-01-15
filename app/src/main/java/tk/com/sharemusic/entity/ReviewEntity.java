package tk.com.sharemusic.entity;

import java.util.List;

public class ReviewEntity {
    private String reviewId;
    private String peopleId;
    private String publishId;
    private String reviewText;
    private String peopleName;
    private long reviewTime;
    private List<ChatReviewEntity> chatReviewList;

    public ReviewEntity(String reviewId, String peopleId, String publishId, String reviewText, String peopleName, long reviewTime, List<ChatReviewEntity> chatReviewEntities) {
        this.reviewId = reviewId;
        this.peopleId = peopleId;
        this.publishId = publishId;
        this.reviewText = reviewText;
        this.peopleName = peopleName;
        this.reviewTime = reviewTime;
        this.chatReviewList = chatReviewEntities;
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getPeopleId() {
        return peopleId;
    }

    public void setPeopleId(String peopleId) {
        this.peopleId = peopleId;
    }

    public String getPublishId() {
        return publishId;
    }

    public void setPublishId(String publishId) {
        this.publishId = publishId;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public String getPeopleName() {
        return peopleName;
    }

    public void setPeopleName(String peopleName) {
        this.peopleName = peopleName;
    }

    public long getReviewTime() {
        return reviewTime;
    }

    public void setReviewTime(long reviewTime) {
        this.reviewTime = reviewTime;
    }

    public List<ChatReviewEntity> getChatReviewList() {
        return chatReviewList;
    }

    public void setChatReviewList(List<ChatReviewEntity> chatReviewList) {
        this.chatReviewList = chatReviewList;
    }
}
