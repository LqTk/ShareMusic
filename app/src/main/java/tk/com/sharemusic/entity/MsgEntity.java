package tk.com.sharemusic.entity;

public class MsgEntity {
    private String peopleName;
    private String peopleId;
    private String peopleHead;
    private int peopleSex;
    private String peopleDes;
    private String setName;
    private String partnerId;
    private int peopleAge;
    private boolean isConcern;

    public MsgEntity(String peopleName, String peopleId, String peopleHead, int peopleSex, String peopleDes, String setName, int peopleAge, boolean isConcern) {
        this.peopleName = peopleName;
        this.peopleId = peopleId;
        this.peopleHead = peopleHead;
        this.peopleSex = peopleSex;
        this.peopleDes = peopleDes;
        this.setName = setName;
        this.peopleAge = peopleAge;
        this.isConcern = isConcern;
    }

    public MsgEntity(String peopleName, String peopleId, String peopleHead, int peopleSex, String peopleDes, int peopleAge, boolean isConcern) {
        this.peopleName = peopleName;
        this.peopleId = peopleId;
        this.peopleHead = peopleHead;
        this.peopleSex = peopleSex;
        this.peopleDes = peopleDes;
        this.peopleAge = peopleAge;
        this.isConcern = isConcern;
    }

    public MsgEntity(String peopleName, String peopleId, String peopleHead, int peopleSex, String peopleDes, String setName, String partnerId, int peopleAge, boolean isConcern) {
        this.peopleName = peopleName;
        this.peopleId = peopleId;
        this.peopleHead = peopleHead;
        this.peopleSex = peopleSex;
        this.peopleDes = peopleDes;
        this.setName = setName;
        this.partnerId = partnerId;
        this.peopleAge = peopleAge;
        this.isConcern = isConcern;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public String getSetName() {
        return setName;
    }

    public void setSetName(String setName) {
        this.setName = setName;
    }

    public String getPeopleName() {
        return peopleName;
    }

    public void setPeopleName(String peopleName) {
        this.peopleName = peopleName;
    }

    public String getPeopleId() {
        return peopleId;
    }

    public void setPeopleId(String peopleId) {
        this.peopleId = peopleId;
    }

    public String getPeopleHead() {
        return peopleHead;
    }

    public void setPeopleHead(String peopleHead) {
        this.peopleHead = peopleHead;
    }

    public int getPeopleSex() {
        return peopleSex;
    }

    public void setPeopleSex(int peopleSex) {
        this.peopleSex = peopleSex;
    }

    public String getPeopleDes() {
        return peopleDes;
    }

    public void setPeopleDes(String peopleDes) {
        this.peopleDes = peopleDes;
    }

    public int getPeopleAge() {
        return peopleAge;
    }

    public void setPeopleAge(int peopleAge) {
        this.peopleAge = peopleAge;
    }

    public boolean isConcern() {
        return isConcern;
    }

    public void setConcern(boolean concern) {
        isConcern = concern;
    }
}
