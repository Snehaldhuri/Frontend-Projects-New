package com.diipl.moviebeam.dtv.interfaces;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

import com.diipl.moviebeam.dtv.FrequencyBean;
import com.diipl.moviebeam.dtv.SatelliteBean;
import com.diipl.moviebeam.dtv.TransponderBean;
import com.diipl.moviebeam.dtv.dto.CityInfoBean;

import java.util.ArrayList;
import java.util.List;

public interface IDvbConfigInterface extends IInterface {

     class Default implements IDvbConfigInterface {
        public IBinder asBinder() {
            return null;
        }

        public void closeDiseqc12Setting() {
        }

        public void dishMove(int i, int i2) {
        }

        public boolean doDvbSScanAction(List<SatelliteBean> list, TransponderBean transponderBean, int i, int i2, int i3) {
            return false;
        }

        public void editSatelliteAndTransponder(int i, int i2, SatelliteBean satelliteBean, TransponderBean transponderBean) {
        }

        public void enableDishLimits(boolean z) {
        }

        public List<String> getCountryDisplayList(String str) {
            return null;
        }

        public int getCurrentCountryIndex(String str) {
            return 0;
        }

        public List<String> getCurrentLangNameList() {
            return null;
        }

        public int getCurrentMainAudioLangId() {
            return 0;
        }

        public int getCurrentMainSubtitleLangId() {
            return 0;
        }

        public List<SatelliteBean> getDefaultSatelliteList() {
            return null;
        }

        public List<TransponderBean> getDefaultTransponderList(long j) {
            return null;
        }

        public String getDvbType() {
            return null;
        }

        public List<CityInfoBean> getEWBSCityInfo() {
            return null;
        }

        public List<FrequencyBean> getFrequencyList() {
            return null;
        }

        public int getIntParameter(String str, boolean z) {
            return 0;
        }

        public boolean getLcnStatusByCountry(String str, int i) {
            return false;
        }

        public String getSignalInfo() {
            return null;
        }

        public String[] getStringArray(String str) {
            return null;
        }

        public String getStringParameters(String str) {
            return null;
        }

        public String getSupportTunerType() {
            return null;
        }

        public String[] getTunerArray() {
            return null;
        }

        public boolean isDoubleTuner() {
            return false;
        }

        public boolean isMultiTuner() {
            return false;
        }

        public boolean isOperators() {
            return false;
        }

        public void moveDishToPosition(int i) {
        }

        public void registerCallback(ScanEventHandler scanEventHandler) {
        }

        public boolean saveIntParameter(String str, int i) {
            return false;
        }

        public void saveStringParameters(String str, String str2) {
        }

        public void setCountryCodeByIndex(int i, String str) {
        }

        public void setDefaultData() {
        }

        public void setDishELimits() {
        }

        public void setDishPosition(int i) {
        }

        public void setDishWLimits() {
        }

        public void setEwbsZone(int i) {
        }

        public void setMainAudioLanguageById(int i) {
        }

        public void setMainSubtitleLanguageById(int i) {
        }

        public void setVoltage(int i) {
        }

        public void startDvbTuneAction(String str) {
        }

        public void startLockTp(SatelliteBean satelliteBean, TransponderBean transponderBean) {
        }

        public void startSearchChannel(String str) {
        }

        public void startTuneAction(String str, int i, int i2, int i3, int i4) {
        }

        public void stopSearchChannel(boolean z) {
        }

        public void stopTuneAction() {
        }

        public void storeDishPosition(int i) {
        }

        public void unregisterCallback(ScanEventHandler scanEventHandler) {
        }

        public void updateDefaultDatabaseSelected(SatelliteBean satelliteBean) {
        }
    }

    void closeDiseqc12Setting();

    void dishMove(int i, int i2);

    boolean doDvbSScanAction(List<SatelliteBean> list, TransponderBean transponderBean, int i, int i2, int i3);

    void editSatelliteAndTransponder(int i, int i2, SatelliteBean satelliteBean, TransponderBean transponderBean);

    void enableDishLimits(boolean z);

    List<String> getCountryDisplayList(String str);

    int getCurrentCountryIndex(String str);

    List<String> getCurrentLangNameList();

    int getCurrentMainAudioLangId();

    int getCurrentMainSubtitleLangId();

    List<SatelliteBean> getDefaultSatelliteList();

    List<TransponderBean> getDefaultTransponderList(long j);

    String getDvbType();

    List<CityInfoBean> getEWBSCityInfo();

    List<FrequencyBean> getFrequencyList();

    int getIntParameter(String str, boolean z);

    boolean getLcnStatusByCountry(String str, int i);

    String getSignalInfo();

    String[] getStringArray(String str);

    String getStringParameters(String str);

    String getSupportTunerType();

    String[] getTunerArray();

    boolean isDoubleTuner();

    boolean isMultiTuner();

    boolean isOperators();

    void moveDishToPosition(int i);

    void registerCallback(ScanEventHandler scanEventHandler);

    boolean saveIntParameter(String str, int i);

    void saveStringParameters(String str, String str2);

    void setCountryCodeByIndex(int i, String str);

    void setDefaultData();

    void setDishELimits();

    void setDishPosition(int i);

    void setDishWLimits();

    void setEwbsZone(int i);

    void setMainAudioLanguageById(int i);

    void setMainSubtitleLanguageById(int i);

    void setVoltage(int i);

    void startDvbTuneAction(String str);

    void startLockTp(SatelliteBean satelliteBean, TransponderBean transponderBean);

    void startSearchChannel(String str);

    void startTuneAction(String str, int i, int i2, int i3, int i4);

    void stopSearchChannel(boolean z);

    void stopTuneAction();

    void storeDishPosition(int i);

    void unregisterCallback(ScanEventHandler scanEventHandler);

    void updateDefaultDatabaseSelected(SatelliteBean satelliteBean);

    public static abstract class Stub extends Binder implements IDvbConfigInterface {
        private static final String DESCRIPTOR = "nes.scan.IDvbConfigInterface";
        static final int TRANSACTION_closeDiseqc12Setting = 39;
        static final int TRANSACTION_dishMove = 38;
        static final int TRANSACTION_doDvbSScanAction = 27;
        static final int TRANSACTION_editSatelliteAndTransponder = 24;
        static final int TRANSACTION_enableDishLimits = 32;
        static final int TRANSACTION_getCountryDisplayList = 6;
        static final int TRANSACTION_getCurrentCountryIndex = 8;
        static final int TRANSACTION_getCurrentLangNameList = 11;
        static final int TRANSACTION_getCurrentMainAudioLangId = 9;
        static final int TRANSACTION_getCurrentMainSubtitleLangId = 13;
        static final int TRANSACTION_getDefaultSatelliteList = 41;
        static final int TRANSACTION_getDefaultTransponderList = 42;
        static final int TRANSACTION_getDvbType = 2;
        static final int TRANSACTION_getEWBSCityInfo = 20;
        static final int TRANSACTION_getFrequencyList = 28;
        static final int TRANSACTION_getIntParameter = 14;
        static final int TRANSACTION_getLcnStatusByCountry = 21;
        static final int TRANSACTION_getSignalInfo = 26;
        static final int TRANSACTION_getStringArray = 18;
        static final int TRANSACTION_getStringParameters = 17;
        static final int TRANSACTION_getSupportTunerType = 5;
        static final int TRANSACTION_getTunerArray = 22;
        static final int TRANSACTION_isDoubleTuner = 3;
        static final int TRANSACTION_isMultiTuner = 4;
        static final int TRANSACTION_isOperators = 23;
        static final int TRANSACTION_moveDishToPosition = 36;
        static final int TRANSACTION_registerCallback = 45;
        static final int TRANSACTION_saveIntParameter = 15;
        static final int TRANSACTION_saveStringParameters = 16;
        static final int TRANSACTION_setCountryCodeByIndex = 7;
        static final int TRANSACTION_setDefaultData = 1;
        static final int TRANSACTION_setDishELimits = 33;
        static final int TRANSACTION_setDishPosition = 37;
        static final int TRANSACTION_setDishWLimits = 34;
        static final int TRANSACTION_setEwbsZone = 19;
        static final int TRANSACTION_setMainAudioLanguageById = 10;
        static final int TRANSACTION_setMainSubtitleLanguageById = 12;
        static final int TRANSACTION_setVoltage = 29;
        static final int TRANSACTION_startDvbTuneAction = 47;
        static final int TRANSACTION_startLockTp = 25;
        static final int TRANSACTION_startSearchChannel = 43;
        static final int TRANSACTION_startTuneAction = 30;
        static final int TRANSACTION_stopSearchChannel = 44;
        static final int TRANSACTION_stopTuneAction = 31;
        static final int TRANSACTION_storeDishPosition = 35;
        static final int TRANSACTION_unregisterCallback = 46;
        static final int TRANSACTION_updateDefaultDatabaseSelected = 40;

        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IDvbConfigInterface asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            if (queryLocalInterface == null || !(queryLocalInterface instanceof IDvbConfigInterface)) {
                return new Proxy(iBinder);
            }
            return (IDvbConfigInterface) queryLocalInterface;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v2, resolved type: nes.scan.TransponderBean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v6, resolved type: nes.scan.TransponderBean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v9, resolved type: nes.scan.TransponderBean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v12, resolved type: nes.scan.SatelliteBean} */
        /* JADX WARNING: type inference failed for: r0v1 */
        /* JADX WARNING: type inference failed for: r0v15 */
        /* JADX WARNING: type inference failed for: r0v16 */
        /* JADX WARNING: type inference failed for: r0v17 */
        /* JADX WARNING: type inference failed for: r0v18 */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTransact(int r11, Parcel r12, Parcel r13, int r14) {
            /*
                r10 = this;
                r0 = 1598968902(0x5f4e5446, float:1.4867585E19)
                r1 = 1
                java.lang.String r2 = "nes.scan.IDvbConfigInterface"
                if (r11 == r0) goto L_0x03a2
                r0 = 0
                r3 = 0
                switch(r11) {
                    case 1: goto L_0x0398;
                    case 2: goto L_0x038a;
                    case 3: goto L_0x037c;
                    case 4: goto L_0x036e;
                    case 5: goto L_0x0360;
                    case 6: goto L_0x034e;
                    case 7: goto L_0x033c;
                    case 8: goto L_0x032a;
                    case 9: goto L_0x031c;
                    case 10: goto L_0x030e;
                    case 11: goto L_0x0300;
                    case 12: goto L_0x02f2;
                    case 13: goto L_0x02e4;
                    case 14: goto L_0x02cb;
                    case 15: goto L_0x02b5;
                    case 16: goto L_0x02a3;
                    case 17: goto L_0x0291;
                    case 18: goto L_0x027f;
                    case 19: goto L_0x0271;
                    case 20: goto L_0x0263;
                    case 21: goto L_0x024d;
                    case 22: goto L_0x023f;
                    case 23: goto L_0x0231;
                    case 24: goto L_0x01e8;
                    case 25: goto L_0x01a7;
                    case 26: goto L_0x0199;
                    case 27: goto L_0x0158;
                    case 28: goto L_0x014a;
                    case 29: goto L_0x013c;
                    case 30: goto L_0x011d;
                    case 31: goto L_0x0113;
                    case 32: goto L_0x0102;
                    case 33: goto L_0x00f8;
                    case 34: goto L_0x00ee;
                    case 35: goto L_0x00e0;
                    case 36: goto L_0x00d2;
                    case 37: goto L_0x00c4;
                    case 38: goto L_0x00b2;
                    case 39: goto L_0x00a8;
                    case 40: goto L_0x0083;
                    case 41: goto L_0x0075;
                    case 42: goto L_0x0063;
                    case 43: goto L_0x0055;
                    case 44: goto L_0x0044;
                    case 45: goto L_0x0032;
                    case 46: goto L_0x0020;
                    case 47: goto L_0x0012;
                    default: goto L_0x000d;
                }
            L_0x000d:
                boolean r10 = super.onTransact(r11, r12, r13, r14)
                return r10
            L_0x0012:
                r12.enforceInterface(r2)
                java.lang.String r11 = r12.readString()
                r10.startDvbTuneAction(r11)
                r13.writeNoException()
                return r1
            L_0x0020:
                r12.enforceInterface(r2)
                android.os.IBinder r11 = r12.readStrongBinder()
                nes.scan.ScanEventHandler r11 = nes.scan.ScanEventHandler.Stub.asInterface(r11)
                r10.unregisterCallback(r11)
                r13.writeNoException()
                return r1
            L_0x0032:
                r12.enforceInterface(r2)
                android.os.IBinder r11 = r12.readStrongBinder()
                nes.scan.ScanEventHandler r11 = nes.scan.ScanEventHandler.Stub.asInterface(r11)
                r10.registerCallback(r11)
                r13.writeNoException()
                return r1
            L_0x0044:
                r12.enforceInterface(r2)
                int r11 = r12.readInt()
                if (r11 == 0) goto L_0x004e
                r3 = r1
            L_0x004e:
                r10.stopSearchChannel(r3)
                r13.writeNoException()
                return r1
            L_0x0055:
                r12.enforceInterface(r2)
                java.lang.String r11 = r12.readString()
                r10.startSearchChannel(r11)
                r13.writeNoException()
                return r1
            L_0x0063:
                r12.enforceInterface(r2)
                long r11 = r12.readLong()
                java.util.List r10 = r10.getDefaultTransponderList(r11)
                r13.writeNoException()
                r13.writeTypedList(r10)
                return r1
            L_0x0075:
                r12.enforceInterface(r2)
                java.util.List r10 = r10.getDefaultSatelliteList()
                r13.writeNoException()
                r13.writeTypedList(r10)
                return r1
            L_0x0083:
                r12.enforceInterface(r2)
                int r11 = r12.readInt()
                if (r11 == 0) goto L_0x0095
                android.os.Parcelable$Creator<nes.scan.SatelliteBean> r11 = nes.scan.SatelliteBean.CREATOR
                java.lang.Object r11 = r11.createFromParcel(r12)
                r0 = r11
                nes.scan.SatelliteBean r0 = (nes.scan.SatelliteBean) r0
            L_0x0095:
                r10.updateDefaultDatabaseSelected(r0)
                r13.writeNoException()
                if (r0 == 0) goto L_0x00a4
                r13.writeInt(r1)
                r0.writeToParcel(r13, r1)
                goto L_0x00a7
            L_0x00a4:
                r13.writeInt(r3)
            L_0x00a7:
                return r1
            L_0x00a8:
                r12.enforceInterface(r2)
                r10.closeDiseqc12Setting()
                r13.writeNoException()
                return r1
            L_0x00b2:
                r12.enforceInterface(r2)
                int r11 = r12.readInt()
                int r12 = r12.readInt()
                r10.dishMove(r11, r12)
                r13.writeNoException()
                return r1
            L_0x00c4:
                r12.enforceInterface(r2)
                int r11 = r12.readInt()
                r10.setDishPosition(r11)
                r13.writeNoException()
                return r1
            L_0x00d2:
                r12.enforceInterface(r2)
                int r11 = r12.readInt()
                r10.moveDishToPosition(r11)
                r13.writeNoException()
                return r1
            L_0x00e0:
                r12.enforceInterface(r2)
                int r11 = r12.readInt()
                r10.storeDishPosition(r11)
                r13.writeNoException()
                return r1
            L_0x00ee:
                r12.enforceInterface(r2)
                r10.setDishWLimits()
                r13.writeNoException()
                return r1
            L_0x00f8:
                r12.enforceInterface(r2)
                r10.setDishELimits()
                r13.writeNoException()
                return r1
            L_0x0102:
                r12.enforceInterface(r2)
                int r11 = r12.readInt()
                if (r11 == 0) goto L_0x010c
                r3 = r1
            L_0x010c:
                r10.enableDishLimits(r3)
                r13.writeNoException()
                return r1
            L_0x0113:
                r12.enforceInterface(r2)
                r10.stopTuneAction()
                r13.writeNoException()
                return r1
            L_0x011d:
                r12.enforceInterface(r2)
                java.lang.String r5 = r12.readString()
                int r6 = r12.readInt()
                int r7 = r12.readInt()
                int r8 = r12.readInt()
                int r9 = r12.readInt()
                r4 = r10
                r4.startTuneAction(r5, r6, r7, r8, r9)
                r13.writeNoException()
                return r1
            L_0x013c:
                r12.enforceInterface(r2)
                int r11 = r12.readInt()
                r10.setVoltage(r11)
                r13.writeNoException()
                return r1
            L_0x014a:
                r12.enforceInterface(r2)
                java.util.List r10 = r10.getFrequencyList()
                r13.writeNoException()
                r13.writeTypedList(r10)
                return r1
            L_0x0158:
                r12.enforceInterface(r2)
                android.os.Parcelable$Creator<nes.scan.SatelliteBean> r11 = nes.scan.SatelliteBean.CREATOR
                java.util.ArrayList r11 = r12.createTypedArrayList(r11)
                int r14 = r12.readInt()
                if (r14 == 0) goto L_0x0170
                android.os.Parcelable$Creator<nes.scan.TransponderBean> r14 = nes.scan.TransponderBean.CREATOR
                java.lang.Object r14 = r14.createFromParcel(r12)
                r0 = r14
                nes.scan.TransponderBean r0 = (nes.scan.TransponderBean) r0
            L_0x0170:
                int r7 = r12.readInt()
                int r8 = r12.readInt()
                int r9 = r12.readInt()
                r4 = r10
                r5 = r11
                r6 = r0
                boolean r10 = r4.doDvbSScanAction(r5, r6, r7, r8, r9)
                r13.writeNoException()
                r13.writeInt(r10)
                r13.writeTypedList(r11)
                if (r0 == 0) goto L_0x0195
                r13.writeInt(r1)
                r0.writeToParcel(r13, r1)
                goto L_0x0198
            L_0x0195:
                r13.writeInt(r3)
            L_0x0198:
                return r1
            L_0x0199:
                r12.enforceInterface(r2)
                java.lang.String r10 = r10.getSignalInfo()
                r13.writeNoException()
                r13.writeString(r10)
                return r1
            L_0x01a7:
                r12.enforceInterface(r2)
                int r11 = r12.readInt()
                if (r11 == 0) goto L_0x01b9
                android.os.Parcelable$Creator<nes.scan.SatelliteBean> r11 = nes.scan.SatelliteBean.CREATOR
                java.lang.Object r11 = r11.createFromParcel(r12)
                nes.scan.SatelliteBean r11 = (nes.scan.SatelliteBean) r11
                goto L_0x01ba
            L_0x01b9:
                r11 = r0
            L_0x01ba:
                int r14 = r12.readInt()
                if (r14 == 0) goto L_0x01c9
                android.os.Parcelable$Creator<nes.scan.TransponderBean> r14 = nes.scan.TransponderBean.CREATOR
                java.lang.Object r12 = r14.createFromParcel(r12)
                r0 = r12
                nes.scan.TransponderBean r0 = (nes.scan.TransponderBean) r0
            L_0x01c9:
                r10.startLockTp(r11, r0)
                r13.writeNoException()
                if (r11 == 0) goto L_0x01d8
                r13.writeInt(r1)
                r11.writeToParcel(r13, r1)
                goto L_0x01db
            L_0x01d8:
                r13.writeInt(r3)
            L_0x01db:
                if (r0 == 0) goto L_0x01e4
                r13.writeInt(r1)
                r0.writeToParcel(r13, r1)
                goto L_0x01e7
            L_0x01e4:
                r13.writeInt(r3)
            L_0x01e7:
                return r1
            L_0x01e8:
                r12.enforceInterface(r2)
                int r11 = r12.readInt()
                int r14 = r12.readInt()
                int r2 = r12.readInt()
                if (r2 == 0) goto L_0x0202
                android.os.Parcelable$Creator<nes.scan.SatelliteBean> r2 = nes.scan.SatelliteBean.CREATOR
                java.lang.Object r2 = r2.createFromParcel(r12)
                nes.scan.SatelliteBean r2 = (nes.scan.SatelliteBean) r2
                goto L_0x0203
            L_0x0202:
                r2 = r0
            L_0x0203:
                int r4 = r12.readInt()
                if (r4 == 0) goto L_0x0212
                android.os.Parcelable$Creator<nes.scan.TransponderBean> r0 = nes.scan.TransponderBean.CREATOR
                java.lang.Object r12 = r0.createFromParcel(r12)
                r0 = r12
                nes.scan.TransponderBean r0 = (nes.scan.TransponderBean) r0
            L_0x0212:
                r10.editSatelliteAndTransponder(r11, r14, r2, r0)
                r13.writeNoException()
                if (r2 == 0) goto L_0x0221
                r13.writeInt(r1)
                r2.writeToParcel(r13, r1)
                goto L_0x0224
            L_0x0221:
                r13.writeInt(r3)
            L_0x0224:
                if (r0 == 0) goto L_0x022d
                r13.writeInt(r1)
                r0.writeToParcel(r13, r1)
                goto L_0x0230
            L_0x022d:
                r13.writeInt(r3)
            L_0x0230:
                return r1
            L_0x0231:
                r12.enforceInterface(r2)
                boolean r10 = r10.isOperators()
                r13.writeNoException()
                r13.writeInt(r10)
                return r1
            L_0x023f:
                r12.enforceInterface(r2)
                java.lang.String[] r10 = r10.getTunerArray()
                r13.writeNoException()
                r13.writeStringArray(r10)
                return r1
            L_0x024d:
                r12.enforceInterface(r2)
                java.lang.String r11 = r12.readString()
                int r12 = r12.readInt()
                boolean r10 = r10.getLcnStatusByCountry(r11, r12)
                r13.writeNoException()
                r13.writeInt(r10)
                return r1
            L_0x0263:
                r12.enforceInterface(r2)
                java.util.List r10 = r10.getEWBSCityInfo()
                r13.writeNoException()
                r13.writeTypedList(r10)
                return r1
            L_0x0271:
                r12.enforceInterface(r2)
                int r11 = r12.readInt()
                r10.setEwbsZone(r11)
                r13.writeNoException()
                return r1
            L_0x027f:
                r12.enforceInterface(r2)
                java.lang.String r11 = r12.readString()
                java.lang.String[] r10 = r10.getStringArray(r11)
                r13.writeNoException()
                r13.writeStringArray(r10)
                return r1
            L_0x0291:
                r12.enforceInterface(r2)
                java.lang.String r11 = r12.readString()
                java.lang.String r10 = r10.getStringParameters(r11)
                r13.writeNoException()
                r13.writeString(r10)
                return r1
            L_0x02a3:
                r12.enforceInterface(r2)
                java.lang.String r11 = r12.readString()
                java.lang.String r12 = r12.readString()
                r10.saveStringParameters(r11, r12)
                r13.writeNoException()
                return r1
            L_0x02b5:
                r12.enforceInterface(r2)
                java.lang.String r11 = r12.readString()
                int r12 = r12.readInt()
                boolean r10 = r10.saveIntParameter(r11, r12)
                r13.writeNoException()
                r13.writeInt(r10)
                return r1
            L_0x02cb:
                r12.enforceInterface(r2)
                java.lang.String r11 = r12.readString()
                int r12 = r12.readInt()
                if (r12 == 0) goto L_0x02d9
                r3 = r1
            L_0x02d9:
                int r10 = r10.getIntParameter(r11, r3)
                r13.writeNoException()
                r13.writeInt(r10)
                return r1
            L_0x02e4:
                r12.enforceInterface(r2)
                int r10 = r10.getCurrentMainSubtitleLangId()
                r13.writeNoException()
                r13.writeInt(r10)
                return r1
            L_0x02f2:
                r12.enforceInterface(r2)
                int r11 = r12.readInt()
                r10.setMainSubtitleLanguageById(r11)
                r13.writeNoException()
                return r1
            L_0x0300:
                r12.enforceInterface(r2)
                java.util.List r10 = r10.getCurrentLangNameList()
                r13.writeNoException()
                r13.writeStringList(r10)
                return r1
            L_0x030e:
                r12.enforceInterface(r2)
                int r11 = r12.readInt()
                r10.setMainAudioLanguageById(r11)
                r13.writeNoException()
                return r1
            L_0x031c:
                r12.enforceInterface(r2)
                int r10 = r10.getCurrentMainAudioLangId()
                r13.writeNoException()
                r13.writeInt(r10)
                return r1
            L_0x032a:
                r12.enforceInterface(r2)
                java.lang.String r11 = r12.readString()
                int r10 = r10.getCurrentCountryIndex(r11)
                r13.writeNoException()
                r13.writeInt(r10)
                return r1
            L_0x033c:
                r12.enforceInterface(r2)
                int r11 = r12.readInt()
                java.lang.String r12 = r12.readString()
                r10.setCountryCodeByIndex(r11, r12)
                r13.writeNoException()
                return r1
            L_0x034e:
                r12.enforceInterface(r2)
                java.lang.String r11 = r12.readString()
                java.util.List r10 = r10.getCountryDisplayList(r11)
                r13.writeNoException()
                r13.writeStringList(r10)
                return r1
            L_0x0360:
                r12.enforceInterface(r2)
                java.lang.String r10 = r10.getSupportTunerType()
                r13.writeNoException()
                r13.writeString(r10)
                return r1
            L_0x036e:
                r12.enforceInterface(r2)
                boolean r10 = r10.isMultiTuner()
                r13.writeNoException()
                r13.writeInt(r10)
                return r1
            L_0x037c:
                r12.enforceInterface(r2)
                boolean r10 = r10.isDoubleTuner()
                r13.writeNoException()
                r13.writeInt(r10)
                return r1
            L_0x038a:
                r12.enforceInterface(r2)
                java.lang.String r10 = r10.getDvbType()
                r13.writeNoException()
                r13.writeString(r10)
                return r1
            L_0x0398:
                r12.enforceInterface(r2)
                r10.setDefaultData()
                r13.writeNoException()
                return r1
            L_0x03a2:
                r13.writeString(r2)
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: nes.scan.IDvbConfigInterface.Stub.onTransact(int, android.os.Parcel, android.os.Parcel, int):boolean");
        }

        private static class Proxy implements IDvbConfigInterface {
            public static IDvbConfigInterface sDefaultImpl;
            private IBinder mRemote;

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public void setDefaultData() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(1, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().setDefaultData();
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String getDvbType() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(2, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getDvbType();
                    }
                    obtain2.readException();
                    String readString = obtain2.readString();
                    obtain2.recycle();
                    obtain.recycle();
                    return readString;
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean isDoubleTuner() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean z = false;
                    if (!this.mRemote.transact(3, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().isDoubleTuner();
                    }
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        z = true;
                    }
                    obtain2.recycle();
                    obtain.recycle();
                    return z;
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean isMultiTuner() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean z = false;
                    if (!this.mRemote.transact(4, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().isMultiTuner();
                    }
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        z = true;
                    }
                    obtain2.recycle();
                    obtain.recycle();
                    return z;
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String getSupportTunerType() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(5, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getSupportTunerType();
                    }
                    obtain2.readException();
                    String readString = obtain2.readString();
                    obtain2.recycle();
                    obtain.recycle();
                    return readString;
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public List<String> getCountryDisplayList(String str) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    if (!this.mRemote.transact(6, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getCountryDisplayList(str);
                    }
                    obtain2.readException();
                    ArrayList<String> createStringArrayList = obtain2.createStringArrayList();
                    obtain2.recycle();
                    obtain.recycle();
                    return createStringArrayList;
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void setCountryCodeByIndex(int i, String str) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeString(str);
                    if (this.mRemote.transact(7, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().setCountryCodeByIndex(i, str);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public int getCurrentCountryIndex(String str) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    if (!this.mRemote.transact(8, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getCurrentCountryIndex(str);
                    }
                    obtain2.readException();
                    int readInt = obtain2.readInt();
                    obtain2.recycle();
                    obtain.recycle();
                    return readInt;
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public int getCurrentMainAudioLangId() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(9, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getCurrentMainAudioLangId();
                    }
                    obtain2.readException();
                    int readInt = obtain2.readInt();
                    obtain2.recycle();
                    obtain.recycle();
                    return readInt;
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void setMainAudioLanguageById(int i) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (this.mRemote.transact(10, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().setMainAudioLanguageById(i);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public List<String> getCurrentLangNameList() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(11, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getCurrentLangNameList();
                    }
                    obtain2.readException();
                    ArrayList<String> createStringArrayList = obtain2.createStringArrayList();
                    obtain2.recycle();
                    obtain.recycle();
                    return createStringArrayList;
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void setMainSubtitleLanguageById(int i) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (this.mRemote.transact(12, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().setMainSubtitleLanguageById(i);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public int getCurrentMainSubtitleLangId() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(13, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getCurrentMainSubtitleLangId();
                    }
                    obtain2.readException();
                    int readInt = obtain2.readInt();
                    obtain2.recycle();
                    obtain.recycle();
                    return readInt;
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public int getIntParameter(String str, boolean z) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(z ? 1 : 0);
                    if (!this.mRemote.transact(14, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getIntParameter(str, z);
                    }
                    obtain2.readException();
                    int readInt = obtain2.readInt();
                    obtain2.recycle();
                    obtain.recycle();
                    return readInt;
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean saveIntParameter(String str, int i) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    boolean z = false;
                    if (!this.mRemote.transact(15, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().saveIntParameter(str, i);
                    }
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        z = true;
                    }
                    obtain2.recycle();
                    obtain.recycle();
                    return z;
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void saveStringParameters(String str, String str2) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    if (this.mRemote.transact(16, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().saveStringParameters(str, str2);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String getStringParameters(String str) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    if (!this.mRemote.transact(17, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getStringParameters(str);
                    }
                    obtain2.readException();
                    String readString = obtain2.readString();
                    obtain2.recycle();
                    obtain.recycle();
                    return readString;
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String[] getStringArray(String str) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    if (!this.mRemote.transact(18, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getStringArray(str);
                    }
                    obtain2.readException();
                    String[] createStringArray = obtain2.createStringArray();
                    obtain2.recycle();
                    obtain.recycle();
                    return createStringArray;
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void setEwbsZone(int i) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (this.mRemote.transact(19, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().setEwbsZone(i);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public List<CityInfoBean> getEWBSCityInfo() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(20, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getEWBSCityInfo();
                    }
                    obtain2.readException();
                    ArrayList<CityInfoBean> createTypedArrayList = obtain2.createTypedArrayList(CityInfoBean.CREATOR);
                    obtain2.recycle();
                    obtain.recycle();
                    return createTypedArrayList;
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean getLcnStatusByCountry(String str, int i) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    boolean z = false;
                    if (!this.mRemote.transact(21, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getLcnStatusByCountry(str, i);
                    }
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        z = true;
                    }
                    obtain2.recycle();
                    obtain.recycle();
                    return z;
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String[] getTunerArray() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(22, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getTunerArray();
                    }
                    obtain2.readException();
                    String[] createStringArray = obtain2.createStringArray();
                    obtain2.recycle();
                    obtain.recycle();
                    return createStringArray;
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean isOperators() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean z = false;
                    if (!this.mRemote.transact(23, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().isOperators();
                    }
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        z = true;
                    }
                    obtain2.recycle();
                    obtain.recycle();
                    return z;
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void editSatelliteAndTransponder(int i, int i2, SatelliteBean satelliteBean, TransponderBean transponderBean) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    if (satelliteBean != null) {
                        obtain.writeInt(1);
                        satelliteBean.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (transponderBean != null) {
                        obtain.writeInt(1);
                        transponderBean.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (this.mRemote.transact(24, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        if (obtain2.readInt() != 0) {
                            satelliteBean.readFromParcel(obtain2);
                        }
                        if (obtain2.readInt() != 0) {
                            transponderBean.readFromParcel(obtain2);
                        }
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().editSatelliteAndTransponder(i, i2, satelliteBean, transponderBean);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void startLockTp(SatelliteBean satelliteBean, TransponderBean transponderBean) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (satelliteBean != null) {
                        obtain.writeInt(1);
                        satelliteBean.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (transponderBean != null) {
                        obtain.writeInt(1);
                        transponderBean.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (this.mRemote.transact(25, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        if (obtain2.readInt() != 0) {
                            satelliteBean.readFromParcel(obtain2);
                        }
                        if (obtain2.readInt() != 0) {
                            transponderBean.readFromParcel(obtain2);
                        }
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().startLockTp(satelliteBean, transponderBean);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String getSignalInfo() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(26, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getSignalInfo();
                    }
                    obtain2.readException();
                    String readString = obtain2.readString();
                    obtain2.recycle();
                    obtain.recycle();
                    return readString;
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean doDvbSScanAction(List<SatelliteBean> list, TransponderBean transponderBean, int i, int i2, int i3) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeTypedList(list);
                    boolean z = true;
                    if (transponderBean != null) {
                        obtain.writeInt(1);
                        transponderBean.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeInt(i3);
                    if (!this.mRemote.transact(27, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().doDvbSScanAction(list, transponderBean, i, i2, i3);
                    }
                    obtain2.readException();
                    if (obtain2.readInt() == 0) {
                        z = false;
                    }
                    obtain2.readTypedList(list, SatelliteBean.CREATOR);
                    if (obtain2.readInt() != 0) {
                        transponderBean.readFromParcel(obtain2);
                    }
                    obtain2.recycle();
                    obtain.recycle();
                    return z;
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public List<FrequencyBean> getFrequencyList() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(28, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getFrequencyList();
                    }
                    obtain2.readException();
                    ArrayList<FrequencyBean> createTypedArrayList = obtain2.createTypedArrayList(FrequencyBean.CREATOR);
                    obtain2.recycle();
                    obtain.recycle();
                    return createTypedArrayList;
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void setVoltage(int i) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (this.mRemote.transact(29, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().setVoltage(i);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void startTuneAction(String str, int i, int i2, int i3, int i4) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeInt(i3);
                    obtain.writeInt(i4);
                    if (this.mRemote.transact(30, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().startTuneAction(str, i, i2, i3, i4);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void stopTuneAction() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(31, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().stopTuneAction();
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void enableDishLimits(boolean z) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(z ? 1 : 0);
                    if (this.mRemote.transact(32, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().enableDishLimits(z);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void setDishELimits() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(33, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().setDishELimits();
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void setDishWLimits() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(34, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().setDishWLimits();
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void storeDishPosition(int i) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (this.mRemote.transact(35, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().storeDishPosition(i);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void moveDishToPosition(int i) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (this.mRemote.transact(36, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().moveDishToPosition(i);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void setDishPosition(int i) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (this.mRemote.transact(37, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().setDishPosition(i);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void dishMove(int i, int i2) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    if (this.mRemote.transact(38, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().dishMove(i, i2);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }


            public void closeDiseqc12Setting() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(39, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().closeDiseqc12Setting();
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void updateDefaultDatabaseSelected(SatelliteBean satelliteBean) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (satelliteBean != null) {
                        obtain.writeInt(1);
                        satelliteBean.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (this.mRemote.transact(40, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        if (obtain2.readInt() != 0) {
                            satelliteBean.readFromParcel(obtain2);
                        }
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().updateDefaultDatabaseSelected(satelliteBean);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public List<SatelliteBean> getDefaultSatelliteList() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(41, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getDefaultSatelliteList();
                    }
                    obtain2.readException();
                    ArrayList<SatelliteBean> createTypedArrayList = obtain2.createTypedArrayList(SatelliteBean.CREATOR);
                    obtain2.recycle();
                    obtain.recycle();
                    return createTypedArrayList;
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public List<TransponderBean> getDefaultTransponderList(long j) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeLong(j);
                    if (!this.mRemote.transact(42, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getDefaultTransponderList(j);
                    }
                    obtain2.readException();
                    ArrayList<TransponderBean> createTypedArrayList = obtain2.createTypedArrayList(TransponderBean.CREATOR);
                    obtain2.recycle();
                    obtain.recycle();
                    return createTypedArrayList;
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void startSearchChannel(String str) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    if (this.mRemote.transact(43, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().startSearchChannel(str);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void stopSearchChannel(boolean z) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(z ? 1 : 0);
                    if (this.mRemote.transact(44, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().stopSearchChannel(z);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void registerCallback(ScanEventHandler scanEventHandler) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(scanEventHandler != null ? scanEventHandler.asBinder() : null);
                    if (this.mRemote.transact(45, obtain, obtain2, 0) || (Stub.getDefaultImpl() == null)) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().registerCallback(scanEventHandler);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void unregisterCallback(ScanEventHandler scanEventHandler) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(scanEventHandler != null ? scanEventHandler.asBinder() : null);
                    if (this.mRemote.transact(46, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().unregisterCallback(scanEventHandler);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void startDvbTuneAction(String str) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    if (this.mRemote.transact(47, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().startDvbTuneAction(str);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public static boolean setDefaultImpl(IDvbConfigInterface iDvbConfigInterface) {
            if (Proxy.sDefaultImpl != null || iDvbConfigInterface == null) {
                return false;
            }
            Proxy.sDefaultImpl = iDvbConfigInterface;
            return true;
        }

        public static IDvbConfigInterface getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
