package com.diipl.moviebeam.dtv;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.diipl.moviebeam.dtv.dto.DBData;
import com.diipl.moviebeam.dtv.interfaces.DtvSetupImpl;
import com.diipl.moviebeam.dtv.interfaces.IDvbConfigInterface;
import com.diipl.moviebeam.dtv.interfaces.ScanEventHandler;
import com.diipl.moviebeam.dtv.utils.InstallerConstant;
import com.google.gson.Gson;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;


public final class DtvKitSetup extends DtvSetupImpl {
    private static final String TAG = "DtvKitSetup";
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    public static final String DVB_CONFIG_SERVICE = "org.dtvkit.inputsource.service.DvbScanConfigService";
    public static final String DVB_INPUT_SOURCE_PACKAGE = "org.dtvkit.inputsource";
    /* access modifiers changed from: private */
    private final Context mContext;
    private DvbServiceConnection mDvbConnection;
    private IDvbConfigInterface mDvbManager;
    private boolean mIsBind;
    private Map<String, String> mKeyMap = new LinkedHashMap();


    private final ScanEventHandlerImpl getScanEventHandler() {
        return (ScanEventHandlerImpl) this.getScanEventHandler();
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public DtvKitSetup(Context context) {
        this.mContext = context;
        this.mKeyMap.put("lcn_switch", "lcnSwitch");
        this.mKeyMap.put("subtitle_switch", "subtitleSwitch");
        this.mKeyMap.put("dvbc_annex_mode", "dvbc_annex_mode");
    }

    public DBData getMDBData() {
        return new DBData(mContext);
    }

    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        public final String getTAG() {
            return DtvKitSetup.TAG;
        }
    }

    public void initialize() {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("org.dtvkit.inputsource", "org.dtvkit.inputsource.service.DvbScanConfigService"));
        DvbServiceConnection dvbServiceConnection = new DvbServiceConnection(this);
        this.mDvbConnection = dvbServiceConnection;
        Context context = this.mContext;
        Intrinsics.checkNotNull(dvbServiceConnection);
        context.bindService(intent, dvbServiceConnection, Context.BIND_AUTO_CREATE);
        IDvbConfigInterface iDvbConfigInterface = this.mDvbManager;
        if (iDvbConfigInterface != null) {
            iDvbConfigInterface.setDefaultData();
        }
    }

    public void destroy() {
        String str = TAG;
        Log.d(str, "unBindService: " + this.mDvbConnection);
        DvbServiceConnection dvbServiceConnection = this.mDvbConnection;
        if (dvbServiceConnection != null) {
            this.mContext.unbindService(dvbServiceConnection);
        }
    }

    public boolean isAvailable() {
        return this.mIsBind && this.mDvbManager != null;
    }

    public boolean saveSatelliteScanParams(List<SatelliteBean> list, TransponderBean transponderBean, int i, int i2, int i3) {
        IDvbConfigInterface iDvbConfigInterface = this.mDvbManager;
        if (iDvbConfigInterface == null) {
            return false;
        }
        return iDvbConfigInterface.doDvbSScanAction(list, transponderBean, i, i2, i3);
    }

    public String getSupportTunerType() {
        IDvbConfigInterface iDvbConfigInterface = this.mDvbManager;
        if (iDvbConfigInterface == null) {
            return "";
        }
        String supportTunerType = null;
        supportTunerType = iDvbConfigInterface.getSupportTunerType();
        Intrinsics.checkNotNullExpressionValue(supportTunerType, "mDvbManager!!.supportTunerType");
        return supportTunerType;
    }

    public String getDvbType() {
        boolean z;
        String[] tunerArray = getTunerArray();
        IDvbConfigInterface iDvbConfigInterface = this.mDvbManager;
        String dvbType = iDvbConfigInterface != null ? iDvbConfigInterface.getDvbType() : null;
        if (tunerArray != null) {
            int length = tunerArray.length;
            int i = 0;
            while (true) {
                if (i >= length) {
                    z = false;
                    break;
                } else if (Intrinsics.areEqual((Object) tunerArray[i], (Object) dvbType)) {
                    z = true;
                    break;
                } else {
                    i++;
                }
            }
            if (!z) {
                String contains = contains(DvbContract.Tuner.DVB_T, tunerArray);
                if (contains != null) {
                    dvbType = contains;
                } else if (!tunerArray.equals(DvbContract.Tuner.DVB_S)) {
                    dvbType = tunerArray[0];
                }
                saveStringParameters("dvb_tuner_type", dvbType != null ? dvbType : "");
            }
            Log.d(TAG, "getDvbType: " + dvbType);
        }
        return dvbType;
    }

    public boolean isMultiTuner() {
        IDvbConfigInterface iDvbConfigInterface = this.mDvbManager;
        boolean isMultiTuner = iDvbConfigInterface != null ? iDvbConfigInterface.isMultiTuner() : false;
        String[] tunerArray = getTunerArray();
        if (tunerArray != null) {
            for (String str : tunerArray) {
                Log.d(TAG, "isMultiTuner: " + str);
            }
        }
        if (!isMultiTuner || tunerArray == null || tunerArray.length <= 1) {
            return false;
        }
        return true;
    }

    public String[] getTunerArray() {
        IDvbConfigInterface iDvbConfigInterface = this.mDvbManager;
        if (iDvbConfigInterface != null) {
            return iDvbConfigInterface.getTunerArray();
        }
        return null;
    }

    public boolean isOperators() {
        IDvbConfigInterface iDvbConfigInterface = this.mDvbManager;
        if (iDvbConfigInterface != null) {
            return iDvbConfigInterface.isOperators();
        }
        return false;
    }

    public void editSatelliteAndTransponder(int i, int i2, SatelliteBean satelliteBean, TransponderBean transponderBean) {
        IDvbConfigInterface iDvbConfigInterface = this.mDvbManager;
        if (iDvbConfigInterface != null) {
            iDvbConfigInterface.editSatelliteAndTransponder(i, i2, satelliteBean, transponderBean);
        }
    }

    public void setDVBCAnnexMode(int i) {
        if (!Intrinsics.areEqual((Object) getDvbType(), (Object) DvbContract.Tuner.DVB_C)) {
            return;
        }
        if (Build.VERSION.SDK_INT > 29) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("key", "dvbc_annex_mode");
            contentValues.put("value", Integer.valueOf(i));
            DBData mDBData = getMDBData();
            Uri uri = DvbContract.TvSettings.CONTENT_URI;
            Intrinsics.checkNotNullExpressionValue(uri, "DvbContract.TvSettings.CONTENT_URI");
            if (!mDBData.update(uri, contentValues, "key=?", new String[]{"dvbc_annex_mode"})) {
                DBData mDBData2 = getMDBData();
                Uri uri2 = DvbContract.TvSettings.CONTENT_URI;
                Intrinsics.checkNotNullExpressionValue(uri2, "DvbContract.TvSettings.CONTENT_URI");
                mDBData2.insert(uri2, contentValues);
                return;
            }
            return;
        }
        Intrinsics.checkNotNullExpressionValue(new Intent("nes.action.dvb.c.set.annex").putExtra("dvbc_annex_mode", i), "intent.putExtra(\"dvbc_annex_mode\", mode)");
    }

    public List<String> getCountryDisplayList(String str) {
        Intrinsics.checkNotNullParameter(str, "type");
        IDvbConfigInterface iDvbConfigInterface = this.mDvbManager;
        if (iDvbConfigInterface != null) {
            return iDvbConfigInterface.getCountryDisplayList(str);
        }
        return null;
    }

    public void setCountryCodeByIndex(int i, String str, boolean z) {
        Intrinsics.checkNotNullParameter(str, "type");
        IDvbConfigInterface iDvbConfigInterface = this.mDvbManager;
        if (iDvbConfigInterface != null) {
            iDvbConfigInterface.setCountryCodeByIndex(i, str);
        }
        if (z) {
            IDvbConfigInterface iDvbConfigInterface2 = this.mDvbManager;
            int i2 = 0;
            setFirstAudioLanguage("", iDvbConfigInterface2 != null ? iDvbConfigInterface2.getCurrentMainAudioLangId() : 0);
            IDvbConfigInterface iDvbConfigInterface3 = this.mDvbManager;
            if (iDvbConfigInterface3 != null) {
                i2 = iDvbConfigInterface3.getCurrentMainSubtitleLangId();
            }
            setFirstSubtitleLang("", i2);
        }
    }

    public int getCurrentCountryIndex(String str) {
        Intrinsics.checkNotNullParameter(str, "type");
        IDvbConfigInterface iDvbConfigInterface = this.mDvbManager;
        if (iDvbConfigInterface != null) {
            return iDvbConfigInterface.getCurrentCountryIndex(str);
        }
        return 0;
    }

    public String getFirstAudioLang() {
        if (this.mDvbManager == null) {
            return null;
        }
        List<String> langNameList = getLangNameList();
        Collection collection = langNameList;
        boolean z = false;
        if (collection == null || collection.isEmpty()) {
            return null;
        }
        if (Build.VERSION.SDK_INT > 29) {
            String tvSettingData = getTvSettingData("firstAudioLanguageIndex");
            CharSequence charSequence = tvSettingData;
            if (charSequence == null || charSequence.length() == 0) {
                z = true;
            }
            if (z) {
                return null;
            }
            return (String) findEntity(langNameList, Integer.parseInt(tvSettingData));
        }
        IDvbConfigInterface iDvbConfigInterface = this.mDvbManager;
        Intrinsics.checkNotNull(iDvbConfigInterface);
        return (String) findEntity(langNameList, iDvbConfigInterface.getCurrentMainAudioLangId());
    }

    public void setFirstAudioLanguage(String str, int i) {
        Intrinsics.checkNotNullParameter(str, "languageCode");
        if (Build.VERSION.SDK_INT > 29) {
            updateTvSettingData("firstAudioLanguageIndex", i);
            updateTvSettingData("secondAudioLanguageIndex", i);
            return;
        }
        IDvbConfigInterface iDvbConfigInterface = this.mDvbManager;
        if (iDvbConfigInterface != null) {
            iDvbConfigInterface.setMainAudioLanguageById(i);
        }
    }

    public String getFirstSubtitleLang() {
        if (this.mDvbManager == null) {
            return null;
        }
        List<String> langNameList = getLangNameList();
        Collection collection = langNameList;
        boolean z = false;
        if (collection == null || collection.isEmpty()) {
            return "";
        }
        if (Build.VERSION.SDK_INT > 29) {
            String tvSettingData = getTvSettingData("firstSubtitleLanguageIndex");
            CharSequence charSequence = tvSettingData;
            if (charSequence == null || charSequence.length() == 0) {
                z = true;
            }
            if (z) {
                return null;
            }
            return (String) findEntity(langNameList, Integer.parseInt(tvSettingData));
        }
        IDvbConfigInterface iDvbConfigInterface = this.mDvbManager;
        Intrinsics.checkNotNull(iDvbConfigInterface);
        return (String) findEntity(langNameList, iDvbConfigInterface.getCurrentMainSubtitleLangId());
    }

    public void setFirstSubtitleLang(String str, int i) {
        Intrinsics.checkNotNullParameter(str, "languageCode");
        if (Build.VERSION.SDK_INT > 29) {
            updateTvSettingData("firstSubtitleLanguageIndex", i);
            updateTvSettingData("secondSubtitleLanguageIndex", i);
            return;
        }
        IDvbConfigInterface iDvbConfigInterface = this.mDvbManager;
        if (iDvbConfigInterface != null) {
            iDvbConfigInterface.setMainSubtitleLanguageById(i);
        }
    }

    public List<String> getLangNameList() {
        if (Build.VERSION.SDK_INT > 29) {
            DBData mDBData = getMDBData();
            Uri uri = DvbContract.TvSettings.DYNAMIC_CONTENT_URI;
            return new Gson().fromJson((String) mDBData.queryOne(uri, new String[]{"language_name_list"}, null,
                    null, null,
                    DtvKitSetup$getLangNameList$languageNameJson$1.INSTANCE), new DtvKitSetup$getLangNameList$1().getType());
        }
        IDvbConfigInterface iDvbConfigInterface = this.mDvbManager;
        if (iDvbConfigInterface != null) {
            return iDvbConfigInterface.getCurrentLangNameList();
        }
        return null;
    }

    public List<FrequencyBean> getFrequencyList() {
        IDvbConfigInterface iDvbConfigInterface = this.mDvbManager;
        if (iDvbConfigInterface != null) {
            return iDvbConfigInterface.getFrequencyList();
        }
        return null;
    }

    public boolean getLcnStatusByCountry(String str) {
        Intrinsics.checkNotNullParameter(str, "country");
        String dvbType = getDvbType();
        if (dvbType == null) {
            dvbType = "";
        }
        IDvbConfigInterface iDvbConfigInterface = this.mDvbManager;
        Boolean valueOf = iDvbConfigInterface != null ? Boolean.valueOf(iDvbConfigInterface.getLcnStatusByCountry(dvbType, getCurrentCountryIndex(dvbType))) : null;
        if (valueOf != null) {
            return valueOf.booleanValue();
        }
        return false;
    }

    public void setVoltage(int i) {
        IDvbConfigInterface iDvbConfigInterface = this.mDvbManager;
        if (iDvbConfigInterface != null) {
            iDvbConfigInterface.setVoltage(i);
        }
    }

    public int getIntParameter(String str, boolean z) {
        Intrinsics.checkNotNullParameter(str, "key");
        IDvbConfigInterface iDvbConfigInterface = this.mDvbManager;
        if (iDvbConfigInterface != null) {
            return iDvbConfigInterface.getIntParameter(str, z);
        }
        return 0;
    }

    public int getIntParameter(String str) {
        Intrinsics.checkNotNullParameter(str, "key");
        if (Build.VERSION.SDK_INT <= 29) {
            return getIntParameter(str, false);
        }
        String str2 = this.mKeyMap.get(str);
        if (str2 == null) {
            return getIntParameter(str, false);
        }
        String tvSettingData = getTvSettingData(str2);
        if (tvSettingData != null) {
            return Integer.parseInt(tvSettingData);
        }
        return 0;
    }

    public boolean saveIntParameter(String str, int i) {
        Boolean bool;
        Intrinsics.checkNotNullParameter(str, "key");
        if (Build.VERSION.SDK_INT > 29) {
            String str2 = this.mKeyMap.get(str);
            if (str2 != null) {
                bool = Boolean.valueOf(updateTvSettingData(str2, i));
            } else {
                IDvbConfigInterface iDvbConfigInterface = this.mDvbManager;
                bool = iDvbConfigInterface != null ? Boolean.valueOf(iDvbConfigInterface.saveIntParameter(str, i)) : null;
            }
            if (bool != null) {
                return bool.booleanValue();
            }
            return false;
        }
        IDvbConfigInterface iDvbConfigInterface2 = this.mDvbManager;
        if (iDvbConfigInterface2 != null) {
            return iDvbConfigInterface2.saveIntParameter(str, i);
        }
        return false;
    }

    public void saveStringParameters(String str, String str2) {
        Intrinsics.checkNotNullParameter(str, "key");
        Intrinsics.checkNotNullParameter(str2, "value");
        IDvbConfigInterface iDvbConfigInterface = this.mDvbManager;
        if (iDvbConfigInterface != null) {
            iDvbConfigInterface.saveStringParameters(str, str2);
        }
    }

    public String getStringParameters(String str) {
        Intrinsics.checkNotNullParameter(str, "key");
        IDvbConfigInterface iDvbConfigInterface = this.mDvbManager;
        if (iDvbConfigInterface != null) {
            return iDvbConfigInterface.getStringParameters(str);
        }
        return null;
    }

    public void startTuneAction(String str, int i, int i2, int i3, int i4) {
        Intrinsics.checkNotNullParameter(str, "type");
        IDvbConfigInterface iDvbConfigInterface = this.mDvbManager;
        if (iDvbConfigInterface != null) {
            iDvbConfigInterface.startTuneAction(str, i, i2, i3, i4);
        }
    }

    public void startTuneAction(SatelliteBean satelliteBean, TransponderBean transponderBean) {
        Intrinsics.checkNotNullParameter(satelliteBean, "satelliteBean");
        Intrinsics.checkNotNullParameter(transponderBean, "transponderBean");
        IDvbConfigInterface iDvbConfigInterface = this.mDvbManager;
        if (iDvbConfigInterface != null) {
            iDvbConfigInterface.startLockTp(satelliteBean, transponderBean);
        }
    }

    public void stopTuneAction() {
        IDvbConfigInterface iDvbConfigInterface = this.mDvbManager;
        if (iDvbConfigInterface != null) {
            iDvbConfigInterface.stopTuneAction();
        }
    }

    public SignalBean probeSignal() {
        String str;
        IDvbConfigInterface iDvbConfigInterface = this.mDvbManager;
        if (iDvbConfigInterface == null || (str = iDvbConfigInterface.getSignalInfo()) == null) {
            str = "";
        }
        SignalBean signalBean = new SignalBean();
        signalBean.parseDtvKitSignal(str);
        return signalBean;
    }

    public void startSearchChannel(String str) {
        Intrinsics.checkNotNullParameter(str, InstallerConstant.EXTRA_START_INSTALL_PARAM);
        IDvbConfigInterface iDvbConfigInterface = this.mDvbManager;
        if (iDvbConfigInterface != null) {
            iDvbConfigInterface.startSearchChannel(str);
        }
    }

    public void stopSearchChannel(boolean z) {
        IDvbConfigInterface iDvbConfigInterface = this.mDvbManager;
        if (iDvbConfigInterface != null) {
            iDvbConfigInterface.stopSearchChannel(z);
        }
    }

    public void startDvbTuneAction(String str) {
        Intrinsics.checkNotNullParameter(str, InstallerConstant.EXTRA_START_INSTALL_PARAM);
        IDvbConfigInterface iDvbConfigInterface = this.mDvbManager;
        if (iDvbConfigInterface != null) {
            iDvbConfigInterface.startDvbTuneAction(str);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0004, code lost:
        r0 = r0.getDefaultSatelliteList();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public List<SatelliteBean> getDefaultSatelliteList() {
        /*
            r0 = this;
            nes.scan.IDvbConfigInterface r0 = r0.mDvbManager
            if (r0 == 0) goto L_0x000b
            java.util.List r0 = r0.getDefaultSatelliteList()
            if (r0 == 0) goto L_0x000b
            goto L_0x0012
        L_0x000b:
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.util.List r0 = (java.util.List) r0
        L_0x0012:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.commonsetup.dtvkit.DtvKitSetup.getDefaultSatelliteList():java.util.List");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0004, code lost:
        r0 = r0.getDefaultTransponderList(r1);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public List<TransponderBean> getDefaultTransponderList(long r1) {
        /*
            r0 = this;
            IDvbConfigInterface r0 = r0.mDvbManager
            if (r0 == 0) goto L_0x000b
            java.util.List r0 = r0.getDefaultTransponderList(r1)
            if (r0 == 0) goto L_0x000b
            goto L_0x0012
        L_0x000b:
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.util.List r0 = (java.util.List) r0
        L_0x0012:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.commonsetup.dtvkit.DtvKitSetup.getDefaultTransponderList(long):java.util.List");
    }

    public void updateDefaultDatabaseSelected(SatelliteBean satelliteBean) {
        try {
            IDvbConfigInterface iDvbConfigInterface = this.mDvbManager;
            if (iDvbConfigInterface != null) {
                iDvbConfigInterface.updateDefaultDatabaseSelected(satelliteBean);
            }
        } catch (Exception unused) {
        }
    }

    public void enableDishLimits(boolean z) {
        try {
            IDvbConfigInterface iDvbConfigInterface = this.mDvbManager;
            if (iDvbConfigInterface != null) {
                iDvbConfigInterface.enableDishLimits(z);
            }
        } catch (Exception unused) {
        }
    }

    public void setDishELimits() {
        try {
            IDvbConfigInterface iDvbConfigInterface = this.mDvbManager;
            if (iDvbConfigInterface != null) {
                iDvbConfigInterface.setDishELimits();
            }
        } catch (Exception unused) {
        }
    }

    public void setDishWLimits() {
        try {
            IDvbConfigInterface iDvbConfigInterface = this.mDvbManager;
            if (iDvbConfigInterface != null) {
                iDvbConfigInterface.setDishWLimits();
            }
        } catch (Exception unused) {
        }
    }

    public void storeDishPosition(int i) {
        try {
            IDvbConfigInterface iDvbConfigInterface = this.mDvbManager;
            if (iDvbConfigInterface != null) {
                iDvbConfigInterface.storeDishPosition(i);
            }
        } catch (Exception unused) {
        }
    }

    public void moveDishToPosition(int i) {
        try {
            IDvbConfigInterface iDvbConfigInterface = this.mDvbManager;
            if (iDvbConfigInterface != null) {
                iDvbConfigInterface.moveDishToPosition(i);
            }
        } catch (Exception unused) {
        }
    }

    public void setDishPosition(int i) {
        IDvbConfigInterface iDvbConfigInterface = this.mDvbManager;
        if (iDvbConfigInterface != null) {
            iDvbConfigInterface.setDishPosition(i);
        }
    }

    public void dishMove(int i, int i2) {
        try {
            IDvbConfigInterface iDvbConfigInterface = this.mDvbManager;
            if (iDvbConfigInterface != null) {
                iDvbConfigInterface.dishMove(i, i2);
            }
        } catch (Exception unused) {
        }
    }

    public void closeDiseqc12Setting() {
        try {
            IDvbConfigInterface iDvbConfigInterface = this.mDvbManager;
            if (iDvbConfigInterface != null) {
                iDvbConfigInterface.closeDiseqc12Setting();
            }
        } catch (Exception unused) {
        }
    }

    public void openDiseqc12Setting() {
        try {
            IDvbConfigInterface iDvbConfigInterface = this.mDvbManager;
            if (iDvbConfigInterface != null) {
                iDvbConfigInterface.saveIntParameter("diseqc_1_2_setting", 1);
            }
        } catch (Exception e) {
            String str = TAG;
            Log.d(str, "openDiseqc12Setting: " + e.getMessage());
        }
    }

    @Metadata(bv = {1, 0, 3}, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0000\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\u001c\u0010\u0007\u001a\u00020\b2\b\u0010\t\u001a\u0004\u0018\u00010\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u0016J\u0012\u0010\r\u001a\u00020\b2\b\u0010\t\u001a\u0004\u0018\u00010\nH\u0016R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00030\u0006X\u0004¢\u0006\u0002\n\u0000¨\u0006\u000e"}, d2 = {"Lcom/commonsetup/dtvkit/DtvKitSetup$DvbServiceConnection;", "Landroid/content/ServiceConnection;", "configManager", "Lcom/commonsetup/dtvkit/DtvKitSetup;", "(Lcom/commonsetup/dtvkit/DtvKitSetup;)V", "weakReference", "Ljava/lang/ref/WeakReference;", "onServiceConnected", "", "name", "Landroid/content/ComponentName;", "service", "Landroid/os/IBinder;", "onServiceDisconnected", "CommonSetup_release"}, k = 1, mv = {1, 4, 2})
    /* compiled from: DtvKitSetup.kt */
    public static final class DvbServiceConnection implements ServiceConnection {
        private final WeakReference<DtvKitSetup> weakReference;

        public DvbServiceConnection(DtvKitSetup dtvKitSetup) {
            Intrinsics.checkNotNullParameter(dtvKitSetup, "configManager");
            this.weakReference = new WeakReference<>(dtvKitSetup);
        }

        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d("TAG", "onServiceConnected: ");
            DtvKitSetup dtvKitSetup = (DtvKitSetup) this.weakReference.get();
            if (dtvKitSetup != null) {
                dtvKitSetup.serviceConnected(iBinder);
            }
        }

        public void onServiceDisconnected(ComponentName componentName) {
            Log.d("TAG", "onServiceDisconnected: ");
            DtvKitSetup dtvKitSetup = (DtvKitSetup) this.weakReference.get();
            if (dtvKitSetup != null) {
                dtvKitSetup.serviceDisconnected();
            }
        }
    }

    public static final class ScanEventHandlerImpl extends ScanEventHandler.Stub {
        private final WeakReference<DtvKitSetup> weakReference;

        public ScanEventHandlerImpl(DtvKitSetup dtvKitSetup) {
            Intrinsics.checkNotNullParameter(dtvKitSetup, "configManager");
            this.weakReference = new WeakReference<>(dtvKitSetup);
        }

        public void onEvent(String str, String str2) {
            Intrinsics.checkNotNullParameter(str, "eventKey");
            Intrinsics.checkNotNullParameter(str2, "json");
            DtvKitSetup dtvKitSetup = (DtvKitSetup) this.weakReference.get();
            if (dtvKitSetup != null) {
                dtvKitSetup.notifyScanEvent(str, str2);
            }
        }
    }

    /* access modifiers changed from: private */
    public final void serviceConnected(IBinder iBinder) {
        IDvbConfigInterface asInterface = IDvbConfigInterface.Stub.asInterface(iBinder);
        this.mDvbManager = asInterface;
        if (asInterface != null) {
            asInterface.setDefaultData();
        }
        this.mIsBind = true;
        IDvbConfigInterface iDvbConfigInterface = this.mDvbManager;
        if (iDvbConfigInterface != null) {
            iDvbConfigInterface.registerCallback(getScanEventHandler());
        }
        notifyServiceConnected();
    }

    /* access modifiers changed from: private */
    public final void serviceDisconnected() {
        IDvbConfigInterface iDvbConfigInterface = null;
        this.mDvbManager = iDvbConfigInterface;
        this.mIsBind = false;
        if (iDvbConfigInterface != null) {
            iDvbConfigInterface.unregisterCallback(getScanEventHandler());
        }
        notifyServiceDisConnected();
    }

    private final boolean updateTvSettingData(String str, int i) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("value", Integer.valueOf(i));
            DBData mDBData = getMDBData();
            Uri uri = DvbContract.TvSettings.CONTENT_URI;
            Intrinsics.checkNotNullExpressionValue(uri, "DvbContract.TvSettings.CONTENT_URI");
            if (mDBData.update(uri, contentValues, "key=?", new String[]{str})) {
                return true;
            }
            contentValues.put("key", str);
            DBData mDBData2 = getMDBData();
            Uri uri2 = DvbContract.TvSettings.CONTENT_URI;
            Intrinsics.checkNotNullExpressionValue(uri2, "DvbContract.TvSettings.CONTENT_URI");
            mDBData2.insert(uri2, contentValues);
            return true;
        } catch (Exception unused) {
            return false;
        }
    }

    private final String getTvSettingData(String str) {
        DBData mDBData = getMDBData();
        Uri uri = DvbContract.TvSettings.CONTENT_URI;
        Intrinsics.checkNotNullExpressionValue(uri, "DvbContract.TvSettings.CONTENT_URI");
        return (String) mDBData.queryOne(uri, (String[]) null, "key=?", new String[]{str}, (String) null, DtvKitSetup$getTvSettingData$index$1.INSTANCE);
    }

    private final <T> T findEntity(List<? extends T> list, int i) {
        if (!list.isEmpty() && i < list.size() && i >= 0) {
            return list.get(i);
        }
        return null;
    }

    private final String contains(String str, String[] strArr) {
        if (str != null) {
            CharSequence charSequence = str;
            boolean z = true;
            if (!(charSequence.length() == 0) && strArr != null) {
                if (strArr.length != 0) {
                    z = false;
                }
                if (!z) {
                    for (String str2 : strArr) {
                        if (str2.contains(charSequence)) {
                            return str2;
                        }
                    }
                }
            }
        }
        return null;
    }
}
