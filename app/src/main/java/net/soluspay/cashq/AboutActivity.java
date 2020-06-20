package net.soluspay.cashq;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.webkit.WebView;

import com.gndi_sd.szzt.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebView myWebView = new WebView(getApplicationContext());
        setContentView(myWebView);
        myWebView.loadUrl("https://soluspay.net/privacy");
//        setContentView(R.layout.activity_about);
    }
}