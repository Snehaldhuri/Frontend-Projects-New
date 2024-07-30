package com.diipl.moviebeam.dtv.interfaces;

import com.diipl.moviebeam.dtv.IDtvSetup;
import com.diipl.moviebeam.dtv.SatelliteBean;
import com.diipl.moviebeam.dtv.TransponderBean;
import com.diipl.moviebeam.dtv.utils.InstallerConstant;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;

import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

public abstract class DtvSetupImpl implements IDtvSetup {
    private final HashSet<OnServiceConnectedCallback> callbackList;
    private final HashSet<OnServiceDisConnectedCallback> disConnectedCallbackList;
    private final HashSet<IScanEventListener> scanEventListenerList;

    public DtvSetupImpl() {
        this((SourceLoader) null, 1, (DefaultConstructorMarker) null);
    }

    public void closeDiseqc12Setting() {
    }

    public void dishMove(int i, int i2) {
    }

    public void editSatelliteAndTransponder(int i, int i2, SatelliteBean satelliteBean, TransponderBean transponderBean) {
    }

    public void enableDishLimits(boolean z) {
    }

    public void loadCountrySource(InputStream inputStream) {
    }

    public void moveDishToPosition(int i) {
    }

    public void openDiseqc12Setting() {
    }

    public void setDVBCAnnexMode(int i) {
    }

    public void setDishELimits() {
    }

    public void setDishPosition(int i) {
    }

    public void setDishWLimits() {
    }

    public void startDvbTuneAction(String str) {
        Intrinsics.checkNotNullParameter(str, InstallerConstant.EXTRA_START_INSTALL_PARAM);
    }

    public void startSearchChannel(String str) {
        Intrinsics.checkNotNullParameter(str, InstallerConstant.EXTRA_START_INSTALL_PARAM);
    }

    public void stopSearchChannel(boolean z) {
    }

    public void storeDishPosition(int i) {
    }

    public DtvSetupImpl(SourceLoader sourceLoader) {
        this.callbackList = new HashSet<>();
        this.disConnectedCallbackList = new HashSet<>();
        this.scanEventListenerList = new HashSet<>();
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public /* synthetic */ DtvSetupImpl(SourceLoader sourceLoader, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this((i & 1) != 0 ? null : sourceLoader);
    }

    public void destroy() {
        this.callbackList.clear();
    }

    public void setOnServiceConnectedCallback(OnServiceConnectedCallback onServiceConnectedCallback) {
        Intrinsics.checkNotNullParameter(onServiceConnectedCallback, "callback");
        this.callbackList.add(onServiceConnectedCallback);
    }

    public void removeOnServiceConnectedCallback(OnServiceConnectedCallback onServiceConnectedCallback) {
        Intrinsics.checkNotNullParameter(onServiceConnectedCallback, "callback");
        this.callbackList.remove(onServiceConnectedCallback);
    }

    public void setOnServiceDisConnectedCallback(OnServiceDisConnectedCallback onServiceDisConnectedCallback) {
        Intrinsics.checkNotNullParameter(onServiceDisConnectedCallback, "callback");
        this.disConnectedCallbackList.add(onServiceDisConnectedCallback);
    }

    public void removeOnServiceDisConnectedCallback(OnServiceDisConnectedCallback onServiceDisConnectedCallback) {
        Intrinsics.checkNotNullParameter(onServiceDisConnectedCallback, "callback");
        this.disConnectedCallbackList.remove(onServiceDisConnectedCallback);
    }

    public void registerScanEventListener(IScanEventListener iScanEventListener) {
        Intrinsics.checkNotNullParameter(iScanEventListener, "scanEventListener");
        this.scanEventListenerList.add(iScanEventListener);
    }

    public void unregisterScanEventListener(IScanEventListener iScanEventListener) {
        Intrinsics.checkNotNullParameter(iScanEventListener, "scanEventListener");
        this.scanEventListenerList.remove(iScanEventListener);
    }

    public void notifyScanEvent(String str, String str2) {
        Intrinsics.checkNotNullParameter(str, "key");
        Intrinsics.checkNotNullParameter(str2, "content");
        for (IScanEventListener notifyScanEvent : this.scanEventListenerList) {
            notifyScanEvent.notifyScanEvent(str, str2);
        }
    }

    public void notifyServiceConnected() {
        Iterator<OnServiceConnectedCallback> it = this.callbackList.iterator();
        Intrinsics.checkNotNullExpressionValue(it, "callbackList.iterator()");
        while (it.hasNext()) {
            OnServiceConnectedCallback next = it.next();
            Intrinsics.checkNotNullExpressionValue(next, "iterator.next()");
            next.onServiceConnected();
        }
    }

    public void notifyServiceDisConnected() {
        Iterator<OnServiceDisConnectedCallback> it = this.disConnectedCallbackList.iterator();
        Intrinsics.checkNotNullExpressionValue(it, "disConnectedCallbackList.iterator()");
        while (it.hasNext()) {
            OnServiceDisConnectedCallback next = it.next();
            Intrinsics.checkNotNullExpressionValue(next, "iterator.next()");
            next.onServiceDisConnected();
        }
    }
}
