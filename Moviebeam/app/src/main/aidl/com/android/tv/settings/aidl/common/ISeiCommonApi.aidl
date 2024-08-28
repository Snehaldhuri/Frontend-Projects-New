package com.android.tv.settings.aidl.common;


//import com.android.tv.settings.aidl.common.IStorageCallback;

interface ISeiCommonApi {

    String getDeviceSn();

    String getDeviceModelName();

    String getSoftwareVersion();

    String getBootloaderVersion();

    String getBrandName();

    String getEthMac();

    String getDeviceWifiMac();

    String getSystemTime();

    void setSystemTime(int year, int month, int date, int hourOfDay, int minute, int second);

    String getTimeZone();

    void setTimeZone(String value);

    boolean getWifiStatus();

    boolean getEthStatus();

    void rebootDevice();

    String getSystemMemory();

    int getGoogleAccountsNumber();

    int getAccountsByTypeNumber(String type);

}
