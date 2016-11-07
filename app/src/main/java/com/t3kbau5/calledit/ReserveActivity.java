package com.t3kbau5.calledit;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ProgressBar;

public class ReserveActivity extends AppCompatActivity {

    private Activity _this = this;
    private WebView webView;
    private SharedPreferences prefs;
    private ProgressBar pb;
    private boolean tryPostAgain = false;
    private String initialUrl;
    String postData = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        initialUrl = "https://queensu.evanced.info/dibs/";

        if(intent.hasExtra("postData")){
            initialUrl +="registration";
            postData = intent.getStringExtra("postData");

        }else if(intent.hasExtra("roomID")){
            initialUrl += "?space=" + intent.getIntExtra("roomID", -1);
        }

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        webView = (WebView) findViewById(R.id.webView);
        WebSettings ws = webView.getSettings();
        ws.setJavaScriptEnabled(true);

        final CookieManager cm = CookieManager.getInstance();
        cm.setAcceptCookie(true);
        String savedCookies = prefs.getString("cookies", "");
        if(!savedCookies.equals("")){
            cm.setCookie(prefs.getString("curl", ""), savedCookies);
        }

        pb = (ProgressBar) findViewById(R.id.progressBar);

        webView.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageStarted(WebView view, String url, Bitmap facIcon){
                pb.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if(webView.getUrl().toLowerCase().contains("login")){

                    if(postData != "") tryPostAgain = true;

                    if(prefs.contains("username")) {
                        webView.loadUrl("javascript:(function(){$('#txtUsername').val('" + prefs.getString("username", "") + "');})()");
                    }
                    prefs.edit().putString("cookies", cm.getCookie(url)).putString("curl", url).apply();
                } else if(tryPostAgain){
                    webView.postUrl(initialUrl, postData.getBytes());
                }
                pb.setVisibility(View.GONE);
            }
        });

        if(postData != ""){
            webView.postUrl(initialUrl, postData.getBytes());
        }else {
            webView.loadUrl(initialUrl);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.activity_reserve, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
            case R.id.menu_username:

                AlertDialog.Builder adb = new AlertDialog.Builder(this);
                adb.setTitle("Set Saved Username");

                final EditText et = new EditText(this);
                if(prefs.contains("username")) et.setText(prefs.getString("username", ""));
                et.setHint("12ab34");

                adb.setView(et);
                adb.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PreferenceManager.getDefaultSharedPreferences(_this).edit().putString("username", et.getText().toString()).apply();
                    }
                });
                adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                adb.show();


                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        if(webView.canGoBack()){
            webView.goBack();
        }else{
            finish();
        }
    }
}
