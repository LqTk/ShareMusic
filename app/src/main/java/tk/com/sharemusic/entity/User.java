package tk.com.sharemusic.entity;

import java.io.Serializable;
import java.util.Date;

import tk.com.sharemusic.network.BaseResult;

public class User implements Serializable {
    private String userId;

    private String userName;

    private String headImg;

    private Integer sex;

    private Integer age;

    private String birthday;

    private String phone;

    private String des;

    public User(String userId, String userName, String headImg, Integer sex, Integer age, String birthday, String phone, String des) {
        this.userId = userId;
        this.userName = userName;
        this.headImg = headImg;
        this.sex = sex;
        this.age = age;
        this.birthday = birthday;
        this.phone = phone;
        this.des = des;
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

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
}
