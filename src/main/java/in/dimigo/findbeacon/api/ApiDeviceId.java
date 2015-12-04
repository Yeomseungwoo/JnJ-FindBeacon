package in.dimigo.findbeacon.api;

import android.content.Context;
import android.content.SharedPreferences;

import in.dimigo.findbeacon.util.Schema;

public class ApiDeviceId {

    static SharedPreferences pref;
    static SharedPreferences.Editor editor;

    public static void setDeviceId(Context mContext, String deviceId) {
        pref = mContext.getSharedPreferences(Schema.DEVICE_ID_KEY, Context.MODE_PRIVATE);
        editor = pref.edit();
        editor.putString(Schema.DEVICE_ID_KEY, deviceId).apply();
    }

    public static void setFoundBeacon(Context mContext, boolean isFind){
        pref= mContext.getSharedPreferences(Schema.FIND_BEACON_KEY, Context.MODE_PRIVATE);
        editor = pref.edit();
        editor.putBoolean(Schema.FIND_BEACON_KEY, isFind).apply();
    }

    public static String getDeviceId(Context mContext) {
        pref = mContext.getSharedPreferences(Schema.DEVICE_ID_KEY, Context.MODE_PRIVATE);
        return pref.getString(Schema.DEVICE_ID_KEY, null);
    }

    public static boolean getFoundBeacon(Context mContext){
        pref = mContext.getSharedPreferences(Schema.FIND_BEACON_KEY, Context.MODE_PRIVATE);
        return pref.getBoolean(Schema.FIND_BEACON_KEY, false);

    }
}
