package com.diipl.moviebeam.dtv;

import com.diipl.moviebeam.dtv.interfaces.IScanEventListener;
import com.diipl.moviebeam.dtv.interfaces.OnServiceConnectedCallback;
import com.diipl.moviebeam.dtv.interfaces.OnServiceDisConnectedCallback;

import java.io.InputStream;
import java.util.List;

import kotlin.Metadata;

public interface IDtvSetup {
    void closeDiseqc12Setting();

    void destroy();

    void dishMove(int i, int i2);

    void editSatelliteAndTransponder(int i, int i2, SatelliteBean satelliteBean, TransponderBean transponderBean);

    void enableDishLimits(boolean z);

    List<String> getCountryDisplayList(String str);

    int getCurrentCountryIndex(String str);

    List<SatelliteBean> getDefaultSatelliteList();

    List<TransponderBean> getDefaultTransponderList(long j);

    String getDvbType();

    String getFirstAudioLang();

    String getFirstSubtitleLang();

    List<FrequencyBean> getFrequencyList();

    int getIntParameter(String str);

    int getIntParameter(String str, boolean z);

    List<String> getLangNameList();

    boolean getLcnStatusByCountry(String str);

    String getStringParameters(String str);

    String getSupportTunerType();

    String[] getTunerArray();

    void initialize();

    boolean isAvailable();

    boolean isMultiTuner();

    boolean isOperators();

    void loadCountrySource(InputStream inputStream);

    void moveDishToPosition(int i);

    void openDiseqc12Setting();

    SignalBean probeSignal();

    void registerScanEventListener(IScanEventListener iScanEventListener);

    void removeOnServiceConnectedCallback(OnServiceConnectedCallback onServiceConnectedCallback);

    void removeOnServiceDisConnectedCallback(OnServiceDisConnectedCallback onServiceDisConnectedCallback);

    boolean saveIntParameter(String str, int i);

    boolean saveSatelliteScanParams(List<SatelliteBean> list, TransponderBean transponderBean, int i, int i2, int i3);

    void saveStringParameters(String str, String str2);

    void setCountryCodeByIndex(int i, String str, boolean z);

    void setDVBCAnnexMode(int i);

    void setDishELimits();

    void setDishPosition(int i);

    void setDishWLimits();

    void setFirstAudioLanguage(String str, int i);

    void setFirstSubtitleLang(String str, int i);

    void setOnServiceConnectedCallback(OnServiceConnectedCallback onServiceConnectedCallback);

    void setOnServiceDisConnectedCallback(OnServiceDisConnectedCallback onServiceDisConnectedCallback);

    void setVoltage(int i);

    void startDvbTuneAction(String str);

    void startSearchChannel(String str);

    void startTuneAction(String str, int i, int i2, int i3, int i4);

    void startTuneAction(SatelliteBean satelliteBean, TransponderBean transponderBean);

    void stopSearchChannel(boolean z);

    void stopTuneAction();

    void storeDishPosition(int i);

    void unregisterScanEventListener(IScanEventListener iScanEventListener);

    void updateDefaultDatabaseSelected(SatelliteBean satelliteBean);

}
