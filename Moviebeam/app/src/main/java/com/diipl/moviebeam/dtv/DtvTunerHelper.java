package com.diipl.moviebeam.dtv;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.tv.TvInputInfo;
import android.media.tv.TvInputManager;
import android.net.Uri;

import com.diipl.moviebeam.R;
import com.diipl.moviebeam.dtv.utils.Utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.TypeIntrinsics;

public class DtvTunerHelper {
    private static final String CBS_INPUT = "com.google.android.tv.dtvinput";
    private static final String DTV_KIT = "org.dtvkit.inputsource";
    /* access modifiers changed from: private */
    public final String TAG = "DtvTunerHelper";

    private static final class CBS {
        public static final String AUTHORITY = "com.google.android.tv.dtvprovider";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_STREAMER_ID = "id";
        public static final String COLUMN_SUPPORTED_TYPES = "supported_types";
        /* access modifiers changed from: private */
        public static final Uri CONTENT_URI;
        public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
        public static final String PATH = "streamers";
        /* access modifiers changed from: private */
        public static final String[] PROJECTION = {"id", "name", "supported_types"};


        public static final class Companion {
            private Companion() {
            }

            public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
                this();
            }

            public final Uri getCONTENT_URI() {
                return CBS.CONTENT_URI;
            }

            public final String[] getPROJECTION() {
                return CBS.PROJECTION;
            }
        }

        static {
            Uri parse = Uri.parse("content://com.google.android.tv.dtvprovider/streamers");
            Intrinsics.checkNotNullExpressionValue(parse, "Uri.parse(\"content://$AUTHORITY/$PATH\")");
            CONTENT_URI = parse;
        }
    }

    private static final class DtvKit {
        public static final String AUTHORITY = "com.nes.dtvkit.tvsetting";
        public static final String COLUMN_KEY = "key";
        public static final String COLUMN_VALUE = "value";
        /* access modifiers changed from: private */
        public static final Uri CONTENT_URI = Uri.parse("content://com.nes.dtvkit.tvsetting/tvsettings_table");
        public static final String KEY_TUNER_TYPE = "support_tuner_type";
        public static final String PATH = "tvsettings_table";
        /* access modifiers changed from: private */
        public static final String[] PROJECTION = {"key", "value"};

        public static Uri getCONTENT_URI() {
            return DtvKit.CONTENT_URI;
        }

        public final String[] getPROJECTION() {
            return DtvKit.PROJECTION;
        }
    }

    public final boolean isContainsDvbS(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        if (isCBSInput(context)) {
            return isDoubleTunerWithSatellite$default(this, context, CBS.Companion.getCONTENT_URI(), CBS.Companion.getPROJECTION(), (String) null, (String[]) null, (String) null, new DtvTunerHelper$isContainsDvbS$1(this), 56, (Object) null);
        } else if (!isDtvKitInput(context)) {
            return false;
        } else {
            Uri content_uri = DtvKit.getCONTENT_URI();
            Intrinsics.checkNotNullExpressionValue(content_uri, "DtvKit.CONTENT_URI");
            return isDoubleTunerWithSatellite$default(this, context, content_uri, (String[]) null, "key=?", new String[]{"support_tuner_type"}, (String) null, new DtvTunerHelper$isContainsDvbS$2(this), 32, (Object) null);
        }
    }

    static boolean isDoubleTunerWithSatellite$default(DtvTunerHelper dtvTunerHelper, Context context, Uri uri, String[] strArr, String str, String[] strArr2, String str2, Function1 function1, int i, Object obj) {
        return dtvTunerHelper.isDoubleTunerWithSatellite(context, uri, (i & 4) != 0 ? null : strArr, (i & 8) != 0 ? null : str, (i & 16) != 0 ? null : strArr2, (i & 32) != 0 ? null : str2, function1);
    }

    private boolean isDoubleTunerWithSatellite(Context context, Uri uri, String[] strArr, String str, String[] strArr2, String str2, Function1<? super Cursor, String> function1) {
        boolean z1, z2, z3, z4;
        ContentResolver contentResolver = context.getContentResolver();
        Cursor query = contentResolver.query(uri, strArr, str, strArr2, str2);
        if (query == null || !query.moveToFirst()) {
            z4 = false;
            z3 = false;
            z2 = false;
            z1 = false;
        } else {
            do {
                CharSequence invoke = function1.invoke(query);
                z4 = invoke.toString().contains("CABLE");
                z3 = invoke.toString().contains("TERRESTRIAL");
                z2 = invoke.toString().contains("ISDBT");
                z1 = invoke.toString().contains("SATELLITE");
            } while (query.moveToNext());
            query.close();
        }
        return (z3 || z4 || z2) && z1;
    }

    public boolean isCBSInput(Context context) {
        Object systemService = context.getSystemService(Context.TV_INPUT_SERVICE);
        if (systemService != null) {
            List<TvInputInfo> tvInputList = ((TvInputManager) systemService).getTvInputList();
            for (TvInputInfo tvInputInfo : tvInputList) {
                String id = tvInputInfo.getId();
                if (id.startsWith(CBS_INPUT))
                    return true;
            }
            return false;
        }
        throw new NullPointerException("null cannot be cast to non-null type android.media.tv.TvInputManager");
    }

    public boolean isDtvKitInput(Context context) {
        Object systemService = context.getSystemService(Context.TV_INPUT_SERVICE);
        if (systemService != null) {
            List<TvInputInfo> tvInputList = ((TvInputManager) systemService).getTvInputList();
            Intrinsics.checkNotNullExpressionValue(tvInputList, "inputManager.tvInputList");
            for (TvInputInfo tvInputInfo : tvInputList) {
                Intrinsics.checkNotNullExpressionValue(tvInputInfo, "it");
                String id = tvInputInfo.getId();
                Intrinsics.checkNotNullExpressionValue(id, "it.id");
                if (id.startsWith(DTV_KIT)) return true;

            }
            return false;
        }
        throw new NullPointerException("null cannot be cast to non-null type android.media.tv.TvInputManager");
    }

    public @Nullable SetupParamBean createSetupParams(@NotNull Context mContext, @Nullable String str) {
        IDtvSetup mDtvSetup = new DtvKitSetup(mContext);
        SetupParamBean setupParamBean = new SetupParamBean();
        IDtvSetup iDtvSetup = mDtvSetup;
        List<FrequencyBean> frequencyList = iDtvSetup.getFrequencyList();
        if (frequencyList == null) {
            frequencyList = new ArrayList<>();
        }
        if (Intrinsics.areEqual(str, DvbContract.Tuner.DVB_C)) {
            IDtvSetup iDtvSetup2 = mDtvSetup;
            int intParameter = iDtvSetup2.getIntParameter(DvbConstant.TV_KEY_AUTO_SYMBOL_SWITCH);
            String[] array = Utils.getArray(mContext, R.array.symbol_switch);
            IDtvSetup iDtvSetup3 = mDtvSetup;
            int intParameter2 = iDtvSetup3.getIntParameter(DvbConstant.TV_KEY_AUTO_SYMBOL_VALUE);
            if (intParameter != array.length) {
                Object obj = array[intParameter];
                intParameter2 = Integer.parseInt((String) obj);
            } else if (intParameter2 <= 0) {
                intParameter2 = 6900;
            }
            for (FrequencyBean symbolRate : frequencyList) {
                symbolRate.setSymbolRate(intParameter2);
            }
        }
        setupParamBean.frequencyList = frequencyList;
        setupParamBean.searchMode = 1;
        setupParamBean.tunerType = str;
        return setupParamBean;
    }

}
