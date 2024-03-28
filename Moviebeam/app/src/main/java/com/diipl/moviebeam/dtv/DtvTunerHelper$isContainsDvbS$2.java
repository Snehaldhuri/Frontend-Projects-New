package com.diipl.moviebeam.dtv;

import android.database.Cursor;
import android.util.Log;

import java.util.Arrays;
import java.util.Locale;

import kotlin.Metadata;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

/* compiled from: DtvTunerHelper.kt */
class DtvTunerHelper$isContainsDvbS$2 implements Function1<Cursor, String> {
    final /* synthetic */ DtvTunerHelper this$0;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    DtvTunerHelper$isContainsDvbS$2(DtvTunerHelper dtvTunerHelper) {
        this.this$0 = dtvTunerHelper;
    }

    public final String invoke(Cursor cursor) {
        Intrinsics.checkNotNullParameter(cursor, "$receiver");
        String access$getTAG$p = this.this$0.TAG;
        String format = String.format(Locale.US, "supported_types:%s", Arrays.copyOf(new Object[]{cursor.getString(1)}, 1));
        Intrinsics.checkNotNullExpressionValue(format, "java.lang.String.format(locale, format, *args)");
        Log.i(access$getTAG$p, format);
        String string = cursor.getString(1);
        Intrinsics.checkNotNullExpressionValue(string, "getString(1)");
        return string;
    }
}
