package tk.com.sharemusic.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class SocialPublicEntity implements Serializable {
    private String shareId;

    private String userId;

    private String userName;

    private String userHead;

    private Integer userSex;

    private String shareName;

    private String shareUrl;

    private String shareText;

    private long createTime;

    private Integer isPublic;

    private Integer reportCount;

    private String type;

    private String location;

    private Double latitude;

    private Double longitude;

    private List<GoodsEntity> goodsList;

    private List<ReviewEntity> reviewEntities;

    public SocialPublicEntity(String shareId, String userId, String userName, String userHead,
                              Integer userSex, String shareName, String shareUrl, String shareText,
                              long createTime, Integer isPublic, Integer reportCount, String type, String location,
                              Double latitude, Double longitude, List<GoodsEntity> goodsList, List<ReviewEntity> reviewEntities) {
        this.shareId = shareId;
        this.userId = userId;
        this.userName = userName;
        this.userHead = userHead;
        this.userSex = userSex;
        this.shareName = shareName;
        this.shareUrl = shareUrl;
        this.shareText = shareText;
        this.createTime = createTime;
        this.isPublic = isPublic;
        this.reportCount = reportCount;
        this.type = type;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.goodsList = goodsList;
        this.reviewEntities = reviewEntities;
    }

    public SocialPublicEntity(String userId, String userName, String userHead, Integer userSex,
                              String shareName, String shareUrl, String shareText, Integer isPublic, Integer reportCount,
                              String type, String location, Double latitude, Double longitude, List<GoodsEntity> goodsList, List<ReviewEntity> reviewEntities) {
        this.userId = userId;
        this.userName = userName;
        this.userHead = userHead;
        this.userSex = userSex;
        this.shareName = shareName;
        this.shareUrl = shareUrl;
        this.shareText = shareText;
        this.isPublic = isPublic;
        this.reportCount = reportCount;
        this.type = type;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.goodsList = goodsList;
        this.reviewEntities = reviewEntities;
    }

    public Integer getReportCount() {
        return reportCount;
    }

    public void setReportCount(Integer reportCount) {
        this.reportCount = reportCount;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public SocialPublicEntity() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getShareId() {
        return shareId;
    }

    public void setShareId(String shareId) {
        this.shareId = shareId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserHead() {
        return userHead;
    }

    public void setUserHead(String userHead) {
        this.userHead = userHead;
    }

    public Integer getUserSex() {
        return userSex;
    }

    public void setUserSex(Integer userSex) {
        this.userSex = userSex;
    }

    public String getShareName() {
        return shareName;
    }

    public void setShareName(String shareName) {
        this.shareName = shareName;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public String getShareText() {
        return shareText;
    }

    public void setShareText(String shareText) {
        this.shareText = shareText;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public Integer getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Integer isPublic) {
        this.isPublic = isPublic;
    }

    public List<GoodsEntity> getGoodsList() {
        return goodsList;
    }

    public void setGoodsList(List<GoodsEntity> goodsList) {
        this.goodsList = goodsList;
    }

    public List<ReviewEntity> getReviewEntities() {
        return reviewEntities;
    }

    public void setReviewEntities(List<ReviewEntity> reviewEntities) {
        this.reviewEntities = reviewEntities;
    }
}