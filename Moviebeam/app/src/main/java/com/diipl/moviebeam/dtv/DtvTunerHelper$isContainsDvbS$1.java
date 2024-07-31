package com.diipl.moviebeam.dtv;

import android.database.Cursor;
import android.util.Log;

import java.util.Arrays;
import java.util.Locale;

import kotlin.Metadata;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

@Metadata(bv = {1, 0, 3}, d1 = {"\u0000\f\n\u0000\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0000\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n¢\u0006\u0002\b\u0003"}, d2 = {"<anonymous>", "", "Landroid/database/Cursor;", "invoke"}, k = 3, mv = {1, 4, 2})
/* compiled from: DtvTunerHelper.kt */
class DtvTunerHelper$isContainsDvbS$1 implements Function1<Cursor, String> {
    final /* synthetic */ DtvTunerHelper this$0;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    DtvTunerHelper$isContainsDvbS$1(DtvTunerHelper dtvTunerHelper) {
        this.this$0 = dtvTunerHelper;
    }

    public String invoke(Cursor cursor) {
        Intrinsics.checkNotNullParameter(cursor, "$receiver");
        String access$getTAG$p = this.this$0.TAG;
        String format = String.format(Locale.US, "Streamer %d, name:%s, supported_types:%s", Arrays.copyOf(new Object[]{Integer.valueOf(cursor.getInt(0)), cursor.getString(1), cursor.getString(2)}, 3));
        Intrinsics.checkNotNullExpressionValue(format, "java.lang.String.format(locale, format, *args)");
        Log.i(access$getTAG$p, format);
        String string = cursor.getString(2);
        Intrinsics.checkNotNullExpressionValue(string, "getString(2)");
        return string;
    }
}
