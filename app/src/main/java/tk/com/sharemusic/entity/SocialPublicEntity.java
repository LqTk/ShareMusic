package tk.com.sharemusic.entity;

public class SocialPublicEntity {
    private String shareid;

    private String userid;

    private String username;

    private String userhead;

    private Integer usersex;

    private String sharename;

    private String shareurl;

    private String sharetext;

    private Integer ispublic;

    private Integer goodscount;

    private Integer reviewcount;

    public SocialPublicEntity(String shareid, String userid, String username, String userhead, Integer usersex, String sharename, String shareurl, String sharetext, Integer ispublic, Integer goodscount, Integer reviewcount) {
        this.shareid = shareid;
        this.userid = userid;
        this.username = username;
        this.userhead = userhead;
        this.usersex = usersex;
        this.sharename = sharename;
        this.shareurl = shareurl;
        this.sharetext = sharetext;
        this.ispublic = ispublic;
        this.goodscount = goodscount;
        this.reviewcount = reviewcount;
    }

    public SocialPublicEntity(String userid, String username, String userhead, Integer usersex, String sharename, String shareurl, String sharetext, Integer ispublic, Integer goodscount, Integer reviewcount) {
        this.userid = userid;
        this.username = username;
        this.userhead = userhead;
        this.usersex = usersex;
        this.sharename = sharename;
        this.shareurl = shareurl;
        this.sharetext = sharetext;
        this.ispublic = ispublic;
        this.goodscount = goodscount;
        this.reviewcount = reviewcount;
    }

    public SocialPublicEntity() {
        super();
    }

    public String getShareid() {
        return shareid;
    }

    public void setShareid(String shareid) {
        this.shareid = shareid == null ? null : shareid.trim();
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid == null ? null : userid.trim();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public String getUserhead() {
        return userhead;
    }

    public void setUserhead(String userhead) {
        this.userhead = userhead == null ? null : userhead.trim();
    }

    public Integer getUsersex() {
        return usersex;
    }

    public void setUsersex(Integer usersex) {
        this.usersex = usersex;
    }

    public String getSharename() {
        return sharename;
    }

    public void setSharename(String sharename) {
        this.sharename = sharename == null ? null : sharename.trim();
    }

    public String getShareurl() {
        return shareurl;
    }

    public void setShareurl(String shareurl) {
        this.shareurl = shareurl == null ? null : shareurl.trim();
    }

    public String getSharetext() {
        return sharetext;
    }

    public void setSharetext(String sharetext) {
        this.sharetext = sharetext == null ? null : sharetext.trim();
    }

    public Integer getIspublic() {
        return ispublic;
    }

    public void setIspublic(Integer ispublic) {
        this.ispublic = ispublic;
    }

    public Integer getGoodscount() {
        return goodscount;
    }

    public void setGoodscount(Integer goodscount) {
        this.goodscount = goodscount;
    }

    public Integer getReviewcount() {
        return reviewcount;
    }

    public void setReviewcount(Integer reviewcount) {
        this.reviewcount = reviewcount;
    }
}