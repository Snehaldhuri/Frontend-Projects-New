package com.diipl.moviebeam.dtv;

import android.database.Cursor;

import kotlin.Metadata;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

@Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u000e\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\u0010\u0000\u001a\n \u0002*\u0004\u0018\u00010\u00010\u0001*\u00020\u0003H\n¢\u0006\u0002\b\u0004"}, d2 = {"<anonymous>", "", "kotlin.jvm.PlatformType", "Landroid/database/Cursor;", "invoke"}, k = 3, mv = {1, 4, 2})
/* compiled from: DtvKitSetup.kt */
final class DtvKitSetup$getLangNameList$languageNameJson$1 implements Function1<Cursor, String> {
    public static final DtvKitSetup$getLangNameList$languageNameJson$1 INSTANCE = new DtvKitSetup$getLangNameList$languageNameJson$1();

    DtvKitSetup$getLangNameList$languageNameJson$1() {
        super();
    }

    public final String invoke(Cursor cursor) {
        Intrinsics.checkNotNullParameter(cursor, "$receiver");
        return cursor.getString(cursor.getColumnIndex("value"));
    }
}
