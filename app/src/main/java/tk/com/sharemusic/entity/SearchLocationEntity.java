package tk.com.sharemusic.entity;

public class SearchLocationEntity {
    public String city;
    public String address;
    public double latitude;
    public double longitude;
    public boolean isCheck;

    public SearchLocationEntity(String city, String address, double latitude, double longitude, boolean isCheck) {
        this.city = city;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isCheck = isCheck;
    }

    public SearchLocationEntity(String city, String address, boolean isCheck) {
        this.city = city;
        this.address = address;
        this.isCheck = isCheck;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }
}
