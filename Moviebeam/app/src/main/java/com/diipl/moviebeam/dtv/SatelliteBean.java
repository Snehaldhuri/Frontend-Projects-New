package com.diipl.moviebeam.dtv;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class SatelliteBean implements Cloneable, Parcelable {
    public static final Creator<SatelliteBean> CREATOR = new Creator<SatelliteBean>() {
        public SatelliteBean createFromParcel(Parcel parcel) {
            return new SatelliteBean(parcel);
        }

        public SatelliteBean[] newArray(int i) {
            return new SatelliteBean[i];
        }
    };
    public static final String[] DEFAULT_PROJECTION = getProjection();
    public static final int UNICABLE_1 = 0;
    public static final int UNICABLE_2 = 1;
    public static final int UNICABLE_SWITCH_OFF = 0;
    public static final int UNICABLE_SWITCH_ON = 1;
    private int cmdOrder;
    private int customHighLnb;
    private int customHighLnbMin;
    private int customHighThreshold;
    private int customLowLnb;
    private int customLowLnbMax;
    private int customLowLnbMin;
    private int disEqc1_0;
    private int disEqc1_1;
    private int disEqcMode;
    private int fastDisEqc;
    /* access modifiers changed from: private */
    public Long id;
    private int laDirection;
    private float latitude;
    /* access modifiers changed from: private */
    public int lnbNumber;
    /* access modifiers changed from: private */
    public int lnbType;
    private int lnbVoltage;
    private int loDirection;
    /* access modifiers changed from: private */
    public int lofHi;
    /* access modifiers changed from: private */
    public int lofLo;
    private int lofThreshold;
    private float longitude;
    private int motorNumber;
    /* access modifiers changed from: private */
    public String name;
    private int posNumber;
    private int repeatCount;
    private int satLongitude;
    private boolean selected;
    private int sequenceRepeat;
    private int signal22Khz;
    private int toneBurst;
    private long unFrequency;
    private String uniCableDataString;
    private int uniCableFormat;
    private int uniCablePosition;
    private int uniCableSwitch;
    private int userBand;

    public int describeContents() {
        return 0;
    }

    public SatelliteBean() {
    }

    protected SatelliteBean(Parcel parcel) {
        this.name = parcel.readString();
        if (parcel.readByte() == 0) {
            this.id = null;
        } else {
            this.id = Long.valueOf(parcel.readLong());
        }
        this.lnbNumber = parcel.readInt();
        this.lnbType = parcel.readInt();
        this.lofHi = parcel.readInt();
        this.lofLo = parcel.readInt();
        this.lofThreshold = parcel.readInt();
        this.signal22Khz = parcel.readInt();
        this.lnbVoltage = parcel.readInt();
        this.motorNumber = parcel.readInt();
        this.posNumber = parcel.readInt();
        this.loDirection = parcel.readInt();
        this.laDirection = parcel.readInt();
        this.longitude = parcel.readFloat();
        this.latitude = parcel.readFloat();
        this.satLongitude = parcel.readInt();
        this.disEqcMode = parcel.readInt();
        this.toneBurst = parcel.readInt();
        this.disEqc1_0 = parcel.readInt();
        this.disEqc1_1 = parcel.readInt();
        this.repeatCount = parcel.readInt();
        this.sequenceRepeat = parcel.readInt();
        this.fastDisEqc = parcel.readInt();
        this.cmdOrder = parcel.readInt();
        this.selected = parcel.readByte() != 0;
        this.customLowLnb = parcel.readInt();
        this.customLowLnbMin = parcel.readInt();
        this.customLowLnbMax = parcel.readInt();
        this.customHighLnb = parcel.readInt();
        this.customHighLnbMin = parcel.readInt();
        this.customHighThreshold = parcel.readInt();
        this.uniCableFormat = parcel.readInt();
        this.uniCableDataString = parcel.readString();
        this.uniCableSwitch = parcel.readInt();
        this.userBand = parcel.readInt();
        this.unFrequency = parcel.readLong();
        this.uniCablePosition = parcel.readInt();
    }

    public SatelliteBean clone() {
        try {
            SatelliteBean satelliteBean = (SatelliteBean) super.clone();
            satelliteBean.setName(this.name);
            satelliteBean.setId(this.id);
            satelliteBean.setLnbNumber(this.lnbNumber);
            satelliteBean.setLnbType(this.lnbType);
            satelliteBean.setLofHi(this.lofHi);
            satelliteBean.setLofLo(this.lofLo);
            satelliteBean.setLofThreshold(this.lofThreshold);
            satelliteBean.setSignal22Khz(this.signal22Khz);
            satelliteBean.setLnbVoltage(this.lnbVoltage);
            satelliteBean.setMotorNumber(this.motorNumber);
            satelliteBean.setDishPosition(this.posNumber);
            satelliteBean.setLoDirection(this.loDirection);
            satelliteBean.setLaDirection(this.laDirection);
            satelliteBean.setLongitude(this.longitude);
            satelliteBean.setLatitude(this.latitude);
            satelliteBean.setDisEqcMode(this.disEqcMode);
            satelliteBean.setToneBurst(this.toneBurst);
            satelliteBean.setDisEqc1_0(this.disEqc1_0);
            satelliteBean.setDisEqc1_1(this.disEqc1_1);
            satelliteBean.setRepeatCount(this.repeatCount);
            satelliteBean.setSequenceRepeat(this.sequenceRepeat);
            satelliteBean.setFastDisEqc(this.fastDisEqc);
            satelliteBean.setCmdOrder(this.cmdOrder);
            satelliteBean.setSelected(this.selected);
            satelliteBean.setUniCableSwitch(this.uniCableSwitch);
            satelliteBean.setUniCableFormat(this.uniCableFormat);
            satelliteBean.setUniCableDataString(this.uniCableDataString);
            satelliteBean.setUserBand(this.userBand);
            satelliteBean.setUbFrequency(this.unFrequency);
            satelliteBean.setUniCablePosition(this.uniCablePosition);
            satelliteBean.setCustomLowLnb(this.customLowLnb);
            satelliteBean.setCustomLowLnbMin(this.customLowLnbMin);
            satelliteBean.setCustomLowLnbMax(this.customLowLnbMax);
            satelliteBean.setCustomHighLnb(this.customHighLnb);
            satelliteBean.setCustomHighLnbMin(this.customHighLnbMin);
            satelliteBean.setCustomHighThreshold(this.customHighThreshold);
            return satelliteBean;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean equals(Object obj) {
        SatelliteBean satelliteBean = (SatelliteBean) obj;
        Log.d("DebugActivity : ", "equals: " + satelliteBean);
        return satelliteBean.getId() == getId() && satelliteBean.getName().equals(getName()) && satelliteBean.getLnbNumber() == getLnbNumber() && satelliteBean.getSignal22Khz() == getSignal22Khz() && satelliteBean.getToneBurst() == getToneBurst() && satelliteBean.getLaDirection() == getLaDirection() && satelliteBean.getLoDirection() == getLoDirection() && satelliteBean.getSelected() == getSelected() && satelliteBean.getSatLongitude() == getSatLongitude() && satelliteBean.getMotorNumber() == getMotorNumber() && satelliteBean.getDisEqc1_1() == getDisEqc1_1() && satelliteBean.getDisEqc1_0() == getDisEqc1_0() && satelliteBean.getCmdOrder() == getCmdOrder() && satelliteBean.getDisEqcMode() == getDisEqcMode() && satelliteBean.getFastDisEqc() == getFastDisEqc() && satelliteBean.getLofHi() == getLofHi() && satelliteBean.getLofLo() == getLofLo() && satelliteBean.getDishPosition() == getDishPosition() && satelliteBean.getSequenceRepeat() == getSequenceRepeat();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long l) {
        this.id = l;
    }

    public int getLnbNumber() {
        return this.lnbNumber;
    }

    public void setLnbNumber(int i) {
        this.lnbNumber = i;
    }

    public int getLnbType() {
        return this.lnbType;
    }

    public void setLnbType(int i) {
        this.lnbType = i;
    }

    public int getLofHi() {
        return this.lofHi;
    }

    public void setLofHi(int i) {
        this.lofHi = i;
    }

    public int getLofLo() {
        return this.lofLo;
    }

    public void setLofLo(int i) {
        this.lofLo = i;
    }

    public int getLofThreshold() {
        return this.lofThreshold;
    }

    public void setLofThreshold(int i) {
        this.lofThreshold = i;
    }

    public int getSignal22Khz() {
        return this.signal22Khz;
    }

    public void setSignal22Khz(int i) {
        this.signal22Khz = i;
    }

    public int getLnbVoltage() {
        return this.lnbVoltage;
    }

    public void setLnbVoltage(int i) {
        this.lnbVoltage = i;
    }

    public int getMotorNumber() {
        return this.motorNumber;
    }

    public void setMotorNumber(int i) {
        this.motorNumber = i;
    }

    public int getDishPosition() {
        return this.posNumber;
    }

    public void setDishPosition(int i) {
        this.posNumber = i;
    }

    public int getLoDirection() {
        return this.loDirection;
    }

    public void setLoDirection(int i) {
        this.loDirection = i;
    }

    public int getLaDirection() {
        return this.laDirection;
    }

    public void setLaDirection(int i) {
        this.laDirection = i;
    }

    public float getLongitude() {
        return this.longitude;
    }

    public void setLongitude(float f) {
        this.longitude = f;
    }

    public float getLatitude() {
        return this.latitude;
    }

    public void setLatitude(float f) {
        this.latitude = f;
    }

    public int getSatLongitude() {
        return this.satLongitude;
    }

    public void setSatLongitude(int i) {
        this.satLongitude = i;
    }

    public int getDisEqcMode() {
        return this.disEqcMode;
    }

    public void setDisEqcMode(int i) {
        this.disEqcMode = i;
    }

    public int getToneBurst() {
        return this.toneBurst;
    }

    public void setToneBurst(int i) {
        this.toneBurst = i;
    }

    public int getDisEqc1_0() {
        return this.disEqc1_0;
    }

    public void setDisEqc1_0(int i) {
        this.disEqc1_0 = i;
    }

    public int getDisEqc1_1() {
        return this.disEqc1_1;
    }

    public void setDisEqc1_1(int i) {
        this.disEqc1_1 = i;
    }

    public int getRepeatCount() {
        return this.repeatCount;
    }

    public void setRepeatCount(int i) {
        this.repeatCount = i;
    }

    public int getSequenceRepeat() {
        return this.sequenceRepeat;
    }

    public void setSequenceRepeat(int i) {
        this.sequenceRepeat = i;
    }

    public int getFastDisEqc() {
        return this.fastDisEqc;
    }

    public void setFastDisEqc(int i) {
        this.fastDisEqc = i;
    }

    public int getCmdOrder() {
        return this.cmdOrder;
    }

    public void setCmdOrder(int i) {
        this.cmdOrder = i;
    }

    public boolean getSelected() {
        return this.selected;
    }

    public void setSelected(boolean z) {
        this.selected = z;
    }

    public int getCustomLowLnb() {
        return this.customLowLnb;
    }

    public void setCustomLowLnb(int i) {
        this.customLowLnb = i;
    }

    public int getCustomLowLnbMin() {
        return this.customLowLnbMin;
    }

    public void setCustomLowLnbMin(int i) {
        this.customLowLnbMin = i;
    }

    public int getCustomLowLnbMax() {
        return this.customLowLnbMax;
    }

    public void setCustomLowLnbMax(int i) {
        this.customLowLnbMax = i;
    }

    public int getCustomHighLnb() {
        return this.customHighLnb;
    }

    public void setCustomHighLnb(int i) {
        this.customHighLnb = i;
    }

    public int getCustomHighLnbMin() {
        return this.customHighLnbMin;
    }

    public void setCustomHighLnbMin(int i) {
        this.customHighLnbMin = i;
    }

    public int getCustomHighThreshold() {
        return this.customHighThreshold;
    }

    public void setCustomHighThreshold(int i) {
        this.customHighThreshold = i;
    }

    public int getUniCableSwitch() {
        return this.uniCableSwitch;
    }

    public void setUniCableSwitch(int i) {
        this.uniCableSwitch = i;
    }

    public String getUniCableDataString() {
        return this.uniCableDataString;
    }

    public void setUniCableDataString(String str) {
        this.uniCableDataString = str;
    }

    public int getUniCableFormat() {
        return this.uniCableFormat;
    }

    public void setUniCableFormat(int i) {
        this.uniCableFormat = i;
    }

    public int getUserBand() {
        return this.userBand;
    }

    public void setUserBand(int i) {
        this.userBand = i;
    }

    public long getUbFrequency() {
        return this.unFrequency;
    }

    public void setUbFrequency(long j) {
        this.unFrequency = j;
    }

    public int getUniCablePosition() {
        return this.uniCablePosition;
    }

    public void setUniCablePosition(int i) {
        this.uniCablePosition = i;
    }

    public static SatelliteBean fromCursor(Cursor cursor) {
        Builder builder = new Builder();
        boolean z = false;
        Builder cmdOrder2 = builder.setId(cursor.getLong(0)).setName(cursor.getString(1)).setLnbNumber(cursor.getInt(2)).setLofHi(cursor.getInt(3)).setLofLo(cursor.getInt(4)).setLofThreshold(cursor.getInt(5)).setSignal22Khz(cursor.getInt(6)).setLnbVoltage(cursor.getInt(7)).setMotorNumber(cursor.getInt(8)).setDishPosition(cursor.getInt(9)).setLoDirection(cursor.getInt(10)).setLaDirection(cursor.getInt(11)).setLongitude(cursor.getFloat(12)).setLatitude(cursor.getFloat(13)).setSatLongitude(cursor.getInt(14)).setDisEqcMode(cursor.getInt(15)).setToneBurst(cursor.getInt(16)).setDisEqc1_0(cursor.getInt(17)).setDisEqc1_1(cursor.getInt(18)).setRepeatCount(cursor.getInt(19)).setSequenceRepeat(cursor.getInt(20)).setFastDisEqc(cursor.getInt(21)).setCmdOrder(cursor.getInt(22));
        if (cursor.getInt(23) == 1) {
            z = true;
        }
        cmdOrder2.setSelected(z).setLnbType(cursor.getInt(24)).setUniCableType(cursor.getInt(25)).setUniCableDataString(cursor.getString(26)).setUniCableSwitch(cursor.getInt(27)).setUserBand(cursor.getInt(28)).setUbFrequency((long) cursor.getInt(29)).setUnicablePosition(cursor.getInt(30)).setCustomLowLnb(cursor.getInt(31)).setCustomLowLnbMin(cursor.getInt(32)).setCustomLowLnbMax(cursor.getInt(33)).setCustomHighLnb(cursor.getInt(34)).setCustomHighLnbMin(cursor.getInt(35)).setCustomHighLnbMax(cursor.getInt(36));
        return builder.build();
    }

    public static SatelliteBean fromDefaultCursor(Cursor cursor) {
        Builder builder = new Builder();
        boolean z = false;
        Builder cmdOrder2 = builder.setId(cursor.getLong(0)).setName(cursor.getString(1)).setLnbNumber(cursor.getInt(2)).setLofHi(cursor.getInt(3)).setLofLo(cursor.getInt(4)).setLofThreshold(cursor.getInt(5)).setSignal22Khz(cursor.getInt(6)).setLnbVoltage(cursor.getInt(7)).setMotorNumber(cursor.getInt(8)).setDishPosition(cursor.getInt(9)).setLoDirection(cursor.getInt(10)).setLaDirection(cursor.getInt(11)).setLongitude(cursor.getFloat(12)).setLatitude(cursor.getFloat(13)).setSatLongitude(cursor.getInt(14)).setDisEqcMode(cursor.getInt(15)).setToneBurst(cursor.getInt(16)).setDisEqc1_0(cursor.getInt(17)).setDisEqc1_1(cursor.getInt(18)).setRepeatCount(cursor.getInt(19)).setSequenceRepeat(cursor.getInt(20)).setFastDisEqc(cursor.getInt(21)).setCmdOrder(cursor.getInt(22));
        if (cursor.getInt(23) == 1) {
            z = true;
        }
        cmdOrder2.setSelected(z);
        return builder.build();
    }

    public static SatelliteBean fromCursorByName(Cursor cursor) {
        Builder builder = new Builder();
        Builder cmdOrder2 = builder.setId(cursor.getLong(cursor.getColumnIndex("db_id"))).setName(cursor.getString(cursor.getColumnIndex("sat_name"))).setLnbNumber(cursor.getInt(cursor.getColumnIndex("lnb_num"))).setLnbType(cursor.getInt(cursor.getColumnIndex("lnb_type"))).setLofHi(cursor.getInt(cursor.getColumnIndex("lof_hi"))).setLofLo(cursor.getInt(cursor.getColumnIndex("lof_lo"))).setLofThreshold(cursor.getInt(cursor.getColumnIndex("lof_threshold"))).setSignal22Khz(cursor.getInt(cursor.getColumnIndex("signal_22khz"))).setLnbVoltage(cursor.getInt(cursor.getColumnIndex("voltage"))).setMotorNumber(cursor.getInt(cursor.getColumnIndex("motor_num"))).setDishPosition(cursor.getInt(cursor.getColumnIndex("pos_num"))).setLoDirection(cursor.getInt(cursor.getColumnIndex("lo_direction"))).setLaDirection(cursor.getInt(cursor.getColumnIndex("la_direction"))).setLongitude(cursor.getFloat(cursor.getColumnIndex("longitude"))).setLatitude(cursor.getFloat(cursor.getColumnIndex("la_direction"))).setSatLongitude(cursor.getInt(cursor.getColumnIndex("sat_longitude"))).setDisEqcMode(cursor.getInt(cursor.getColumnIndex("diseqc_mode"))).setToneBurst(cursor.getInt(cursor.getColumnIndex("tone_burst"))).setDisEqc1_0(cursor.getInt(cursor.getColumnIndex("committed_cmd"))).setDisEqc1_1(cursor.getInt(cursor.getColumnIndex("uncommitted_cmd"))).setRepeatCount(cursor.getInt(cursor.getColumnIndex("repeat_count"))).setSequenceRepeat(cursor.getInt(cursor.getColumnIndex("sequence_repeat"))).setFastDisEqc(cursor.getInt(cursor.getColumnIndex("fast_diseqc"))).setCmdOrder(cursor.getInt(cursor.getColumnIndex("cmd_order")));
        boolean z = true;
        if (cursor.getInt(cursor.getColumnIndex("selected")) != 1) {
            z = false;
        }
        cmdOrder2.setSelected(z).setUniCableType(cursor.getInt(cursor.getColumnIndex("int_one"))).setUniCableDataString(cursor.getString(cursor.getColumnIndex("string_one"))).setUniCableSwitch(cursor.getInt(cursor.getColumnIndex("unicable_switch"))).setUserBand(cursor.getInt(cursor.getColumnIndex("user_band"))).setUbFrequency(cursor.getLong(cursor.getColumnIndex("ub_frequency"))).setUnicablePosition(cursor.getInt(cursor.getColumnIndex("unicable_position"))).setCustomLowLnb(cursor.getInt(cursor.getColumnIndex("custom_low_lnb"))).setCustomLowLnbMin(cursor.getInt(cursor.getColumnIndex("custom_low_lnb_min"))).setCustomLowLnbMax(cursor.getInt(cursor.getColumnIndex("custom_low_lnb_max"))).setCustomHighLnb(cursor.getInt(cursor.getColumnIndex("custom_high_lnb"))).setCustomHighLnbMin(cursor.getColumnIndex("custom_high_lnb_min")).setCustomHighLnbMax(cursor.getInt(cursor.getColumnIndex("custom_high_lnb_max")));
        return builder.build();
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.name);
        if (this.id == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(this.id.longValue());
        }
        parcel.writeInt(this.lnbNumber);
        parcel.writeInt(this.lnbType);
        parcel.writeInt(this.lofHi);
        parcel.writeInt(this.lofLo);
        parcel.writeInt(this.lofThreshold);
        parcel.writeInt(this.signal22Khz);
        parcel.writeInt(this.lnbVoltage);
        parcel.writeInt(this.motorNumber);
        parcel.writeInt(this.posNumber);
        parcel.writeInt(this.loDirection);
        parcel.writeInt(this.laDirection);
        parcel.writeFloat(this.longitude);
        parcel.writeFloat(this.latitude);
        parcel.writeInt(this.satLongitude);
        parcel.writeInt(this.disEqcMode);
        parcel.writeInt(this.toneBurst);
        parcel.writeInt(this.disEqc1_0);
        parcel.writeInt(this.disEqc1_1);
        parcel.writeInt(this.repeatCount);
        parcel.writeInt(this.sequenceRepeat);
        parcel.writeInt(this.fastDisEqc);
        parcel.writeInt(this.cmdOrder);
        parcel.writeByte(this.selected ? (byte) 1 : 0);
        parcel.writeInt(this.customLowLnb);
        parcel.writeInt(this.customLowLnbMin);
        parcel.writeInt(this.customLowLnbMax);
        parcel.writeInt(this.customHighLnb);
        parcel.writeInt(this.customHighLnbMin);
        parcel.writeInt(this.customHighThreshold);
        parcel.writeInt(this.uniCableFormat);
        parcel.writeString(this.uniCableDataString);
        parcel.writeInt(this.uniCableSwitch);
        parcel.writeInt(this.userBand);
        parcel.writeLong(this.unFrequency);
        parcel.writeInt(this.uniCablePosition);
    }

    public void readFromParcel(Parcel parcel) {
        this.name = parcel.readString();
        if (parcel.readByte() == 0) {
            this.id = null;
        } else {
            this.id = Long.valueOf(parcel.readLong());
        }
        this.lnbNumber = parcel.readInt();
        this.lnbType = parcel.readInt();
        this.lofHi = parcel.readInt();
        this.lofLo = parcel.readInt();
        this.lofThreshold = parcel.readInt();
        this.signal22Khz = parcel.readInt();
        this.lnbVoltage = parcel.readInt();
        this.motorNumber = parcel.readInt();
        this.posNumber = parcel.readInt();
        this.loDirection = parcel.readInt();
        this.laDirection = parcel.readInt();
        this.longitude = parcel.readFloat();
        this.latitude = parcel.readFloat();
        this.satLongitude = parcel.readInt();
        this.disEqcMode = parcel.readInt();
        this.toneBurst = parcel.readInt();
        this.disEqc1_0 = parcel.readInt();
        this.disEqc1_1 = parcel.readInt();
        this.repeatCount = parcel.readInt();
        this.sequenceRepeat = parcel.readInt();
        this.fastDisEqc = parcel.readInt();
        this.cmdOrder = parcel.readInt();
        this.selected = parcel.readByte() != 0;
        this.customLowLnb = parcel.readInt();
        this.customLowLnbMin = parcel.readInt();
        this.customLowLnbMax = parcel.readInt();
        this.customHighLnb = parcel.readInt();
        this.customHighLnbMin = parcel.readInt();
        this.customHighThreshold = parcel.readInt();
        this.uniCableFormat = parcel.readInt();
        this.uniCableDataString = parcel.readString();
        this.uniCableSwitch = parcel.readInt();
        this.userBand = parcel.readInt();
        this.unFrequency = parcel.readLong();
        this.uniCablePosition = parcel.readInt();
    }

    public static final class Builder {
        private final SatelliteBean mSatelliteBean = new SatelliteBean();

        public Builder setName(String str) {
            String unused = this.mSatelliteBean.name = str;
            return this;
        }

        public Builder setId(long j) {
            Long unused = this.mSatelliteBean.id = Long.valueOf(j);
            return this;
        }

        public Builder setLnbNumber(int i) {
            int unused = this.mSatelliteBean.lnbNumber = i;
            return this;
        }

        public Builder setLnbType(int i) {
            int unused = this.mSatelliteBean.lnbType = i;
            return this;
        }

        public Builder setLofHi(int i) {
            int unused = this.mSatelliteBean.lofHi = i;
            return this;
        }

        public Builder setLofLo(int i) {
            int unused = this.mSatelliteBean.lofLo = i;
            return this;
        }

        public Builder setLofThreshold(int i) {
            this.mSatelliteBean.setLofThreshold(i);
            return this;
        }

        public Builder setSignal22Khz(int i) {
            this.mSatelliteBean.setSignal22Khz(i);
            return this;
        }

        public Builder setLnbVoltage(int i) {
            this.mSatelliteBean.setLnbVoltage(i);
            return this;
        }

        public Builder setMotorNumber(int i) {
            this.mSatelliteBean.setMotorNumber(i);
            return this;
        }

        public Builder setDishPosition(int i) {
            this.mSatelliteBean.setDishPosition(i);
            return this;
        }

        public Builder setLoDirection(int i) {
            this.mSatelliteBean.setLoDirection(i);
            return this;
        }

        public Builder setLaDirection(int i) {
            this.mSatelliteBean.setLaDirection(i);
            return this;
        }

        public Builder setLongitude(float f) {
            this.mSatelliteBean.setLongitude(f);
            return this;
        }

        public Builder setLatitude(float f) {
            this.mSatelliteBean.setLatitude(f);
            return this;
        }

        public Builder setSatLongitude(int i) {
            this.mSatelliteBean.setSatLongitude(i);
            return this;
        }

        public Builder setDisEqcMode(int i) {
            this.mSatelliteBean.setDisEqcMode(i);
            return this;
        }

        public Builder setToneBurst(int i) {
            this.mSatelliteBean.setToneBurst(i);
            return this;
        }

        public Builder setDisEqc1_0(int i) {
            this.mSatelliteBean.setDisEqc1_0(i);
            return this;
        }

        public Builder setDisEqc1_1(int i) {
            this.mSatelliteBean.setDisEqc1_1(i);
            return this;
        }

        public Builder setRepeatCount(int i) {
            this.mSatelliteBean.setRepeatCount(i);
            return this;
        }

        public Builder setSequenceRepeat(int i) {
            this.mSatelliteBean.setSequenceRepeat(i);
            return this;
        }

        public Builder setFastDisEqc(int i) {
            this.mSatelliteBean.setFastDisEqc(i);
            return this;
        }

        public Builder setCmdOrder(int i) {
            this.mSatelliteBean.setCmdOrder(i);
            return this;
        }

        public Builder setSelected(boolean z) {
            this.mSatelliteBean.setSelected(z);
            return this;
        }

        public Builder setCustomLowLnb(int i) {
            this.mSatelliteBean.setCustomLowLnb(i);
            return this;
        }

        public Builder setCustomLowLnbMin(int i) {
            this.mSatelliteBean.setCustomLowLnbMin(i);
            return this;
        }

        public Builder setCustomLowLnbMax(int i) {
            this.mSatelliteBean.setCustomLowLnbMax(i);
            return this;
        }

        public Builder setCustomHighLnb(int i) {
            this.mSatelliteBean.setCustomHighLnb(i);
            return this;
        }

        public Builder setCustomHighLnbMin(int i) {
            this.mSatelliteBean.setCustomHighLnbMin(i);
            return this;
        }

        public Builder setCustomHighLnbMax(int i) {
            this.mSatelliteBean.setCustomHighThreshold(i);
            return this;
        }

        public Builder setUniCableType(int i) {
            this.mSatelliteBean.setUniCableFormat(i);
            return this;
        }

        public Builder setUniCableDataString(String str) {
            this.mSatelliteBean.setUniCableDataString(str);
            return this;
        }

        public Builder setUniCableSwitch(int i) {
            this.mSatelliteBean.setUniCableSwitch(i);
            return this;
        }

        public Builder setUserBand(int i) {
            this.mSatelliteBean.setUserBand(i);
            return this;
        }

        public Builder setUbFrequency(long j) {
            this.mSatelliteBean.setUbFrequency(j);
            return this;
        }

        public Builder setUnicablePosition(int i) {
            this.mSatelliteBean.setUniCablePosition(i);
            return this;
        }

        public SatelliteBean build() {
            return this.mSatelliteBean;
        }
    }

    static ContentValues toDefaultContentValues(SatelliteBean satelliteBean) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("db_id", satelliteBean.getId());
        contentValues.put("sat_name", satelliteBean.getName());
        contentValues.put("lnb_num", Integer.valueOf(satelliteBean.getLnbNumber()));
        contentValues.put("lof_hi", Integer.valueOf(satelliteBean.getLofHi()));
        contentValues.put("lof_lo", Integer.valueOf(satelliteBean.getLofLo()));
        contentValues.put("lof_threshold", Integer.valueOf(satelliteBean.getLofThreshold()));
        contentValues.put("signal_22khz", Integer.valueOf(satelliteBean.getSignal22Khz()));
        contentValues.put("voltage", Integer.valueOf(satelliteBean.getLnbVoltage()));
        contentValues.put("motor_num", Integer.valueOf(satelliteBean.getMotorNumber()));
        contentValues.put("pos_num", Integer.valueOf(satelliteBean.getDishPosition()));
        contentValues.put("lo_direction", Integer.valueOf(satelliteBean.getLoDirection()));
        contentValues.put("la_direction", Integer.valueOf(satelliteBean.getLaDirection()));
        contentValues.put("longitude", Float.valueOf(satelliteBean.getLongitude()));
        contentValues.put("latitude", Float.valueOf(satelliteBean.getLatitude()));
        contentValues.put("sat_longitude", Integer.valueOf(satelliteBean.getSatLongitude()));
        contentValues.put("diseqc_mode", Integer.valueOf(satelliteBean.getDisEqcMode()));
        contentValues.put("tone_burst", Integer.valueOf(satelliteBean.getToneBurst()));
        contentValues.put("committed_cmd", Integer.valueOf(satelliteBean.getDisEqc1_0()));
        contentValues.put("uncommitted_cmd", Integer.valueOf(satelliteBean.getDisEqc1_1()));
        contentValues.put("repeat_count", Integer.valueOf(satelliteBean.getRepeatCount()));
        contentValues.put("sequence_repeat", Integer.valueOf(satelliteBean.getSequenceRepeat()));
        contentValues.put("fast_diseqc", Integer.valueOf(satelliteBean.getFastDisEqc()));
        contentValues.put("cmd_order", Integer.valueOf(satelliteBean.getCmdOrder()));
        contentValues.put("selected", Integer.valueOf(satelliteBean.getSelected() ? 1 : 0));
        return contentValues;
    }

    public static ContentValues toContentValues(SatelliteBean satelliteBean) {
        ContentValues defaultContentValues = toDefaultContentValues(satelliteBean);
        defaultContentValues.put("unicable_switch", Integer.valueOf(satelliteBean.getUniCableSwitch()));
        defaultContentValues.put("int_one", Integer.valueOf(satelliteBean.getUniCableFormat()));
        defaultContentValues.put("string_one", satelliteBean.getUniCableDataString());
        defaultContentValues.put("user_band", Integer.valueOf(satelliteBean.getUserBand()));
        defaultContentValues.put("ub_frequency", Long.valueOf(satelliteBean.getUbFrequency()));
        defaultContentValues.put("unicable_position", Integer.valueOf(satelliteBean.getUniCablePosition()));
        defaultContentValues.put("lnb_type", Integer.valueOf(satelliteBean.getLnbType()));
        defaultContentValues.put("custom_low_lnb", Integer.valueOf(satelliteBean.getCustomLowLnb()));
        defaultContentValues.put("custom_low_lnb_min", Integer.valueOf(satelliteBean.getCustomLowLnbMin()));
        defaultContentValues.put("custom_low_lnb_max", Integer.valueOf(satelliteBean.getCustomLowLnbMax()));
        defaultContentValues.put("custom_high_lnb", Integer.valueOf(satelliteBean.getCustomHighLnb()));
        defaultContentValues.put("custom_high_lnb_min", Integer.valueOf(satelliteBean.getCustomHighLnbMin()));
        defaultContentValues.put("custom_high_lnb_max", Integer.valueOf(satelliteBean.getCustomHighThreshold()));
        return defaultContentValues;
    }

    public String toString() {
        return "SatelliteBean{name='" + this.name + '\'' + ", id=" + this.id + ", lnbNumber=" + this.lnbNumber + ", lnbType=" + this.lnbType + ", lofHi=" + this.lofHi + ", lofLo=" + this.lofLo + ", lofThreshold=" + this.lofThreshold + ", signal22Khz=" + this.signal22Khz + ", lnbVoltage=" + this.lnbVoltage + ", motorNumber=" + this.motorNumber + ", posNumber=" + this.posNumber + ", loDirection=" + this.loDirection + ", laDirection=" + this.laDirection + ", longitude=" + this.longitude + ", latitude=" + this.latitude + ", satLongitude=" + this.satLongitude + ", disEqcMode=" + this.disEqcMode + ", toneBurst=" + this.toneBurst + ", disEqc1_0=" + this.disEqc1_0 + ", disEqc1_1=" + this.disEqc1_1 + ", repeatCount=" + this.repeatCount + ", sequenceRepeat=" + this.sequenceRepeat + ", fastDisEqc=" + this.fastDisEqc + ", cmdOrder=" + this.cmdOrder + ", selected=" + this.selected + ", customLowLnb=" + this.customLowLnb + ", customLowLnbMin=" + this.customLowLnbMin + ", customLowLnbMax=" + this.customLowLnbMax + ", customHighLnb=" + this.customHighLnb + ", customHighLnbMin=" + this.customHighLnbMin + ", customHighThreshold=" + this.customHighThreshold + ", uniCableDataString=" + this.uniCableDataString + ", uniCableFormat=" + this.uniCableFormat + ", uniCableSwitch=" + this.uniCableSwitch + ", userBand=" + this.userBand + ", unFrequency=" + this.unFrequency + ", uniCablePosition=" + this.uniCablePosition + '}';
    }

    private static String[] getProjection() {
        return new String[]{"db_id", "sat_name", "lnb_num", "lof_hi", "lof_lo", "lof_threshold", "signal_22khz", "voltage", "motor_num", "pos_num", "lo_direction", "la_direction", "longitude", "latitude", "sat_longitude", "diseqc_mode", "tone_burst", "committed_cmd", "uncommitted_cmd", "repeat_count", "sequence_repeat", "fast_diseqc", "cmd_order", "selected"};
    }

    public static class UniCableBean {
        private int format;
        private int position;
        private int ubFrequency;
        private int userBand;

        public UniCableBean() {
        }

        public UniCableBean(int i) {
            this.format = i;
        }

        public int getFormat() {
            return this.format;
        }

        public void setFormat(int i) {
            this.format = i;
        }

        public int getUserBand() {
            return this.userBand;
        }

        public void setUserBand(int i) {
            this.userBand = i;
        }

        public int getUbFrequency() {
            return this.ubFrequency;
        }

        public void setUnFrequency(int i) {
            this.ubFrequency = i;
        }

        public int getPosition() {
            return this.position;
        }

        public void setPosition(int i) {
            this.position = i;
        }
    }
}
