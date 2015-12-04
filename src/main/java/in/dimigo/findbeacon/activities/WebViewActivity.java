package in.dimigo.findbeacon.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import in.dimigo.findbeacon.R;
import in.dimigo.findbeacon.api.ApiDeviceId;
import in.dimigo.findbeacon.util.Schema;

public class WebViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        WebView lotteryView = (WebView)findViewById(R.id.lottery_view);
        WebSettings webSettings = lotteryView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        lotteryView.loadUrl(Schema.RECO_API_ENDPOINT + Schema.LOTTERY + ApiDeviceId.getDeviceId(getApplicationContext()));
    }

    public void showAsPopup(float widthScale, float heightScale){
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int width = (int)(displayMetrics.widthPixels*widthScale);
        final int height = (int) (displayMetrics.heightPixels*widthScale);
        getWindow().setLayout(width, height);

    }

}
