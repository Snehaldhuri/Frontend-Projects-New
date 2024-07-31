package com.diipl.moviebeam.dtv;

import android.content.ContentUris;
import android.media.tv.TvContract;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;


import org.json.JSONException;
import org.json.JSONObject;

public final class DvbContract {
    public static String AUTHORITY = "com.nes.dtv.setup.DvbProvider";
    public static final String DTV_KIT_AUTHORITY = "org.dtvkit.inputsource.data.DvbProvider";
    public static final String KEY_DISEQC1_2_DISH_LIMITS_STATUS = "key_dish_limit_status";
    public static final String KEY_DISEQC1_2_DISH_MOVE_DIRECTION = "key_dish_move_direction";
    public static final String KEY_DISEQC1_2_DISH_MOVE_STEP = "key_dish_move_step";
    public static final int LNB_TYPE_LNB_TYPE_OCS = 4;
    public static final int LNB_TYPE_SINGLE = 0;
    public static final int LNB_TYPE_UNICABLE = 2;
    public static final int LNB_TYPE_UNIVERSAL = 1;
    public static final int LNB_TYPE_USER_DEFIINED = 3;
    public static final String PATH_SATELLITE = "satellite";
    public static final String PATH_TRANSPONDER = "transponder";
    public static final int SCAN_DVBS = 768;
    public static final String SELECTION_FIELD_ID = "db_id=?";
    public static final String SELECTION_FIELD_PARENT_ID = "db_sat_para_id=?";
    public static final String SELECTION_FIELD_SAT_LONGITUDE = "sat_longitude=?";

    public static final class Transponder implements TvContract.BaseTvColumns {
        public static final String BACKUP_INT_FIVE = "int_five";
        public static final String BACKUP_INT_FOUR = "int_four";
        public static final String BACKUP_INT_ONE = "int_one";
        public static final String BACKUP_INT_THREE = "int_three";
        public static final String BACKUP_INT_TWO = "int_two";
        public static final String BACKUP_STRING_FIVE = "string_five";
        public static final String BACKUP_STRING_FOUR = "string_four";
        public static final String BACKUP_STRING_ONE = "string_one";
        public static final String BACKUP_STRING_THREE = "string_three";
        public static final String BACKUP_STRING_TWO = "string_two";
        public static final String BER = "ber";
        public static final String BW = "bw";
        public static final String CONTENT_ITEM_TYPE = ("vnd.android.cursor.item/" + DvbContract.AUTHORITY + "." + "transponder");
        public static final String CONTENT_TYPE = ("vnd.android.cursor.dir/" + DvbContract.AUTHORITY + "." + "transponder");
        public static final Uri CONTENT_URI = Uri.parse("content://" + DvbContract.AUTHORITY + "/" + "transponder");
        public static final String DB_ID = "db_id";
        public static final Uri DTV_KIT_CONTENT_URI = Uri.parse("content://org.dtvkit.inputsource.data.DvbProvider/transponder");
        public static final String DVBSFLAG = "dvbs_flag";
        public static final String FEC = "fec";
        public static final String FREQ = "freq";
        public static final String MOD = "mod";
        public static final String NETWORK_FAVLIST_ID = "network_favlist_id";
        public static final String NETWORK_ID = "network_id";
        public static final String NETWORK_OPERATOR_SEARCH = "network_opeaator_search";
        public static final String NETWORK_OP_SEAECH_DATE = "network_op_search_date";
        public static final String NETWORK_OP_SEAECH_TIME = "network_op_search_time";
        public static final String NETWORK_ORIG_NET_ID = "network_orig_net_id";
        public static final String NETWORK_PROFILE_CAM_ID = "network_profile_cam_id";
        public static final String NETWORK_PROFILE_NAME = "network_profile_name";
        public static final String NETWORK_PROFILE_TYPE = "network_profile_type";
        public static final String NETWORK_REC_NAME = "network_rec_name";
        public static final String NETWORK_VERSION = "network_version";
        public static final String NET_ID = "db_net_id";
        public static final String PATH = "transponder";
        public static final String POLAR = "polar";
        public static final String SAT_PARA_ID = "db_sat_para_id";
        public static final String SDT_VERSION = "sdt_version";
        public static final String SNR = "snr";
        public static final String SRC = "src";
        public static final String STRENGTH = "strength";
        public static final String SYMB = "symb";
        public static final String TRANSPORT_ID = "transport_id";
        public static final String TRANSPORT_PLP_ID = "tran_plp_id";
        public static final String TRANSPORT_SIGNAL_LEVEL = "tran_signal_level";
        public static final String TRANSPORT_TERR_TYPE = "tran_terr_type";
        public static final String TS_ID = "ts_id";
        public static final String USEDFLAG = "used_flag";
        public static final int VERSION = 1;
    }

    public static String getContentType(String str) {
        return "vnd.android.cursor.dir/" + AUTHORITY + "." + str;
    }

    public static String getContentItemType(String str) {
        return "vnd.android.cursor.item/" + AUTHORITY + "." + str;
    }

    public static final class Satellite implements TvContract.BaseTvColumns {
        public static final String BACKUP_INT_FIVE = "int_five";
        public static final String BACKUP_INT_FOUR = "int_four";
        public static final String BACKUP_INT_ONE = "int_one";
        public static final String BACKUP_INT_THREE = "int_three";
        public static final String BACKUP_INT_TWO = "int_two";
        public static final String BACKUP_STRING_FIVE = "string_five";
        public static final String BACKUP_STRING_FOUR = "string_four";
        public static final String BACKUP_STRING_ONE = "string_one";
        public static final String BACKUP_STRING_THREE = "string_three";
        public static final String BACKUP_STRING_TWO = "string_two";
        public static final String CMD_ORDER = "cmd_order";
        public static final String COMMITTED_CMD = "committed_cmd";
        public static final String CONTENT_ITEM_TYPE = ("vnd.android.cursor.item/" + DvbContract.AUTHORITY + "." + "satellite");
        public static final String CONTENT_TYPE = ("vnd.android.cursor.dir/" + DvbContract.AUTHORITY + "." + "satellite");
        public static final Uri CONTENT_URI = Uri.parse("content://" + DvbContract.AUTHORITY + "/" + "satellite");
        public static final String CUSTOM_HIGH_LNB = "custom_high_lnb";
        public static final String CUSTOM_HIGH_LNB_MAX = "custom_high_lnb_max";
        public static final String CUSTOM_HIGH_LNB_MIN = "custom_high_lnb_min";
        public static final String CUSTOM_LOW_LNB = "custom_low_lnb";
        public static final String CUSTOM_LOW_LNB_MAX = "custom_low_lnb_max";
        public static final String CUSTOM_LOW_LNB_MIN = "custom_low_lnb_min";
        public static final String DB_ID = "db_id";
        public static final String DISEQC_MODE = "diseqc_mode";
        public static final Uri DTV_KIT_CONTENT_URI = Uri.parse("content://org.dtvkit.inputsource.data.DvbProvider/satellite");
        public static final String FAST_DISEQC = "fast_diseqc";
        public static final String LATITUDE = "latitude";
        public static final String LA_DIRECTION = "la_direction";
        public static final String LNB_NUMBER = "lnb_num";
        public static final String LNB_TYPE = "lnb_type";
        public static final String LOF_HI = "lof_hi";
        public static final String LOF_LO = "lof_lo";
        public static final String LOF_THRESHOLD = "lof_threshold";
        public static final String LONGITUDE = "longitude";
        public static final String LO_DIRECTION = "lo_direction";
        public static final String MOTOR_NUMBER = "motor_num";
        public static final String PATH = "satellite";
        public static final String POS_NUMBER = "pos_num";
        public static final String REPEAT_COUNT = "repeat_count";
        public static final String SAT_LONGITUDE = "sat_longitude";
        public static final String SAT_NAME = "sat_name";
        public static final String SELECTED = "selected";
        public static final String SEQUENCE_REPEAT = "sequence_repeat";
        public static final String SIGNAL_22KHZ = "signal_22khz";
        public static final String TONE_BURST = "tone_burst";
        public static final String UB_FREQUENCY = "ub_frequency";
        public static final String UNCOMMITTED_CMD = "uncommitted_cmd";
        public static final String UNICABLE_FORMAT = "unicable_format";
        public static final String UNICABLE_POSITION = "unicable_position";
        public static final String UNICABLE_SWITCH = "unicable_switch";
        public static final String USER_BAND = "user_band";
        public static final int VERSION = 1;
        public static final String VOLTAGE = "voltage";

        public static Uri buildSatelliteChannelUri(long j) {
            return ContentUris.withAppendedId(CONTENT_URI, j);
        }
    }

    public static final class Tuner {
        public static final String DVB_C = "DVB-C";
        public static final String DVB_S = "DVB-S";
        public static final String DVB_S2 = "DVB-S2";
        public static final String DVB_S_C = "DVB-SC";
        public static final String DVB_S_I = "DVB-SI";
        public static final String DVB_S_T = "DVB-ST";
        public static final String DVB_S_T_C = "DVB-STC";
        public static final String DVB_S_T_C_I = "DVB-STCI";
        public static final String DVB_S_T_I = "DVB-STI";
        public static final String DVB_T = "DVB-T";
        public static final String DVB_T2 = "DVB-T2";
        public static final String DVB_T_C = "DVB-TC";
        public static final String DVB_T_C_I = "DVB-TCI";
        public static final String DVB_T_I = "DVB-TI";
        public static final String DVB_T_T2 = "DVB-T+T2";
        public static final String ISDB_T = "ISDB-T";

        private Tuner() {
        }

        /* JADX WARNING: Removed duplicated region for block: B:17:0x0038 A[ADDED_TO_REGION] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public static boolean IsDVB_TType(String r5) {
            /*
                int r0 = r5.hashCode()
                r1 = -798447438(0xffffffffd068a8b2, float:-1.56134789E10)
                r2 = 0
                r3 = 2
                r4 = 1
                if (r0 == r1) goto L_0x002b
                r1 = 65426359(0x3e653b7, float:1.3537405E-36)
                if (r0 == r1) goto L_0x0021
                r1 = 2028217179(0x78e4235b, float:3.7017524E34)
                if (r0 == r1) goto L_0x0017
                goto L_0x0035
            L_0x0017:
                java.lang.String r0 = "DVB-T2"
                boolean r5 = r5.equals(r0)
                if (r5 == 0) goto L_0x0035
                r5 = r4
                goto L_0x0036
            L_0x0021:
                java.lang.String r0 = "DVB-T"
                boolean r5 = r5.equals(r0)
                if (r5 == 0) goto L_0x0035
                r5 = r2
                goto L_0x0036
            L_0x002b:
                java.lang.String r0 = "DVB-T+T2"
                boolean r5 = r5.equals(r0)
                if (r5 == 0) goto L_0x0035
                r5 = r3
                goto L_0x0036
            L_0x0035:
                r5 = -1
            L_0x0036:
                if (r5 == 0) goto L_0x003d
                if (r5 == r4) goto L_0x003d
                if (r5 == r3) goto L_0x003d
                return r2
            L_0x003d:
                return r4
            */
            throw new UnsupportedOperationException("Method not decompiled: com.commonsetup.data.DvbContract.Tuner.IsDVB_TType(java.lang.String):boolean");
        }

        /* JADX WARNING: Removed duplicated region for block: B:12:0x0028 A[ADDED_TO_REGION] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public static boolean isDVB_S(String r4) {
            /*
                int r0 = r4.hashCode()
                r1 = 65426358(0x3e653b6, float:1.3537404E-36)
                r2 = 0
                r3 = 1
                if (r0 == r1) goto L_0x001b
                r1 = 2028217148(0x78e4233c, float:3.7017448E34)
                if (r0 == r1) goto L_0x0011
                goto L_0x0025
            L_0x0011:
                java.lang.String r0 = "DVB-S2"
                boolean r4 = r4.equals(r0)
                if (r4 == 0) goto L_0x0025
                r4 = r3
                goto L_0x0026
            L_0x001b:
                java.lang.String r0 = "DVB-S"
                boolean r4 = r4.equals(r0)
                if (r4 == 0) goto L_0x0025
                r4 = r2
                goto L_0x0026
            L_0x0025:
                r4 = -1
            L_0x0026:
                if (r4 == 0) goto L_0x002b
                if (r4 == r3) goto L_0x002b
                return r2
            L_0x002b:
                return r3
            */
            throw new UnsupportedOperationException("Method not decompiled: com.commonsetup.data.DvbContract.Tuner.isDVB_S(java.lang.String):boolean");
        }

        /* JADX WARNING: Can't fix incorrect switch cases order */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public static boolean isSingleTuner(String r3) {
            /*
                int r0 = r3.hashCode()
                r1 = 0
                r2 = 1
                switch(r0) {
                    case -2126296337: goto L_0x0046;
                    case -798447438: goto L_0x003c;
                    case 65426342: goto L_0x0032;
                    case 65426358: goto L_0x0028;
                    case 65426359: goto L_0x001e;
                    case 2028217148: goto L_0x0014;
                    case 2028217179: goto L_0x000a;
                    default: goto L_0x0009;
                }
            L_0x0009:
                goto L_0x0050
            L_0x000a:
                java.lang.String r0 = "DVB-T2"
                boolean r3 = r3.equals(r0)
                if (r3 == 0) goto L_0x0050
                r3 = 2
                goto L_0x0051
            L_0x0014:
                java.lang.String r0 = "DVB-S2"
                boolean r3 = r3.equals(r0)
                if (r3 == 0) goto L_0x0050
                r3 = 5
                goto L_0x0051
            L_0x001e:
                java.lang.String r0 = "DVB-T"
                boolean r3 = r3.equals(r0)
                if (r3 == 0) goto L_0x0050
                r3 = r2
                goto L_0x0051
            L_0x0028:
                java.lang.String r0 = "DVB-S"
                boolean r3 = r3.equals(r0)
                if (r3 == 0) goto L_0x0050
                r3 = 4
                goto L_0x0051
            L_0x0032:
                java.lang.String r0 = "DVB-C"
                boolean r3 = r3.equals(r0)
                if (r3 == 0) goto L_0x0050
                r3 = r1
                goto L_0x0051
            L_0x003c:
                java.lang.String r0 = "DVB-T+T2"
                boolean r3 = r3.equals(r0)
                if (r3 == 0) goto L_0x0050
                r3 = 3
                goto L_0x0051
            L_0x0046:
                java.lang.String r0 = "ISDB-T"
                boolean r3 = r3.equals(r0)
                if (r3 == 0) goto L_0x0050
                r3 = 6
                goto L_0x0051
            L_0x0050:
                r3 = -1
            L_0x0051:
                switch(r3) {
                    case 0: goto L_0x0055;
                    case 1: goto L_0x0055;
                    case 2: goto L_0x0055;
                    case 3: goto L_0x0055;
                    case 4: goto L_0x0055;
                    case 5: goto L_0x0055;
                    case 6: goto L_0x0055;
                    default: goto L_0x0054;
                }
            L_0x0054:
                return r1
            L_0x0055:
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: com.commonsetup.data.DvbContract.Tuner.isSingleTuner(java.lang.String):boolean");
        }
    }

    public static class MODULATION {
        public static final String[] MODULATION_ARRAYS = {"qam_auto", "qam_4", "qam_8", "qam_16", "qam_32", "qam_64", "qam_128", "qam_256"};

        private MODULATION() {
        }
    }

    public static class BANDWIDTH {
        public static final int[] BANDWIDTH_ARRAYS = {8, 7, 6, 5, 10};

        private BANDWIDTH() {
        }
    }

    public static final class Streamers implements BaseColumns {
        public static final String AUTHORITY = "com.google.android.tv.dtvprovider";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PARAMETERS = "parameters";
        public static final String COLUMN_SIGNAL = "signal";
        public static final String COLUMN_STATE = "state";
        public static final String COLUMN_STATS = "stats";
        public static final String COLUMN_STREAMER_ID = "id";
        public static final String COLUMN_SUPPORTED_TYPES = "supported_types";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_URI = "uri";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.google.android.tv.dtvprovider.streamers";
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/com.google.android.tv.dtvprovider.streamers";
        public static final Uri CONTENT_URI = Uri.parse("content://com.google.android.tv.dtvprovider/streamers");
        public static final String FRONTEND_STATE_FILTERING = "FILTERING";
        public static final String FRONTEND_STATE_IDLE = "IDLE";
        public static final String FRONTEND_STATE_STREAMING = "STREAMING";
        public static final String FRONTEND_TYPE_CABLE = "CABLE";
        public static final String FRONTEND_TYPE_IP = "IP";
        public static final String FRONTEND_TYPE_ISDBT = "ISDBT";
        public static final String FRONTEND_TYPE_SATELLITE = "SATELLITE";
        public static final String FRONTEND_TYPE_TERRESTRIAL = "TERRESTRIAL";
        public static final String FRONTEND_TYPE_TERRESTRIAL_2 = "TERRESTRIAL_2";
        public static final String FRONTEND_TYPE_UNKNOWN = "UNKNOWN";
        public static final String PARAMETER_BANDWIDTH = "bandwidth";
        public static final String PARAMETER_FREQUENCY = "frequency";
        public static final String PARAMETER_IP_ADDRESS = "ip-address";
        public static final String PARAMETER_IP_PORT = "ip-port";
        public static final String PARAMETER_MODULATION = "modulation";
        public static final String PARAMETER_SYMBOL_RATE = "symbol-rate";
        public static final String PATH = "streamers";
        public static final String STAT_BIT_ERROR_RATE = "ber";
        public static final String STAT_NB_LOST_PACKETS = "lost-packets";
        public static final String STAT_NB_RECEIVED_PACKETS = "received-packets";
        public static final String STAT_NB_RECOVERED_PACKETS = "recovered-packets";
        public static final String STAT_NB_REORDERED_PACKETS = "reordered-packets";
        public static final String STAT_NB_REQUESTED_PACKETS = "requested-packets";
        public static final String STAT_NETWORK_JITTER = "network-jitter";
        public static final String STAT_RTP_PACKETS = "rtp-packets";
        public static final String STAT_SIGNAL_NOISE_RATIO = "snr";
        public static final String STAT_SIGNAL_STRENGTH = "rssi";

        private Streamers() {
        }

        public static double getStatDouble(String str, String str2) {
            return ((Double) getValueFromJson(str, str2, Double.class)).doubleValue();
        }

        public static int getStatInt(String str, String str2) {
            return ((Integer) getValueFromJson(str, str2, Integer.class)).intValue();
        }

        public static String getStatString(String str, String str2) {
            return (String) getValueFromJson(str, str2, String.class);
        }

        public static boolean getStatBoolean(String str, String str2) {
            try {
                return ((Boolean) getValueFromJson(str, str2, Boolean.class)).booleanValue();
            } catch (Exception unused) {
                return false;
            }
        }

        public static int getParamInt(String str, String str2) {
            return ((Integer) getValueFromJson(str, str2, Integer.class)).intValue();
        }

        public static String getParamString(String str, String str2) {
            return (String) getValueFromJson(str, str2, String.class);
        }

        static Object  getValueFromJson(String str, String str2, Class cls) {
            if (str == null) {
                return null;
            }
            try {
                JSONObject jSONObject = new JSONObject(str);
                if (!jSONObject.has(str2)) {
                    return null;
                }
                if (cls == String.class) {
                    return jSONObject.getString(str2);
                }
                if (cls == Boolean.class) {
                    return Boolean.valueOf(jSONObject.getBoolean(str2));
                }
                if (cls == Integer.class) {
                    return Integer.valueOf(jSONObject.getInt(str2));
                }
                if (cls == Double.class) {
                    return Double.valueOf(jSONObject.getDouble(str2));
                }
                return null;
            } catch (JSONException e) {
                Log.w("TAG", String.format("can't parse %s error:%s", new Object[]{str, e.getMessage()}));
            }
            return null;
        }
    }

    public static final class TvSettings implements BaseColumns {
        public static final String AUTHORITY = "com.nes.dtvkit.tvsetting";
        public static final String COLUMN_KEY = "key";
        public static final String COLUMN_VALUE = "value";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.nes.dtvkit.tvsetting.tvsettings_table";
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/com.nes.dtvkit.tvsetting.tvsettings_table";
        public static final Uri CONTENT_URI = Uri.parse("content://com.nes.dtvkit.tvsetting/tvsettings_table");
        public static final Uri DYNAMIC_CONTENT_URI = Uri.parse("content://com.nes.dtvkit.tvsetting/dynamic_table");
        public static final String DYNAMIC_PATH = "dynamic_table";
        public static final String KEY_AUDIO_DESCRIPTION_SWITCH = "audioDescriptionSwitch";
        public static final String KEY_CURRENT_LANG_NAME_LIST = "language_name_list";
        public static final String KEY_FIRST_AUDIO_LANGUAGE_INDEX = "firstAudioLanguageIndex";
        public static final String KEY_FIRST_SUBTITLE_LANGUAGE_INDEX = "firstSubtitleLanguageIndex";
        public static final String KEY_KEY_DVB_C_ANNEX_MODE = "dvbc_annex_mode";
        public static final String KEY_LCN_SWITCH = "lcnSwitch";
        public static final String KEY_SECOND_AUDIO_LANGUAGE_INDEX = "secondAudioLanguageIndex";
        public static final String KEY_SECOND_SUBTITLE_LANGUAGE_INDEX = "secondSubtitleLanguageIndex";
        public static final String KEY_SUBTITLE_SWITCH = "subtitleSwitch";
        public static final String PATH = "tvsettings_table";
        public static final int VERSION = 1;

        private TvSettings() {
        }
    }

    public static final class TvProbes implements BaseColumns {
        public static final String COLUMN_PROBE_NAME = "probe_name";
        public static final String COLUMN_REF_CHANNEL_URI = "ref_channel_uri";
        public static final Uri CONTENT_URI = Uri.parse("content://com.google.android.tv.dtvprovider/tvprobes");
        public static final String PROBE_REF_CHANNEL_STATES = "ref_channel_stats";

        private TvProbes() {
        }
    }
}
