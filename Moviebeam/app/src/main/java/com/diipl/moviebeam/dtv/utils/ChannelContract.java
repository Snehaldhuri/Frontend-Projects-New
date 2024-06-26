package com.diipl.moviebeam.dtv.utils;

import static com.diipl.moviebeam.dtv.DvbContract.AUTHORITY;

import android.content.ContentUris;
import android.net.Uri;



public class ChannelContract {

    public static final class RecommendChannel {
        public static final String COLUMN_CHANNEL_ID = "channel_id";
        public static final String COLUMN_CHANNEL_NAME = "channel_name";
        public static final String COLUMN_CHANNEL_TYPE = "channel_type";
        public static final String COLUMN_DISPLAY_NUMBER = "display_number";
        public static final String COLUMN_INTEGER_INTERNAL_FLAG1 = "integer_internal_flag1";
        public static final String COLUMN_INTEGER_INTERNAL_FLAG2 = "integer_internal_flag2";
        public static final String COLUMN_INTEGER_INTERNAL_FLAG3 = "integer_internal_flag3";
        public static final String COLUMN_IS_FAVORITE = "is_favorite";
        public static final String COLUMN_NETWORK_ID = "network_id";
        public static final String COLUMN_SERVICE_ID = "service_id";
        public static final String COLUMN_SERVICE_TYPE = "service_type";
        public static final String COLUMN_TEXT_INTERNAL_FLAG1 = "text_internal_flag1";
        public static final String COLUMN_TEXT_INTERNAL_FLAG2 = "text_internal_flag2";
        public static final String COLUMN_TEXT_INTERNAL_FLAG3 = "text_internal_flag3";
        public static final String COLUMN_TIMESTAMP = "timestamp";
        public static final String COLUMN_TS_ID = "ts_id";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_WATCH_DURATION = "watch_duration";
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" +"recommend_channel");
        public static final String PATH = "recommend_channel";
        public static final String TABLE_NAME = "recommend_channel_table";

        public static Uri buildRecommendChannelUri(long j) {
            return ContentUris.withAppendedId(CONTENT_URI, j);
        }
    }
}
