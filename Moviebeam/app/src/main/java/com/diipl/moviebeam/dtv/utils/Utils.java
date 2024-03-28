package com.diipl.moviebeam.dtv.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.diipl.moviebeam.R;
import com.diipl.moviebeam.dtv.DvbContract;
import com.diipl.moviebeam.dtv.SatelliteBean;
import com.diipl.moviebeam.dtv.TransponderBean;

import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static List<String> DIS_EQC_POSITION_LIST = new ArrayList();
    public static List<String> DIS_EQC_STEP_LIST = new ArrayList();
    private static final String TAG = "Utils";

    static {
        for (int i = 1; i <= 255; i++) {
            DIS_EQC_POSITION_LIST.add(i + "");
        }
        for (int i2 = 1; i2 <= 16; i2++) {
            DIS_EQC_STEP_LIST.add(i2 + "");
        }
    }

    public static String getTpString(Context context, TransponderBean transponderBean, String str) {
        return getTpString(context, transponderBean, str, 0, 0);
    }

    public static String getTpString(Context context, TransponderBean transponderBean, String str, int i, int i2) {
        if (transponderBean == null) {
            return "--/--";
        }
        StringBuilder sb = new StringBuilder();
        if (str.equals(DvbContract.Tuner.DVB_S)) {
            if (i2 > 0) {
                sb.append("(");
                sb.append(i + 1);
                sb.append(MqttTopic.TOPIC_LEVEL_SEPARATOR);
                sb.append(i2);
                sb.append(")");
            }
            sb.append(transponderBean.getFreq() / 1000);
            sb.append(MqttTopic.TOPIC_LEVEL_SEPARATOR);
            sb.append(getArray(context, R.array.polarity)[transponderBean.getPolar()]);
            sb.append(MqttTopic.TOPIC_LEVEL_SEPARATOR);
            sb.append(transponderBean.getSymbol() / 1000);
        } else if (str.equals(DvbContract.Tuner.DVB_C)) {
            sb.append(transponderBean.getFreq() / 1000000);
            sb.append("MHz/");
            sb.append(transponderBean.getSymbol() / 1000);
            sb.append(MqttTopic.TOPIC_LEVEL_SEPARATOR);
            sb.append(getArray(context, R.array.modulation_array)[transponderBean.getMod()]);
        } else {
            sb.append(transponderBean.getFreq() / 1000000);
            sb.append("MHz/");
            sb.append(getArray(context, R.array.bandwidth)[transponderBean.getBw()]);
        }
        return sb.toString();
    }

    public static String[] getArray(Context context, int i) {
        return context.getResources().getStringArray(i);
    }

    public static List<String> toStringList(CharSequence[] charSequenceArr) {
        ArrayList arrayList = new ArrayList();
        for (CharSequence charSequence : charSequenceArr) {
            arrayList.add(charSequence.toString());
        }
        return arrayList;
    }

    public static int getLnbPosition(Context context, SatelliteBean satelliteBean) {
        String str;
        if (satelliteBean == null) {
            return 0;
        }
        int lofLo = satelliteBean.getLofLo();
        int lofHi = satelliteBean.getLofHi();
        int customLowLnb = satelliteBean.getCustomLowLnb();
        String[] array = getArray(context, R.array.lnb_type);
        if (!(lofLo == lofHi && lofHi == 0) && customLowLnb <= 0) {
            String str2 = array[0];
            if (satelliteBean.getLnbType() == 4) {
                str = lofLo + MqttTopic.TOPIC_LEVEL_SEPARATOR + lofHi + context.getResources().getString(R.string.lnb_ocs);
            } else if (lofLo == lofHi) {
                str = String.valueOf(lofLo);
            } else {
                str = lofLo + MqttTopic.TOPIC_LEVEL_SEPARATOR + lofHi;
            }
            Log.d(TAG, "getLnbValue : " + str);
            for (int i = 0; i < array.length; i++) {
                if (array[i].equals(str)) {
                    return i;
                }
            }
            return 0;
        }
        Log.d(TAG, "getLnbPosition: customize position ...");
        return array.length - 1;
    }

    public static boolean checkTp(TransponderBean transponderBean, TransponderBean transponderBean2) {
        if (transponderBean == null || transponderBean2 == null) {
            return false;
        }
        boolean z = Math.abs(transponderBean.getFreq() - transponderBean2.getFreq()) / 1000 < 3;
        boolean z2 = transponderBean.getPolar() == transponderBean2.getPolar();
        boolean z3 = Math.abs(transponderBean.getSymbol() - transponderBean2.getSymbol()) / 1000 < 3;
        if (!z || !z2 || !z3) {
            return false;
        }
        return true;
    }

    public static int polarityToInt(Context context, String str) {
        String[] array = getArray(context, R.array.polarity);
        for (int i = 0; i < array.length; i++) {
            if (array[i].equalsIgnoreCase(str)) {
                return i;
            }
        }
        return -1;
    }

    public static <T> int clickLeftOrRight(boolean z, int i, List<T> list) {
        if (z) {
            return i <= 0 ? list.size() - 1 : i - 1;
        }
        if (i >= list.size() - 1) {
            return 0;
        }
        return i + 1;
    }

    public static String intToHex(int i, int i2) {
        StringBuffer stringBuffer = new StringBuffer();
        char[] cArr = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        while (i != 0) {
            stringBuffer.append(cArr[i % 16]);
            i /= 16;
        }
        return add_zore(stringBuffer.reverse().toString(), i2);
    }

    public static String add_zore(String str, int i) {
        if (str.length() >= i) {
            return str;
        }
        return add_zore("0" + str, i);
    }

    public static String formatDateTime(int i, int i2, int i3) {
        Object obj;
        Object obj2;
        StringBuilder sb = new StringBuilder();
        sb.append(i);
        sb.append(MqttTopic.TOPIC_LEVEL_SEPARATOR);
        if (i2 >= 10) {
            obj = Integer.valueOf(i2);
        } else {
            obj = "0" + i2;
        }
        sb.append(obj);
        sb.append(MqttTopic.TOPIC_LEVEL_SEPARATOR);
        if (i3 >= 10) {
            obj2 = Integer.valueOf(i2);
        } else {
            obj2 = "0" + i3;
        }
        sb.append(obj2);
        sb.append(" ");
        return sb.toString();
    }

    public static String formatTime(int i, int i2, int i3) {
        Object obj;
        Object obj2;
        StringBuilder sb = new StringBuilder();
        sb.append(i);
        sb.append(":");
        if (i2 >= 10) {
            obj = Integer.valueOf(i2);
        } else {
            obj = "0" + i2;
        }
        sb.append(obj);
        sb.append(":");
        if (i3 >= 10) {
            obj2 = Integer.valueOf(i3);
        } else {
            obj2 = "0" + i3;
        }
        sb.append(obj2);
        return sb.toString();
    }

    public static String hexString2binaryString(String str) {
        if (str == null || str.length() % 2 != 0) {
            return null;
        }
        int i = 0;
        String str2 = "";
        while (i < str.length()) {
            StringBuilder sb = new StringBuilder();
            sb.append("0000");
            int i2 = i + 1;
            sb.append(Integer.toBinaryString(Integer.parseInt(str.substring(i, i2), 16)));
            String sb2 = sb.toString();
            str2 = str2 + sb2.substring(sb2.length() - 4);
            i = i2;
        }
        return str2;
    }

    public static String formatSortPosition(int i, int i2) {
        if (i == -1 || i2 == 0) {
            return "";
        }
        return "(" + (i + 1) + MqttTopic.TOPIC_LEVEL_SEPARATOR + i2 + ")";
    }

    public static List<String> getUniCableUserBand1() {
        ArrayList arrayList = new ArrayList();
        int i = 0;
        while (i < 8) {
            i++;
            arrayList.add(String.valueOf(i));
        }
        return arrayList;
    }

    public static List<String> getUniCableUserBand2() {
        ArrayList arrayList = new ArrayList();
        int i = 0;
        while (i < 32) {
            i++;
            arrayList.add(String.valueOf(i));
        }
        return arrayList;
    }

    public static List<SatelliteBean.UniCableBean> parseUniCable(String str) {
        ArrayList arrayList = new ArrayList();
        Log.d(TAG, "parseUniCable: " + str);
        if (TextUtils.isEmpty(str)) {
            arrayList.add(new SatelliteBean.UniCableBean(0));
            arrayList.add(new SatelliteBean.UniCableBean(1));
            return arrayList;
        }
        try {
            JSONArray jSONArray = new JSONArray(str);
            for (int i = 0; i < jSONArray.length(); i++) {
                JSONObject optJSONObject = jSONArray.optJSONObject(i);
                int optInt = optJSONObject.optInt("unicable_format");
                int optInt2 = optJSONObject.optInt("user_band");
                int optInt3 = optJSONObject.optInt("ub_frequency");
                int optInt4 = optJSONObject.optInt("unicable_position");
                SatelliteBean.UniCableBean uniCableBean = new SatelliteBean.UniCableBean();
                uniCableBean.setFormat(optInt);
                uniCableBean.setUserBand(optInt2);
                uniCableBean.setUnFrequency(optInt3);
                uniCableBean.setPosition(optInt4);
                arrayList.add(uniCableBean);
            }
            return arrayList;
        } catch (Exception e) {
            e.printStackTrace();
            arrayList.add(new SatelliteBean.UniCableBean(0));
            arrayList.add(new SatelliteBean.UniCableBean(1));
            return arrayList;
        }
    }

    public static String uNiCableToString(List<SatelliteBean.UniCableBean> list) {
        if (list == null || list.isEmpty()) {
            Log.e(TAG, "uNiCableToString: UniCableBeans is null or empty ...");
            return "";
        }
        JSONArray jSONArray = new JSONArray();
        for (SatelliteBean.UniCableBean next : list) {
            JSONObject jSONObject = new JSONObject();
            try {
                jSONObject.put("unicable_format", next.getFormat());
                jSONObject.put("user_band", next.getUserBand());
                jSONObject.put("ub_frequency", next.getUbFrequency());
                jSONObject.put("unicable_position", next.getPosition());
                jSONArray.put(jSONObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return jSONArray.toString();
    }

    public static SatelliteBean.UniCableBean getUniCableByType(SatelliteBean satelliteBean) {
        List<SatelliteBean.UniCableBean> parseUniCable = parseUniCable(satelliteBean.getUniCableDataString());
        for (SatelliteBean.UniCableBean next : parseUniCable) {
            if (next.getFormat() == satelliteBean.getUniCableFormat()) {
                return next;
            }
        }
        return parseUniCable.get(0);
    }
}
