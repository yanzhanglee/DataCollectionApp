package com.example.datacollectionapp;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class WebViewWhitelist extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_whitelist);


        WebView wvWhitelist = (WebView) findViewById(R.id.wvWhitelist);

        wvWhitelist.getSettings().setJavaScriptEnabled(true);
        wvWhitelist.getSettings().setDomStorageEnabled(true);
        wvWhitelist.getSettings().setUseWideViewPort(true);
        wvWhitelist.getSettings().setLoadWithOverviewMode(true);

        WebViewClient webViewClient = new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                super.shouldOverrideUrlLoading(view, request);
                view.loadUrl(String.valueOf(request.getUrl()));
                return true;
            }
        };
        wvWhitelist.setWebViewClient(webViewClient);

        String urlWhitelist = "https://support.qq.com/product/370073/faqs-more";
        wvWhitelist.loadUrl(urlWhitelist);

        ActionBar actionBar = this.getActionBar();
        if (actionBar == null)
            return;
        actionBar.setTitle("如何将应用添加至白名单");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

    }

}
