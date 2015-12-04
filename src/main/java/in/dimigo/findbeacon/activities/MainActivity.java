package in.dimigo.findbeacon.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.perples.recosdk.RECOBeacon;

import com.perples.recosdk.RECOBeaconManager;
import com.perples.recosdk.RECOBeaconRegion;
import com.perples.recosdk.RECOBeaconRegionState;
import com.perples.recosdk.RECOErrorCode;
import com.perples.recosdk.RECOMonitoringListener;
import com.perples.recosdk.RECORangingListener;
import com.perples.recosdk.RECOServiceConnectListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import in.dimigo.findbeacon.R;
import in.dimigo.findbeacon.api.ApiDeviceId;
import in.dimigo.findbeacon.api.ApiObject;
import in.dimigo.findbeacon.util.Schema;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity implements RECORangingListener, RECOServiceConnectListener, RECOMonitoringListener {

    private RECOBeaconManager mRECOBeaconManager;
    private SmoothProgressBar searchingProgress;
    private LinearLayout foundLayout;
    private WebView couponView;
    private TextView findingText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initPost();
        didFound(false);
        initUI();
        initBeaconManager();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Schema.REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(MainActivity.this, "블루투스 요청을 허용해주세요.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onServiceConnect() {
        Log.d("onServiceConnect()", getString(R.string.beacon_connect));
        List<RECOBeaconRegion> rangingRegions = new ArrayList<>();
        rangingRegions.add(new RECOBeaconRegion(Schema.RECO_UUID, Schema.RECO_DEVICE_RED_MAJOR, "RED"));
        rangingRegions.add(new RECOBeaconRegion(Schema.RECO_UUID, Schema.RECO_DEVICE_ORANGE_MAJOR, "ORANGE"));
        rangingRegions.add(new RECOBeaconRegion(Schema.RECO_UUID, Schema.RECO_DEVICE_GREEN_MAJOR, "GREEN"));
        for (RECOBeaconRegion beaconRegion : rangingRegions) {
            try {
                mRECOBeaconManager.startRangingBeaconsInRegion(beaconRegion);
                mRECOBeaconManager.requestStateForRegion(beaconRegion);
            } catch (RemoteException re) {
                re.printStackTrace();
            }
        }
    }

    @Override
    public void onServiceFail(RECOErrorCode recoErrorCode) {
        Log.d("onServiceFail", getString(R.string.beacon_failed));
    }

    @Override
    public void didRangeBeaconsInRegion(Collection<RECOBeacon> recoBeacons, RECOBeaconRegion recoBeaconRegion) {
        switch (recoBeacons.size()) {
            case 0:
                Log.d("탈주", recoBeaconRegion.getUniqueIdentifier());
                break;
            default:
                Log.d("인식", recoBeaconRegion.getUniqueIdentifier());
                didFound(true);
                try {
                    mRECOBeaconManager.stopRangingBeaconsInRegion(recoBeaconRegion);
                    mRECOBeaconManager.stopMonitoringForRegion(recoBeaconRegion);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

        }

    }

    @Override
    public void rangingBeaconsDidFailForRegion(RECOBeaconRegion recoBeaconRegion, RECOErrorCode recoErrorCode) {
        //TODO
    }

    private String getDeviceId() {
        return ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getSubscriberId();
    }

    @Override
    public void didEnterRegion(RECOBeaconRegion recoBeaconRegion, Collection<RECOBeacon> collection) {

    }

    @Override
    public void didExitRegion(RECOBeaconRegion recoBeaconRegion) {

    }

    @Override
    public void didStartMonitoringForRegion(RECOBeaconRegion recoBeaconRegion) {

    }

    @Override
    public void didDetermineStateForRegion(RECOBeaconRegionState recoBeaconRegionState, RECOBeaconRegion recoBeaconRegion) {

    }

    @Override
    public void monitoringDidFailForRegion(RECOBeaconRegion recoBeaconRegion, RECOErrorCode recoErrorCode) {

    }

    private void initUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setSubtitle(getDeviceId());
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setLogo(R.mipmap.ic_logo_text);

        couponView = (WebView) findViewById(R.id.coupon_webview);
        WebSettings webSettings = couponView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        searchingProgress = (SmoothProgressBar) findViewById(R.id.search_progress);
        foundLayout = (LinearLayout) findViewById(R.id.found_beacon);
        findingText = (TextView) findViewById(R.id.finding_text);
        findingText.append("\n"+getDeviceId());
        foundLayout.setVisibility(View.GONE);
    }

    private void didFound(boolean isFound) {
        if (isFound) {
            ApiDeviceId.setFoundBeacon(getApplicationContext(), true);
            findingText.setText(getString(R.string.beacon_find_end)+getDeviceId());
            foundLayout.setVisibility(View.VISIBLE);
            searchingProgress.setVisibility(View.GONE);
            couponView.loadUrl(Schema.RECO_API_ENDPOINT + Schema.LOTTERY + ApiDeviceId.getDeviceId(getApplicationContext()));
        }
    }

    private void initPost() {
        ApiDeviceId.setDeviceId(getApplicationContext(), getDeviceId());
        ApiDeviceId.setFoundBeacon(getApplicationContext(), false);
        final ApiObject apiObject = ApiObject.getInstance();

        apiObject.postDeviceId(ApiDeviceId.getDeviceId(getApplicationContext()), new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                Log.d("response", jsonObject.toString());
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private void initBeaconManager() {

        if (!ApiDeviceId.getFoundBeacon(getApplicationContext())) {
            mRECOBeaconManager = RECOBeaconManager.getInstance(getApplicationContext(), true, true);
            mRECOBeaconManager.bind(this);
            mRECOBeaconManager.setRangingListener(this);
            mRECOBeaconManager.setMonitoringListener(this);
        }

        final BluetoothAdapter mBluetoothAdapter;
        final BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
            mBluetoothAdapter = mBluetoothManager.getAdapter();
        else
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            final Intent mBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(mBluetoothIntent, Schema.REQUEST_ENABLE_BT);
        }

    }

}
