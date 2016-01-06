package com.example.filedownload;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class CoordinatorActivity extends Activity {

    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinator);
        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadData("","text/html",null);
        webView.loadUrl("http://v2.master.appisbest.com/report/index.html");
    }
}
