package com.mobile.carcare.carcare.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Province implements Parcelable {

    private String provinceId;
    private String provinceName;
    private String countryId;

    public Province() {
    }

    public Province(String provinceId, String provinceName, String countryId) {
        this.provinceId = provinceId;
        this.provinceName = provinceName;
        this.countryId = countryId;
    }

    protected Province(Parcel in) {
        provinceId = in.readString();
        provinceName = in.readString();
        countryId = in.readString();
    }

    public static final Creator<Province> CREATOR = new Creator<Province>() {
        @Override
        public Province createFromParcel(Parcel in) {
            return new Province(in);
        }

        @Override
        public Province[] newArray(int size) {
            return new Province[size];
        }
    };

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

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(provinceId);
        dest.writeString(provinceName);
        dest.writeString(countryId);
    }
}
