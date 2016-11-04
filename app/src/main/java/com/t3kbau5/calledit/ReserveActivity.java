package com.t3kbau5.calledit;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ReserveActivity extends AppCompatActivity {

    private Activity _this = this;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String url = "https://queensu.evanced.info/dibs/";
        if(intent.hasExtra("roomID")){
            url += "?space=" + intent.getIntExtra("roomID", -1);
        }

        webView = (WebView) findViewById(R.id.webView);
        WebSettings ws = webView.getSettings();
        ws.setJavaScriptEnabled(true);

        final CookieManager cm = CookieManager.getInstance();
        cm.setAcceptCookie(true);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String savedCookies = prefs.getString("cookies", "");
        if(!savedCookies.equals("")){
            cm.setCookie(prefs.getString("curl", ""), savedCookies);
        }

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d("wv", "opf");
                if(webView.getUrl().toLowerCase().contains("login")){
                    Log.d("wv", "login");
                    //webView.loadUrl("javascript:(function(){$('#txtUsername').val('" + username + "');$('#pwdPassword').val('" + password + "');})()");
                    prefs.edit().putString("cookies", cm.getCookie(url)).putString("curl", url).apply();
                }
            }
        });
        webView.loadUrl(url);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
