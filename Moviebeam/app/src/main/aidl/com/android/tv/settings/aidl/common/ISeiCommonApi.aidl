package com.android.tv.settings.aidl.common;


//import com.android.tv.settings.aidl.common.IStorageCallback;
import com.android.tv.settings.aidl.regular.ISoftAPConfigureCallback;

interface ISeiCommonApi {

    String getDeviceSn();

    String getDeviceModelName();

    String getSoftwareVersion();

    String getBootloaderVersion();

    String getBrandName();

    String getEthMac();

    String getDeviceWifiMac();

    String getTimeZone();

    void setTimeZone(String value);

    int getGoogleAccountsNumber();

    int getAccountsByTypeNumber(String type);


    //[SEI-luoyf-2024-7-1] add common api {
    String getSystemTime();

    void setSystemTime(int year, int month, int date, int hourOfDay, int minute, int second);

    boolean getWifiStatus();

    boolean getEthStatus();

    void rebootDevice();

    String getSystemMemory();
    //[SEI-luoyf-2024-7-1] add common api }

    //[SEI-luoyf-2024-8-9] add api {
    void openSoftAP(boolean status, in ISoftAPConfigureCallback callback);

    String getSoftApStatus();
    //[SEI-luoyf-2024-8-9] add api }

}
