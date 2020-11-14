package com.example.bgctub_transport_tracker_studentapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class NewsAndUpdateActivity extends AppCompatActivity {
    private WebView mWebView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_and_update);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        mWebView = (WebView) findViewById(R.id.webView);
        //load the website**
        loadSite();

    }

    //load site to webView
    private void loadSite(){
        final String link = "https://bgctubtransporttrackingapp.blogspot.com/";
        mWebView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //if url contain "bgctubtransporttrackingapp.blogspot.com" then page will load
                //check for deprecated
                if(Uri.parse(url).getHost().endsWith("bgctubtransporttrackingapp.blogspot.com")){
                    return false;
                }else{
                    Toast.makeText(NewsAndUpdateActivity.this,"Sorry, this app not support this link.", Toast.LENGTH_LONG).show();
                    return true;
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url=request.getUrl().toString();
                if(Uri.parse(url).getHost().endsWith("bgctubtransporttrackingapp.blogspot.com")){
                    return false;
                }else{
                    Toast.makeText(NewsAndUpdateActivity.this,"Sorry, this app not support this link.",Toast.LENGTH_LONG).show();
                    return true;
                }
            }
        });

        //WebView settings**
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(true);
        webSettings.setSupportZoom(true);
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        //load the link**
        mWebView.loadUrl(link);


    }

    @Override
    public void onBackPressed() {
        //if backPressed then previous page will load**
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}