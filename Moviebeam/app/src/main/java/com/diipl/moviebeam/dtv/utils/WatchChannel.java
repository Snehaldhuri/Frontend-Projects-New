package com.diipl.moviebeam.dtv.utils;

import android.database.Cursor;

public class WatchChannel {
    public static String[] PROJECTION = {"channel_name", "channel_id", "channel_type", "display_number", "service_id", "service_type", "network_id", "ts_id", "is_favorite", "watch_duration", "timestamp"};
    private boolean isFavorite;
    private long mChannelId;
    private String mChannelName;
    private String mDisplayNumber;
    private long mNetWorkId;
    private long mServiceId;
    private String mServiceType;
    private long mTimestamp;
    private long mTransportStreamId;
    private String mType;
    private long mWatchDuration;

    public String getChannelName() {
        return this.mChannelName;
    }

    public void setChannelName(String str) {
        this.mChannelName = str;
    }

    public long getChannelId() {
        return this.mChannelId;
    }

    public void setChannelId(long j) {
        this.mChannelId = j;
    }

    public String getType() {
        return this.mType;
    }

    public void setType(String str) {
        this.mType = str;
    }

    public long getServiceId() {
        return this.mServiceId;
    }

    public void setServiceId(long j) {
        this.mServiceId = j;
    }

    public String getServiceType() {
        return this.mServiceType;
    }

    public void setServiceType(String str) {
        this.mServiceType = str;
    }

    public long getNetWorkId() {
        return this.mNetWorkId;
    }

    public void setNetWorkId(long j) {
        this.mNetWorkId = j;
    }

    public long getTransportStreamId() {
        return this.mTransportStreamId;
    }

    public void setTransportStreamId(long j) {
        this.mTransportStreamId = j;
    }

    public boolean isFavorite() {
        return this.isFavorite;
    }

    public void setFavorite(boolean z) {
        this.isFavorite = z;
    }

    public long getWatchDuration() {
        return this.mWatchDuration;
    }

    public void setWatchDuration(long j) {
        this.mWatchDuration = j;
    }

    public long getTimestamp() {
        return this.mTimestamp;
    }

    public void setTimestamp(long j) {
        this.mTimestamp = j;
    }

    public String getDisplayNumber() {
        return this.mDisplayNumber;
    }

    public void setDisplayNumber(String str) {
        this.mDisplayNumber = str;
    }

    public static WatchChannel fromCursor(Cursor cursor) {
        WatchChannel watchChannel = new WatchChannel();
        boolean z = false;
        watchChannel.setChannelName(cursor.getString(0));
        watchChannel.setChannelId(cursor.getLong(1));
        watchChannel.setType(cursor.getString(2));
        watchChannel.setDisplayNumber(cursor.getString(3));
        watchChannel.setServiceId((long) cursor.getInt(4));
        watchChannel.setServiceType(cursor.getString(5));
        watchChannel.setNetWorkId((long) cursor.getInt(6));
        watchChannel.setTransportStreamId((long) cursor.getInt(7));
        if (cursor.getInt(8) == 1) {
            z = true;
        }
        watchChannel.isFavorite = z;
        watchChannel.setWatchDuration(cursor.getLong(9));
        watchChannel.setTimestamp(cursor.getLong(10));
        return watchChannel;
    }

    public String toString() {
        return "WatchChannel{mChannelName='" + this.mChannelName + '\'' + ", mChannelId=" + this.mChannelId + ", mType='" + this.mType + '\'' + ", mDisplayNumber='" + this.mDisplayNumber + '\'' + ", mServiceId=" + this.mServiceId + ", mServiceType='" + this.mServiceType + '\'' + ", mNetWorkId=" + this.mNetWorkId + ", mTransportStreamId=" + this.mTransportStreamId + ", isFavorite=" + this.isFavorite + ", mWatchDuration=" + this.mWatchDuration + ", mTimestamp=" + this.mTimestamp + '}';
    }
}
