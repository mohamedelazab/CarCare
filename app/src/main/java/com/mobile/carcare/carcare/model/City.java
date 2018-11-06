package com.mobile.carcare.carcare.model;

import android.os.Parcel;
import android.os.Parcelable;

public class City implements Parcelable {

    private String cityId;
    private String cityName;
    private String provinceId;

    public City() {
    }

    public City(String cityId, String cityName, String provinceId) {
        this.cityId = cityId;
        this.cityName = cityName;
        this.provinceId = provinceId;
    }

    protected City(Parcel in) {
        cityId = in.readString();
        cityName = in.readString();
        provinceId = in.readString();
    }

    public static final Creator<City> CREATOR = new Creator<City>() {
        @Override
        public City createFromParcel(Parcel in) {
            return new City(in);
        }

        @Override
        public City[] newArray(int size) {
            return new City[size];
        }
    };

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

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(cityId);
        dest.writeString(cityName);
        dest.writeString(provinceId);
    }
}
