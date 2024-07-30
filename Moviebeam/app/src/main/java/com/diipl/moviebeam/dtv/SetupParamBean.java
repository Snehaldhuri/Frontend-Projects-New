package com.diipl.moviebeam.dtv;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Patterns;

import androidx.exifinterface.media.ExifInterface;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


public class SetupParamBean implements Parcelable, Cloneable {
    public static final Creator<SetupParamBean> CREATOR = new Creator<SetupParamBean>() {
        public SetupParamBean createFromParcel(Parcel parcel) {
            return new SetupParamBean(parcel);
        }

        public SetupParamBean[] newArray(int i) {
            return new SetupParamBean[i];
        }
    };
    public int annexModeIndex;
    public String countryCode;
    public String deviceTenant;
    public List<FrequencyBean> frequencyList;
    public boolean isAutoScan;
    public boolean isBlindScan;
    public boolean isClearData;
    public boolean isDtvKitInput;
    public boolean isFastScan;
    public boolean isIfIfMode;
    public boolean isPowerOn;
    public boolean isTkgsScan;
    public boolean isUseNit;
    public String receiveIntentParams;
    public List<SatelliteBean> satelliteList;
    public boolean saveParamsStatus;
    public int scanType;
    public int searchMode;
    public int serviceType;
    public TransponderBean singleTransponder;
    public Map<Long, List<TransponderBean>> transponderMap;
    public String tunerType;

    public int describeContents() {
        return 0;
    }

    public SetupParamBean clone() {
        SetupParamBean setupParamBean = null;
        try {
            SetupParamBean setupParamBean2 = (SetupParamBean) super.clone();
            setupParamBean2.receiveIntentParams = this.receiveIntentParams;
            setupParamBean2.countryCode = this.countryCode;
            setupParamBean2.tunerType = this.tunerType;
            setupParamBean2.searchMode = this.searchMode;
            setupParamBean2.frequencyList = this.frequencyList;
            setupParamBean2.annexModeIndex = this.annexModeIndex;
            setupParamBean2.isUseNit = this.isUseNit;
            setupParamBean2.isBlindScan = this.isBlindScan;
            setupParamBean2.scanType = this.scanType;
            setupParamBean2.serviceType = this.serviceType;
            setupParamBean2.satelliteList = this.satelliteList;
            setupParamBean2.transponderMap = this.transponderMap;
            setupParamBean2.isDtvKitInput = this.isDtvKitInput;
            setupParamBean2.isFastScan = this.isFastScan;
            setupParamBean2.isIfIfMode = this.isIfIfMode;
            setupParamBean2.isTkgsScan = this.isTkgsScan;
            setupParamBean2.deviceTenant = this.deviceTenant;
            setupParamBean2.singleTransponder = this.singleTransponder;
            setupParamBean2.saveParamsStatus = this.saveParamsStatus;
            setupParamBean2.isClearData = this.isClearData;
            setupParamBean2.isAutoScan = this.isAutoScan;
            setupParamBean2.isPowerOn = this.isPowerOn;
            return setupParamBean2;
        } catch (CloneNotSupportedException e2) {

            return setupParamBean;
        }
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.receiveIntentParams);
        parcel.writeString(this.countryCode);
        parcel.writeString(this.tunerType);
        parcel.writeInt(this.searchMode);
        parcel.writeTypedList(this.frequencyList);
        parcel.writeInt(this.annexModeIndex);
        parcel.writeByte(this.isUseNit ? (byte) 1 : 0);
        parcel.writeByte(this.isBlindScan ? (byte) 1 : 0);
        parcel.writeInt(this.scanType);
        parcel.writeInt(this.serviceType);
        parcel.writeTypedList(this.satelliteList);
        if (this.transponderMap == null) {
            this.transponderMap = new HashMap();
        }
        parcel.writeInt(this.transponderMap.size());
        for (Map.Entry next : this.transponderMap.entrySet()) {
            parcel.writeValue(next.getKey());
            parcel.writeTypedList((List) next.getValue());
        }
        parcel.writeByte(this.isDtvKitInput ? (byte) 1 : 0);
        parcel.writeByte(this.isFastScan ? (byte) 1 : 0);
        parcel.writeByte(this.isIfIfMode ? (byte) 1 : 0);
        parcel.writeByte(this.isTkgsScan ? (byte) 1 : 0);
        parcel.writeString(this.deviceTenant);
        parcel.writeParcelable(this.singleTransponder, i);
        parcel.writeByte(this.saveParamsStatus ? (byte) 1 : 0);
        parcel.writeByte(this.isClearData ? (byte) 1 : 0);
        parcel.writeByte(this.isAutoScan ? (byte) 1 : 0);
        parcel.writeByte(this.isPowerOn ? (byte) 1 : 0);
    }

    public void readFromParcel(Parcel parcel) {
        this.receiveIntentParams = parcel.readString();
        this.countryCode = parcel.readString();
        this.tunerType = parcel.readString();
        this.searchMode = parcel.readInt();
        this.frequencyList = parcel.createTypedArrayList(FrequencyBean.CREATOR);
        this.annexModeIndex = parcel.readInt();
        boolean z = true;
        this.isUseNit = parcel.readByte() != 0;
        this.isBlindScan = parcel.readByte() != 0;
        this.scanType = parcel.readInt();
        this.serviceType = parcel.readInt();
        this.satelliteList = parcel.createTypedArrayList(SatelliteBean.CREATOR);
        int readInt = parcel.readInt();
        this.transponderMap = new HashMap(readInt);
        for (int i = 0; i < readInt; i++) {
            this.transponderMap.put((Long) parcel.readValue(Long.class.getClassLoader()), parcel.createTypedArrayList(TransponderBean.CREATOR));
        }
        this.isDtvKitInput = parcel.readByte() != 0;
        this.isFastScan = parcel.readByte() != 0;
        this.isIfIfMode = parcel.readByte() != 0;
        this.isTkgsScan = parcel.readByte() != 0;
        this.deviceTenant = parcel.readString();
        this.singleTransponder = (TransponderBean) parcel.readParcelable(TransponderBean.class.getClassLoader());
        this.saveParamsStatus = parcel.readByte() != 0;
        this.isClearData = parcel.readByte() != 0;
        this.isAutoScan = parcel.readByte() != 0;
        if (parcel.readByte() == 0) {
            z = false;
        }
        this.isPowerOn = z;
    }

    public SetupParamBean() {
    }

    protected SetupParamBean(Parcel parcel) {
        this.receiveIntentParams = parcel.readString();
        this.countryCode = parcel.readString();
        this.tunerType = parcel.readString();
        this.searchMode = parcel.readInt();
        this.frequencyList = parcel.createTypedArrayList(FrequencyBean.CREATOR);
        this.annexModeIndex = parcel.readInt();
        boolean z = true;
        this.isUseNit = parcel.readByte() != 0;
        this.isBlindScan = parcel.readByte() != 0;
        this.scanType = parcel.readInt();
        this.serviceType = parcel.readInt();
        this.satelliteList = parcel.createTypedArrayList(SatelliteBean.CREATOR);
        int readInt = parcel.readInt();
        this.transponderMap = new HashMap(readInt);
        for (int i = 0; i < readInt; i++) {
            this.transponderMap.put((Long) parcel.readValue(Long.class.getClassLoader()), parcel.createTypedArrayList(TransponderBean.CREATOR));
        }
        this.isDtvKitInput = parcel.readByte() != 0;
        this.isFastScan = parcel.readByte() != 0;
        this.isIfIfMode = parcel.readByte() != 0;
        this.isTkgsScan = parcel.readByte() != 0;
        this.deviceTenant = parcel.readString();
        this.singleTransponder = (TransponderBean) parcel.readParcelable(TransponderBean.class.getClassLoader());
        this.saveParamsStatus = parcel.readByte() != 0;
        this.isClearData = parcel.readByte() != 0;
        this.isAutoScan = parcel.readByte() != 0;
        this.isPowerOn = parcel.readByte() == 0 ? false : z;
    }

    public String toString() {
        return "SetupParamBean{receiveIntentParams='" + this.receiveIntentParams + '\'' + ", countryCode='" + this.countryCode + '\'' + ", tunerType='" + this.tunerType + '\'' + ", searchMode=" + this.searchMode + ", frequencyList=" + this.frequencyList + ", annexModeIndex=" + this.annexModeIndex + ", isUseNit=" + this.isUseNit + ", isBlindScan=" + this.isBlindScan + ", scanType=" + this.scanType + ", serviceType=" + this.serviceType + ", satelliteList=" + this.satelliteList + ", isDtvKitInput=" + this.isDtvKitInput + ", isFastScan=" + this.isFastScan + ", isIfIfMode=" + this.isIfIfMode + ", isTkgsScan=" + this.isTkgsScan + ", deviceTenant='" + this.deviceTenant + '\'' + ", singleTransponder=" + getTpString(this.singleTransponder, this.tunerType) + ", saveParamsStatus=" + this.saveParamsStatus + ", isClearData=" + this.isClearData + '}';
    }

    private static String getTpString(TransponderBean transponderBean, String str) {
        if (transponderBean == null || str == null) {
            return "--/--";
        }
        StringBuilder sb = new StringBuilder();
        if (str.equals(DvbContract.Tuner.DVB_S)) {
            sb.append(transponderBean.getFreq() / 1000);
            sb.append("/");
            sb.append(new String[]{"H", ExifInterface.GPS_MEASUREMENT_INTERRUPTED}[transponderBean.getPolar()]);
            sb.append("/");
            sb.append(transponderBean.getSymbol() / 1000);
        } else if (str.equals(DvbContract.Tuner.DVB_C)) {
            sb.append(transponderBean.getFreq());
            sb.append("/");
            sb.append(transponderBean.getSymbol());
            sb.append("/");
            sb.append(new String[]{"Auto", "4QAM", "8QAM", "16QAM", "32QAM", "64QAM", "128QAM", "256QAM"}[transponderBean.getMod()]);
        } else {
            sb.append(transponderBean.getFreq());
            sb.append("/");
            sb.append(new String[]{"8MHZ", "7MHZ", "6MHZ", "5MHZ", "10MHZ"}[transponderBean.getBw()]);
        }
        return sb.toString();
    }
}
