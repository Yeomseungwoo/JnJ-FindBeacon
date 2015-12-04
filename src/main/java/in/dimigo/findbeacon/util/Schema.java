package in.dimigo.findbeacon.util;

public class Schema {
    public static final String RECO_API_ENDPOINT = "http://beacon.lap.so/lottery";
    public static final String LOTTERY = "/coupon.php?uuid=";

    public static final String DEVICE_ID_KEY = "uuid";
    public static final String FIND_BEACON_KEY = "isfindbeacon";

    public static final String RECO_UUID = "24DDF411-8CF1-440C-87CD-E368DAF9C93E";
    public static final int RECO_DEVICE_RED_MAJOR = 17989;
    public static final int RECO_DEVICE_GREEN_MAJOR = 17985;
    public static final int RECO_DEVICE_ORANGE_MAJOR = 0;

    public static final boolean SCAN_RECO_ONLY = true;
    public static final boolean ENABLE_BACKGROUND_RANGING_TIMEOUT = true;
    public static final boolean DISCONNECTION_SCAN = false;
    public static final int REQUEST_ENABLE_BT = 1;
}
