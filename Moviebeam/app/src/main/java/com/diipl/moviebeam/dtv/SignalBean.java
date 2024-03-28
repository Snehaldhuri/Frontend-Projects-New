package com.diipl.moviebeam.dtv;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

public class SignalBean {
    private int mDb;
    private int mDbm;
    private int mQuality;
    private int mStrength;

    public final int getStrength() {
        return this.mStrength;
    }

    public final void setStrength(int i) {
        this.mStrength = i;
    }

    public final int getQuality() {
        return this.mQuality;
    }

    public final void setQuality(int i) {
        this.mQuality = i;
    }

    public final int getDbm() {
        return this.mDbm;
    }

    public final void setDbm(int i) {
        this.mDbm = i;
    }

    public final int getDb() {
        return this.mDb;
    }

    public final void setDb(int i) {
        this.mDb = i;
    }

    public final void parseSignal(String str) {
        int i;
        try {
            if (!TextUtils.isEmpty(str)) {
                JSONObject jSONObject = new JSONObject(str);
                String optString = jSONObject.optString("snr");
                String optString2 = jSONObject.optString("rssi");
                int i2 = 0;
                if (TextUtils.isEmpty(optString)) {
                    i = 0;
                } else {
                    Intrinsics.checkNotNullExpressionValue(optString, "snr");
                    i = Integer.parseInt(optString);
                }
                this.mQuality = i;
                if (!TextUtils.isEmpty(optString2)) {
                    Intrinsics.checkNotNullExpressionValue(optString2, "rssi");
                    i2 = Integer.parseInt(optString2);
                }
                this.mStrength = i2;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public final void parseDtvKitSignal(String str) {
        CharSequence charSequence = str;
        if (!(charSequence == null || charSequence.length() == 0)) {
            try {
                JSONArray jSONArray = new JSONArray(str);
                setStrength(jSONArray.optJSONObject(0).optInt("value"));
                setQuality(jSONArray.optJSONObject(1).optInt("value"));
                setDb(jSONArray.optJSONObject(2).optInt("value"));
                setDbm(jSONArray.optJSONObject(3).optInt("value"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public String toString() {
        return "SignalBean(mStrength=" + this.mStrength + ", mQuality=" + this.mQuality + ", mDbm=" + this.mDbm + ", mDb=" + this.mDb + ')';
    }
}
