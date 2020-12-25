package tk.com.sharemusic.enums;

import tk.com.sharemusic.R;

public enum Gender {
    MAN(0, "男","MAN", R.drawable.default_head_boy),
    WOMAN(1,"女","WOMAN",R.drawable.default_head_girl);
    private int code;
    private String name;
    private String flagName;
    private int image;

    Gender(int code, String name, String flagName, int image) {
        this.code = code;
        this.name = name;
        this.flagName = flagName;
        this.image = image;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFlagName() {
        return flagName;
    }

    public void setFlagName(String flagName) {
        this.flagName = flagName;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public static String getName(int typeCode){
        for (Gender gender:Gender.values()){
            if (gender.getCode() == typeCode){
                return gender.name;
            }
        }
        return null;
    }

    public static int getImage(int typeCode){
        for (Gender gender:Gender.values()){
            if (gender.getCode() == typeCode){
                return gender.image;
            }
        }
        return -1;
    }

    public static int getCode(String name){
        for (Gender gender:Gender.values()){
            if (gender.getName().equals(name)){
                return gender.code;
            }
        }
        return -1;
    }
}
