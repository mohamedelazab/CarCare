package com.mobile.carcare.carcare.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Agency implements Parcelable {

    private String name;
    private String avatar;
    private String header;
    private String lat;
    private String lng;
    private String address;
    private String countryId;
    private String countryName;
    private String provinceId;
    private String provinceName;
    private String cityId;
    private String cityName;
    private String email;
    private String phoneNumber;
    private String description;
    private String key;
    private boolean isInFavorites;

    public Agency() {
    }

    public Agency(String name, String avatar, String header, String lat, String lng, String address, String countryId, String countryName, String provinceId, String provinceName, String cityId, String cityName, String email, String phoneNumber, String description, String key) {
        this.name = name;
        this.avatar = avatar;
        this.header = header;
        this.lat = lat;
        this.lng = lng;
        this.address = address;
        this.countryId = countryId;
        this.countryName = countryName;
        this.provinceId = provinceId;
        this.provinceName = provinceName;
        this.cityId = cityId;
        this.cityName = cityName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.description = description;
        this.key =key;
    }

    protected Agency(Parcel in) {
        name = in.readString();
        avatar = in.readString();
        header = in.readString();
        lat = in.readString();
        lng = in.readString();
        address = in.readString();
        countryId = in.readString();
        countryName = in.readString();
        provinceId = in.readString();
        provinceName = in.readString();
        cityId = in.readString();
        cityName = in.readString();
        email = in.readString();
        phoneNumber = in.readString();
        description = in.readString();
        key = in.readString();
        isInFavorites = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(avatar);
        dest.writeString(header);
        dest.writeString(lat);
        dest.writeString(lng);
        dest.writeString(address);
        dest.writeString(countryId);
        dest.writeString(countryName);
        dest.writeString(provinceId);
        dest.writeString(provinceName);
        dest.writeString(cityId);
        dest.writeString(cityName);
        dest.writeString(email);
        dest.writeString(phoneNumber);
        dest.writeString(description);
        dest.writeString(key);
        dest.writeByte((byte) (isInFavorites ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Agency> CREATOR = new Creator<Agency>() {
        @Override
        public Agency createFromParcel(Parcel in) {
            return new Agency(in);
        }

        @Override
        public Agency[] newArray(int size) {
            return new Agency[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isInFavorites() {
        return isInFavorites;
    }

    public void setInFavorites(boolean inFavorites) {
        isInFavorites = inFavorites;
    }

}