package com.diipl.moviebeam.dtv;

import android.os.Parcel;
import android.os.Parcelable;

public class FrequencyBean implements Parcelable {
    public static final Creator<FrequencyBean> CREATOR = new Creator<FrequencyBean>() {
        public FrequencyBean createFromParcel(Parcel parcel) {
            return new FrequencyBean(parcel);
        }

        public FrequencyBean[] newArray(int i) {
            return new FrequencyBean[i];
        }
    };
    private int bandWidthIndex;
    private String channelNo;
    private long frequency;
    private int modeIndex;
    private String name;
    private int symbolRate;

    public int describeContents() {
        return 0;
    }

    public FrequencyBean() {
    }

    protected FrequencyBean(Parcel parcel) {
        this.channelNo = parcel.readString();
        this.name = parcel.readString();
        this.frequency = parcel.readLong();
        this.bandWidthIndex = parcel.readInt();
        this.modeIndex = parcel.readInt();
        this.symbolRate = parcel.readInt();
    }

    public String getChannelNo() {
        return this.channelNo;
    }

    public void setChannelNo(String str) {
        this.channelNo = str;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public long getFrequency() {
        return this.frequency;
    }

    public void setFrequency(long j) {
        this.frequency = j;
    }

    public int getBandWidthIndex() {
        return this.bandWidthIndex;
    }

    public void setBandWidthIndex(int i) {
        this.bandWidthIndex = i;
    }

    public int getModeIndex() {
        return this.modeIndex;
    }

    public void setModeIndex(int i) {
        this.modeIndex = i;
    }

    public int getSymbolRate() {
        return this.symbolRate;
    }

    public void setSymbolRate(int i) {
        this.symbolRate = i;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.channelNo);
        parcel.writeString(this.name);
        parcel.writeLong(this.frequency);
        parcel.writeInt(this.bandWidthIndex);
        parcel.writeInt(this.modeIndex);
        parcel.writeInt(this.symbolRate);
    }
}
