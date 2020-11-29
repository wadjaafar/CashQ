package net.soluspay.cashq;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gndi_sd.szzt.R;

public class QRWebview extends AppCompatActivity {
    private String viewUrl = "https://qr.noebs.dev";

    private WebView webview;
    private static final String TAG = "QR Info";
    private ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewUrl = getIntent().getStringExtra("qr");
        Log.i("getIntenet", getIntent().getStringExtra("qr"));

        setContentView(R.layout.activity_about);
        setTitle("QR Info");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.webview = findViewById(R.id.webview);
        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        progressBar = ProgressDialog.show(QRWebview.this, getString(R.string.loading), getString(R.string.loading_wait));
//        progressBar.show();
        webview.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }


            public void onPageFinished(WebView view, String url) {
                if (progressBar.isShowing()) {
                    progressBar.dismiss();
                }
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(QRWebview.this, "Oh no! " + description, Toast.LENGTH_SHORT).show();
                alertDialog.setTitle("Error");
                alertDialog.setMessage(description);
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                alertDialog.show();
            }
        });
        progressBar.show();
        Log.i("qr_url", Constants.QR_INFO+"?q="+ viewUrl);
        webview.loadUrl(Constants.QR_INFO+"?qr="+ viewUrl);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        onBackPressed();
        return true;
    }

}
