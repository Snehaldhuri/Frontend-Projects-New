package com.diipl.moviebeam.dtv.dto;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.media.tv.TvContract;
import android.net.Uri;

import java.util.ArrayList;

import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

public class DBData {
    private Context mContext;

    public boolean delete(Uri uri, String[] strArr) {
        return delete$default(this, uri, (String) null, strArr, 2, (Object) null);
    }

    public final <T> ArrayList<T> queryAll(Uri uri, Function1<? super Cursor, ? extends T> function1) {
        return queryAll$default(this, uri, (String[]) null, (String) null, (String[]) null, (String) null, function1, 30, (Object) null);
    }

    public final <T> ArrayList<T> queryAll(Uri uri, String[] strArr, String str, Function1<? super Cursor, ? extends T> function1) {
        return queryAll$default(this, uri, strArr, str, (String[]) null, (String) null, function1, 24, (Object) null);
    }

    public final <T> ArrayList<T> queryAll(Uri uri, String[] strArr, String str, String[] strArr2, Function1<? super Cursor, ? extends T> function1) {
        return queryAll$default(this, uri, strArr, str, strArr2, (String) null, function1, 16, (Object) null);
    }

    public final <T> ArrayList<T> queryAll(Uri uri, String[] strArr, Function1<? super Cursor, ? extends T> function1) {
        return queryAll$default(this, uri, strArr, (String) null, (String[]) null, (String) null, function1, 28, (Object) null);
    }

    public final <T> Object queryElement(Uri uri, Function1<? super Cursor, ? extends T> function1) {
        return queryElement$default(this, uri, (String[]) null, (String) null, (String[]) null, (String) null, function1, 30, (Object) null);
    }

    public final <T> Object queryElement(Uri uri, String[] strArr, String str, Function1<? super Cursor, ? extends T> function1) {
        return queryElement$default(this, uri, strArr, str, (String[]) null, (String) null, function1, 24, (Object) null);
    }

    public final <T> Object queryElement(Uri uri, String[] strArr, String str, String[] strArr2, Function1<? super Cursor, ? extends T> function1) {
        return queryElement$default(this, uri, strArr, str, strArr2, (String) null, function1, 16, (Object) null);
    }

    public final <T> Object queryElement(Uri uri, String[] strArr, Function1<? super Cursor, ? extends T> function1) {
        return queryElement$default(this, uri, strArr, (String) null, (String[]) null, (String) null, function1, 28, (Object) null);
    }

    public final <T> Object queryOne(Uri uri, Function1<? super Cursor, ? extends T> function1) {
        return queryOne$default(this, uri, (String[]) null, (String) null, (String[]) null, (String) null, function1, 30, (Object) null);
    }

    public final <T> Object queryOne(Uri uri, String[] strArr, String str, Function1<? super Cursor, ? extends T> function1) {
        return queryOne$default(this, uri, strArr, str, (String[]) null, (String) null, function1, 24, (Object) null);
    }

    public final <T> Object queryOne(Uri uri, String[] strArr, String str, String[] strArr2, Function1<? super Cursor, ? extends T> function1) {
        return  queryOne$default(this, uri, strArr, str, strArr2, (String) null, function1, 16, (Object) null);
    }

    public final <T> Object queryOne(Uri uri, String[] strArr, Function1<? super Cursor, ? extends T> function1) {
        return queryOne$default(this, uri, strArr, (String) null, (String[]) null, (String) null, function1, 28, (Object) null);
    }

    public final boolean update(Uri uri, ContentValues contentValues) {
        return update$default(this, uri, contentValues, (String) null, (String[]) null, 12, (Object) null);
    }

    public final boolean update(Uri uri, ContentValues contentValues, String str) {
        return update$default(this, uri, contentValues, str, (String[]) null, 8, (Object) null);
    }

    public DBData(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        this.mContext = context;
    }

    public final Context getMContext() {
        return this.mContext;
    }

    public final void setMContext(Context context) {
        Intrinsics.checkNotNullParameter(context, "<set-?>");
        this.mContext = context;
    }

    public static /* synthetic */ ArrayList queryAll$default(DBData dBData, Uri uri, String[] strArr, String str, String[] strArr2, String str2, Function1 function1, int i, Object obj) {
        return dBData.queryAll(uri, (i & 2) != 0 ? null : strArr, (i & 4) != 0 ? null : str, (i & 8) != 0 ? null : strArr2, (i & 16) != 0 ? null : str2, function1);
    }

    public final <T> ArrayList<T> queryAll(Uri uri, String[] strArr, String str, String[] strArr2, String str2, Function1<? super Cursor, ? extends T> function1) {
        Intrinsics.checkNotNullParameter(uri, "uri");
        Intrinsics.checkNotNullParameter(function1, "block");
        ArrayList<T> arrayList = new ArrayList<>();
        Cursor query = this.mContext.getContentResolver().query(uri, strArr, str, strArr2, str2);
        if (query != null) {
            while (query.moveToNext()) {
                arrayList.add(function1.invoke(query));
            }
            query.close();
        }
        return arrayList;
    }

    public static /* synthetic */ Object queryElement$default(DBData dBData, Uri uri, String[] strArr, String str, String[] strArr2, String str2, Function1 function1, int i, Object obj) {
        return dBData.queryElement(uri, (i & 2) != 0 ? null : strArr, (i & 4) != 0 ? null : str, (i & 8) != 0 ? null : strArr2, (i & 16) != 0 ? null : str2, function1);
    }

    public final <T> Object queryElement(Uri uri, String[] strArr, String str, String[] strArr2, String str2, Function1<? super Cursor, ? extends T> function1) {
        Intrinsics.checkNotNullParameter(uri, "uri");
        Intrinsics.checkNotNullParameter(function1, "block");
        Cursor query = this.mContext.getContentResolver().query(uri, strArr, str, strArr2, str2);
        T t = null;
        if (query != null) {
            if (query.moveToFirst()) {
                t = function1.invoke(query);
            }
            query.close();
        }
        return t;
    }

    public static /* synthetic */ Object queryOne$default(DBData dBData, Uri uri, String[] strArr, String str, String[] strArr2, String str2, Function1 function1, int i, Object obj) {
        return dBData.queryOne(uri, (i & 2) != 0 ? null : strArr, (i & 4) != 0 ? null : str, (i & 8) != 0 ? null : strArr2, (i & 16) != 0 ? null : str2, function1);
    }

    public final <T> Object queryOne(Uri uri, String[] strArr, String str, String[] strArr2, String str2, Function1<? super Cursor, ? extends T> function1) {
        Intrinsics.checkNotNullParameter(uri, "uri");
        Intrinsics.checkNotNullParameter(function1, "block");
        Cursor query = this.mContext.getContentResolver().query(uri, strArr, str, strArr2, str2);
        T t = null;
        if (query != null) {
            if (query.moveToFirst()) {
                t = function1.invoke(query);
            }
            query.close();
        }
        return t;
    }

    public final Uri insert(Uri uri, ContentValues contentValues) {
        Intrinsics.checkNotNullParameter(uri, "uri");
        Intrinsics.checkNotNullParameter(contentValues, "contentValues");
        return this.mContext.getContentResolver().insert(uri, contentValues);
    }

    public static /* synthetic */ boolean delete$default(DBData dBData, Uri uri, String str, String[] strArr, int i, Object obj) {
        if ((i & 2) != 0) {
            str = null;
        }
        return dBData.delete(uri, str, strArr);
    }

    public final boolean delete(Uri uri, String str, String[] strArr) {
        Intrinsics.checkNotNullParameter(uri, "uri");
        int delete = this.mContext.getContentResolver().delete(uri, str, strArr);
        return (delete == 0 || delete == -1) ? false : true;
    }

    public static /* synthetic */ boolean update$default(DBData dBData, Uri uri, ContentValues contentValues, String str, String[] strArr, int i, Object obj) {
        if ((i & 4) != 0) {
            str = null;
        }
        if ((i & 8) != 0) {
            strArr = null;
        }
        return dBData.update(uri, contentValues, str, strArr);
    }

    public final boolean update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        Intrinsics.checkNotNullParameter(uri, "uri");
        Intrinsics.checkNotNullParameter(contentValues, "contentValues");
        int update = this.mContext.getContentResolver().update(uri, contentValues, str, strArr);
        return (update == 0 || update == -1) ? false : true;
    }

    public final boolean updateSingleChannelColumn(long j, String str, Object obj) {
        Intrinsics.checkNotNullParameter(str, "columnKey");
        Intrinsics.checkNotNullParameter(obj, "value");
        if (j != -1) {
            if (!(str.length() == 0)) {
                try {
                    Uri buildChannelUri = TvContract.buildChannelUri(j);
                    ContentValues contentValues = new ContentValues();
                    if (obj instanceof String) {
                        contentValues.put(str, (String) obj);
                    } else if (obj instanceof Integer) {
                        contentValues.put(str, (Integer) obj);
                    }
                    Intrinsics.checkNotNullExpressionValue(buildChannelUri, "uri");
                    update$default(this, buildChannelUri, contentValues, (String) null, (String[]) null, 12, (Object) null);
                    return true;
                } catch (Exception unused) {
                }
            }
        }
        return false;
    }
}
