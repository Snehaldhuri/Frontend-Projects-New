package com.diipl.moviebeam.dtv;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

public class TransponderBean implements Parcelable {
    public static final Creator<TransponderBean> CREATOR = new Creator<TransponderBean>() {
        public TransponderBean createFromParcel(Parcel parcel) {
            return new TransponderBean(parcel);
        }

        public TransponderBean[] newArray(int i) {
            return new TransponderBean[i];
        }
    };
    public static final String[] DEFAULT_PROJECTION = getProjection();
    private int ber;
    private int bw;
    private int dvbsFlag;
    private int fec;
    private long freq;
    private long id;
    private boolean isSelected;
    private int mod;
    private int netId;
    private int networkFavListId;
    private int networkId;
    private int networkOpeatorSearch;
    private int networkOpeatorSearchDate;
    private int networkOpeatorSearchTime;
    private int networkOrigNetId;
    private int networkProfileCamId;
    private int networkProfileName;
    private int networkProfileType;
    private String networkRecNAme;
    private int networkVersion;
    private int polar;
    private int satParaId;
    private int sdtVersion;
    private int snr;
    private int src;
    private int strength;
    private long symbol;
    private int tranPlpId;
    private int tranSignalLevel;
    private int tranTerrType;
    private int transprotId;
    private int tsId;

    public int describeContents() {
        return 0;
    }

    public TransponderBean() {
    }

    protected TransponderBean(Parcel parcel) {
        this.id = parcel.readLong();
        this.src = parcel.readInt();
        this.netId = parcel.readInt();
        this.tsId = parcel.readInt();
        this.freq = parcel.readLong();
        this.symbol = parcel.readLong();
        this.mod = parcel.readInt();
        this.bw = parcel.readInt();
        this.snr = parcel.readInt();
        this.ber = parcel.readInt();
        this.strength = parcel.readInt();
        this.satParaId = parcel.readInt();
        this.polar = parcel.readInt();
        this.fec = parcel.readInt();
        this.dvbsFlag = parcel.readInt();
        this.transprotId = parcel.readInt();
        this.tranSignalLevel = parcel.readInt();
        this.tranTerrType = parcel.readInt();
        this.tranPlpId = parcel.readInt();
        this.sdtVersion = parcel.readInt();
        this.networkId = parcel.readInt();
        this.networkVersion = parcel.readInt();
        this.networkOrigNetId = parcel.readInt();
        this.networkProfileType = parcel.readInt();
        this.networkProfileCamId = parcel.readInt();
        this.networkOpeatorSearch = parcel.readInt();
        this.networkOpeatorSearchDate = parcel.readInt();
        this.networkOpeatorSearchTime = parcel.readInt();
        this.networkFavListId = parcel.readInt();
        this.networkRecNAme = parcel.readString();
        this.networkProfileName = parcel.readInt();
        this.isSelected = parcel.readByte() != 0;
    }

    public boolean isSelected() {
        return this.isSelected;
    }

    public void setSelected(boolean z) {
        this.isSelected = z;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long j) {
        this.id = j;
    }

    public int getSrc() {
        return this.src;
    }

    public void setSrc(int i) {
        this.src = i;
    }

    public int getNetId() {
        return this.netId;
    }

    public void setNetId(int i) {
        this.netId = i;
    }

    public int getTsId() {
        return this.tsId;
    }

    public void setTsId(int i) {
        this.tsId = i;
    }

    public long getFreq() {
        return this.freq;
    }

    public void setFreq(long j) {
        this.freq = j;
    }

    public long getSymbol() {
        return this.symbol;
    }

    public void setSymbol(long j) {
        this.symbol = j;
    }

    public int getMod() {
        return this.mod;
    }

    public void setMod(int i) {
        this.mod = i;
    }

    public int getBw() {
        return this.bw;
    }

    public void setBw(int i) {
        this.bw = i;
    }

    public int getSnr() {
        return this.snr;
    }

    public void setSnr(int i) {
        this.snr = i;
    }

    public int getBer() {
        return this.ber;
    }

    public void setBer(int i) {
        this.ber = i;
    }

    public int getStrength() {
        return this.strength;
    }

    public void setStrength(int i) {
        this.strength = i;
    }

    public int getSatParaId() {
        return this.satParaId;
    }

    public void setSatParaId(int i) {
        this.satParaId = i;
    }

    public int getPolar() {
        return this.polar;
    }

    public void setPolar(int i) {
        this.polar = i;
    }

    public int getFec() {
        return this.fec;
    }

    public void setFec(int i) {
        this.fec = i;
    }

    public int getDvbsFlag() {
        return this.dvbsFlag;
    }

    public void setDvbsFlag(int i) {
        this.dvbsFlag = i;
    }

    public int getTransprotId() {
        return this.transprotId;
    }

    public void setTransprotId(int i) {
        this.transprotId = i;
    }

    public int getTranSignalLevel() {
        return this.tranSignalLevel;
    }

    public void setTranSignalLevel(int i) {
        this.tranSignalLevel = i;
    }

    public int getTranTerrType() {
        return this.tranTerrType;
    }

    public void setTranTerrType(int i) {
        this.tranTerrType = i;
    }

    public int getTranPlpId() {
        return this.tranPlpId;
    }

    public void setTranPlpId(int i) {
        this.tranPlpId = i;
    }

    public int getSdtVersion() {
        return this.sdtVersion;
    }

    public void setSdtVersion(int i) {
        this.sdtVersion = i;
    }

    public int getNetworkId() {
        return this.networkId;
    }

    public void setNetworkId(int i) {
        this.networkId = i;
    }

    public int getNetworkVersion() {
        return this.networkVersion;
    }

    public void setNetworkVersion(int i) {
        this.networkVersion = i;
    }

    public int getNetworkOrigNetId() {
        return this.networkOrigNetId;
    }

    public void setNetworkOrigNetId(int i) {
        this.networkOrigNetId = i;
    }

    public int getNetworkProfileType() {
        return this.networkProfileType;
    }

    public void setNetworkProfileType(int i) {
        this.networkProfileType = i;
    }

    public int getNetworkProfileCamId() {
        return this.networkProfileCamId;
    }

    public void setNetworkProfileCamId(int i) {
        this.networkProfileCamId = i;
    }

    public int getNetworkOpeatorSearch() {
        return this.networkOpeatorSearch;
    }

    public void setNetworkOpeatorSearch(int i) {
        this.networkOpeatorSearch = i;
    }

    public int getNetworkOpeatorSearchDate() {
        return this.networkOpeatorSearchDate;
    }

    public void setNetworkOpeatorSearchDate(int i) {
        this.networkOpeatorSearchDate = i;
    }

    public int getNetworkOpeatorSearchTime() {
        return this.networkOpeatorSearchTime;
    }

    public void setNetworkOpeatorSearchTime(int i) {
        this.networkOpeatorSearchTime = i;
    }

    public int getNetworkFavListId() {
        return this.networkFavListId;
    }

    public void setNetworkFavListId(int i) {
        this.networkFavListId = i;
    }

    public String getNetworkRecNAme() {
        return this.networkRecNAme;
    }

    public void setNetworkRecNAme(String str) {
        this.networkRecNAme = str;
    }

    public int getNetworkProfileName() {
        return this.networkProfileName;
    }

    public void setNetworkProfileName(int i) {
        this.networkProfileName = i;
    }

    public static TransponderBean fromCursor(Cursor cursor) {
        Builder builder = new Builder();
        builder.setId(cursor.getLong(0)).setSrc(cursor.getInt(1)).setNetId(cursor.getInt(2)).setTsId(cursor.getInt(3)).setFreq((long) cursor.getInt(4)).setSymbol(cursor.getInt(5)).setMod(cursor.getInt(6)).setBw(cursor.getInt(7)).setSnr(cursor.getInt(8)).setBer(cursor.getInt(9)).setStrength(cursor.getInt(10)).setSatParaId(cursor.getInt(11)).setPolar(cursor.getInt(12)).setFec(cursor.getInt(13)).setDvbsFlag(cursor.getInt(14)).setTransprotId(cursor.getInt(15)).setTranSignalLevel(cursor.getInt(16)).setTranTerrType(cursor.getInt(17)).setTranPlpId(cursor.getInt(18)).setSdtVersion(cursor.getInt(19)).setNetworkId(cursor.getInt(20)).setNetworkVersion(cursor.getInt(21)).setNetworkOrigNetId(cursor.getInt(22)).setNetworkProfileType(cursor.getInt(23)).setNetworkProfileCamId(cursor.getInt(24)).setNetworkOreatorSearch(cursor.getInt(25)).setNetworkOreatorSearchDate(cursor.getInt(26)).setNetworkOreatorSearchTime(cursor.getInt(27)).setNetworkFavListId(cursor.getInt(28)).setNetworkRecName(cursor.getString(29)).setNetworkProfileName(cursor.getInt(30));
        return builder.build();
    }

    public static TransponderBean fromDefaultCursor(Cursor cursor) {
        Builder builder = new Builder();
        builder.setId(cursor.getLong(0)).setSrc(cursor.getInt(1)).setNetId(cursor.getInt(2)).setTsId(cursor.getInt(3)).setFreq((long) cursor.getInt(4)).setSymbol(cursor.getInt(5)).setMod(cursor.getInt(6)).setBw(cursor.getInt(7)).setSnr(cursor.getInt(8)).setBer(cursor.getInt(9)).setStrength(cursor.getInt(10)).setSatParaId(cursor.getInt(11)).setPolar(cursor.getInt(12)).setNetworkId(cursor.getInt(13));
        return builder.build();
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(this.id);
        parcel.writeInt(this.src);
        parcel.writeInt(this.netId);
        parcel.writeInt(this.tsId);
        parcel.writeLong(this.freq);
        parcel.writeLong(this.symbol);
        parcel.writeInt(this.mod);
        parcel.writeInt(this.bw);
        parcel.writeInt(this.snr);
        parcel.writeInt(this.ber);
        parcel.writeInt(this.strength);
        parcel.writeInt(this.satParaId);
        parcel.writeInt(this.polar);
        parcel.writeInt(this.fec);
        parcel.writeInt(this.dvbsFlag);
        parcel.writeInt(this.transprotId);
        parcel.writeInt(this.tranSignalLevel);
        parcel.writeInt(this.tranTerrType);
        parcel.writeInt(this.tranPlpId);
        parcel.writeInt(this.sdtVersion);
        parcel.writeInt(this.networkId);
        parcel.writeInt(this.networkVersion);
        parcel.writeInt(this.networkOrigNetId);
        parcel.writeInt(this.networkProfileType);
        parcel.writeInt(this.networkProfileCamId);
        parcel.writeInt(this.networkOpeatorSearch);
        parcel.writeInt(this.networkOpeatorSearchDate);
        parcel.writeInt(this.networkOpeatorSearchTime);
        parcel.writeInt(this.networkFavListId);
        parcel.writeString(this.networkRecNAme);
        parcel.writeInt(this.networkProfileName);
        parcel.writeByte(this.isSelected ? (byte) 1 : 0);
    }

    public void readFromParcel(Parcel parcel) {
        this.id = parcel.readLong();
        this.src = parcel.readInt();
        this.netId = parcel.readInt();
        this.tsId = parcel.readInt();
        this.freq = parcel.readLong();
        this.symbol = parcel.readLong();
        this.mod = parcel.readInt();
        this.bw = parcel.readInt();
        this.snr = parcel.readInt();
        this.ber = parcel.readInt();
        this.strength = parcel.readInt();
        this.satParaId = parcel.readInt();
        this.polar = parcel.readInt();
        this.fec = parcel.readInt();
        this.dvbsFlag = parcel.readInt();
        this.transprotId = parcel.readInt();
        this.tranSignalLevel = parcel.readInt();
        this.tranTerrType = parcel.readInt();
        this.tranPlpId = parcel.readInt();
        this.sdtVersion = parcel.readInt();
        this.networkId = parcel.readInt();
        this.networkVersion = parcel.readInt();
        this.networkOrigNetId = parcel.readInt();
        this.networkProfileType = parcel.readInt();
        this.networkProfileCamId = parcel.readInt();
        this.networkOpeatorSearch = parcel.readInt();
        this.networkOpeatorSearchDate = parcel.readInt();
        this.networkOpeatorSearchTime = parcel.readInt();
        this.networkFavListId = parcel.readInt();
        this.networkRecNAme = parcel.readString();
        this.networkProfileName = parcel.readInt();
        this.isSelected = parcel.readByte() != 0;
    }

    public static final class Builder {
        final TransponderBean mTransponderBean = new TransponderBean();

        public Builder setId(long j) {
            this.mTransponderBean.setId(j);
            return this;
        }

        public Builder setSrc(int i) {
            this.mTransponderBean.setSrc(i);
            return this;
        }

        public Builder setNetId(int i) {
            this.mTransponderBean.setNetId(i);
            return this;
        }

        public Builder setTsId(int i) {
            this.mTransponderBean.setTsId(i);
            return this;
        }

        public Builder setFreq(long j) {
            this.mTransponderBean.setFreq(j);
            return this;
        }

        public Builder setSymbol(int i) {
            this.mTransponderBean.setSymbol((long) i);
            return this;
        }

        public Builder setMod(int i) {
            this.mTransponderBean.setMod(i);
            return this;
        }

        public Builder setBw(int i) {
            this.mTransponderBean.setBw(i);
            return this;
        }

        public Builder setSnr(int i) {
            this.mTransponderBean.setSnr(i);
            return this;
        }

        public Builder setBer(int i) {
            this.mTransponderBean.setBer(i);
            return this;
        }

        public Builder setStrength(int i) {
            this.mTransponderBean.setStrength(i);
            return this;
        }

        public Builder setSatParaId(int i) {
            this.mTransponderBean.setSatParaId(i);
            return this;
        }

        public Builder setPolar(int i) {
            this.mTransponderBean.setPolar(i);
            return this;
        }

        public Builder setFec(int i) {
            this.mTransponderBean.setFec(i);
            return this;
        }

        public Builder setDvbsFlag(int i) {
            this.mTransponderBean.setDvbsFlag(i);
            return this;
        }

        public Builder setTransprotId(int i) {
            this.mTransponderBean.setTransprotId(i);
            return this;
        }

        public Builder setTranSignalLevel(int i) {
            this.mTransponderBean.setTranSignalLevel(i);
            return this;
        }

        public Builder setTranTerrType(int i) {
            this.mTransponderBean.setTranTerrType(i);
            return this;
        }

        public Builder setTranPlpId(int i) {
            this.mTransponderBean.setTranPlpId(i);
            return this;
        }

        public Builder setSdtVersion(int i) {
            this.mTransponderBean.setSdtVersion(i);
            return this;
        }

        public Builder setNetworkId(int i) {
            this.mTransponderBean.setNetworkId(i);
            return this;
        }

        public Builder setNetworkVersion(int i) {
            this.mTransponderBean.setNetworkVersion(i);
            return this;
        }

        public Builder setNetworkOrigNetId(int i) {
            this.mTransponderBean.setNetworkOrigNetId(i);
            return this;
        }

        public Builder setNetworkProfileType(int i) {
            this.mTransponderBean.setNetworkProfileType(i);
            return this;
        }

        public Builder setNetworkProfileCamId(int i) {
            this.mTransponderBean.setNetworkProfileCamId(i);
            return this;
        }

        public Builder setNetworkOreatorSearch(int i) {
            this.mTransponderBean.setNetworkOpeatorSearch(i);
            return this;
        }

        public Builder setNetworkOreatorSearchDate(int i) {
            this.mTransponderBean.setNetworkOpeatorSearchDate(i);
            return this;
        }

        public Builder setNetworkOreatorSearchTime(int i) {
            this.mTransponderBean.setNetworkOpeatorSearchTime(i);
            return this;
        }

        public Builder setNetworkFavListId(int i) {
            this.mTransponderBean.setNetworkFavListId(i);
            return this;
        }

        public Builder setNetworkRecName(String str) {
            this.mTransponderBean.setNetworkRecNAme(str);
            return this;
        }

        public Builder setNetworkProfileName(int i) {
            this.mTransponderBean.setNetworkProfileName(i);
            return this;
        }

        public TransponderBean build() {
            return this.mTransponderBean;
        }
    }

    public static ContentValues toContentValue(TransponderBean transponderBean) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("src", Integer.valueOf(transponderBean.getSrc()));
        contentValues.put("db_net_id", Integer.valueOf(transponderBean.getNetId()));
        contentValues.put("ts_id", Integer.valueOf(transponderBean.getTsId()));
        contentValues.put("freq", Long.valueOf(transponderBean.getFreq()));
        contentValues.put("symb", Long.valueOf(transponderBean.getSymbol()));
        contentValues.put("mod", Integer.valueOf(transponderBean.getMod()));
        contentValues.put("bw", Integer.valueOf(transponderBean.getBw()));
        contentValues.put("snr", Integer.valueOf(transponderBean.getSnr()));
        contentValues.put("ber", Integer.valueOf(transponderBean.getBer()));
        contentValues.put("strength", Integer.valueOf(transponderBean.getStrength()));
        contentValues.put("db_sat_para_id", Integer.valueOf(transponderBean.getSatParaId()));
        contentValues.put("polar", Integer.valueOf(transponderBean.getPolar()));
        contentValues.put("fec", Integer.valueOf(transponderBean.getFec()));
        contentValues.put("dvbs_flag", Integer.valueOf(transponderBean.getDvbsFlag()));
        contentValues.put("transport_id", Integer.valueOf(transponderBean.getTransprotId()));
        contentValues.put("tran_signal_level", Integer.valueOf(transponderBean.getTranSignalLevel()));
        contentValues.put("tran_terr_type", Integer.valueOf(transponderBean.getTranTerrType()));
        contentValues.put("tran_plp_id", Integer.valueOf(transponderBean.getTranPlpId()));
        contentValues.put("sdt_version", Integer.valueOf(transponderBean.getSdtVersion()));
        contentValues.put("network_id", Integer.valueOf(transponderBean.getNetworkId()));
        contentValues.put("network_version", Integer.valueOf(transponderBean.getNetworkVersion()));
        contentValues.put("network_orig_net_id", Integer.valueOf(transponderBean.getNetworkOrigNetId()));
        contentValues.put("network_profile_type", Integer.valueOf(transponderBean.getNetworkProfileType()));
        contentValues.put("network_profile_cam_id", Integer.valueOf(transponderBean.getNetworkProfileCamId()));
        contentValues.put("network_opeaator_search", Integer.valueOf(transponderBean.getNetworkOpeatorSearch()));
        contentValues.put("network_op_search_date", Integer.valueOf(transponderBean.getNetworkOpeatorSearchDate()));
        contentValues.put("network_op_search_time", Integer.valueOf(transponderBean.getNetworkOpeatorSearchTime()));
        contentValues.put("network_favlist_id", Integer.valueOf(transponderBean.getNetworkFavListId()));
        contentValues.put("network_rec_name", transponderBean.getNetworkRecNAme());
        contentValues.put("network_profile_name", Integer.valueOf(transponderBean.getNetworkProfileName()));
        return contentValues;
    }

    private static String[] getProjection() {
        return new String[]{"db_id", "src", "db_net_id", "ts_id", "freq", "symb", "mod", "bw", "snr", "ber", "strength", "db_sat_para_id", "polar", "network_id"};
    }
}
