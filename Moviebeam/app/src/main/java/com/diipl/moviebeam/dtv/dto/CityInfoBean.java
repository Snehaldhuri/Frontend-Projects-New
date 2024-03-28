package com.diipl.moviebeam.dtv.dto;

import android.os.Parcel;
import android.os.Parcelable;

public class CityInfoBean implements Parcelable {
    public static final Creator<CityInfoBean> CREATOR = new Creator<CityInfoBean>() {
        public CityInfoBean createFromParcel(Parcel parcel) {
            return new CityInfoBean(parcel);
        }

        public CityInfoBean[] newArray(int i) {
            return new CityInfoBean[i];
        }
    };
    public int city_code;
    public String city_name;
    public int country_code;
    public String country_name;
    public int region_code;
    public String region_name;
    public int wide_area_code;
    public String wide_area_name;

    public int describeContents() {
        return 0;
    }

    public CityInfoBean() {
    }

    protected CityInfoBean(Parcel parcel) {
        this.country_name = parcel.readString();
        this.country_code = parcel.readInt();
        this.wide_area_name = parcel.readString();
        this.wide_area_code = parcel.readInt();
        this.region_name = parcel.readString();
        this.region_code = parcel.readInt();
        this.city_name = parcel.readString();
        this.city_code = parcel.readInt();
    }

    public String getCountry_name() {
        return this.country_name;
    }

    public void setCountry_name(String str) {
        this.country_name = str;
    }

    public int getCountry_code() {
        return this.country_code;
    }

    public void setCountry_code(int i) {
        this.country_code = i;
    }

    public String getWide_area_name() {
        return this.wide_area_name;
    }

    public void setWide_area_name(String str) {
        this.wide_area_name = str;
    }

    public int getWide_area_code() {
        return this.wide_area_code;
    }

    public void setWide_area_code(int i) {
        this.wide_area_code = i;
    }

    public String getRegion_name() {
        return this.region_name;
    }

    public void setRegion_name(String str) {
        this.region_name = str;
    }

    public int getRegion_code() {
        return this.region_code;
    }

    public void setRegion_code(int i) {
        this.region_code = i;
    }

    public String getCity_name() {
        return this.city_name;
    }

    public void setCity_name(String str) {
        this.city_name = str;
    }

    public int getCity_code() {
        return this.city_code;
    }

    public void setCity_code(int i) {
        this.city_code = i;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.country_name);
        parcel.writeInt(this.country_code);
        parcel.writeString(this.wide_area_name);
        parcel.writeInt(this.wide_area_code);
        parcel.writeString(this.region_name);
        parcel.writeInt(this.region_code);
        parcel.writeString(this.city_name);
        parcel.writeInt(this.city_code);
    }
}
