package com.t3kbau5.calledit;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    private static final int READ_PHONE_STATE_CODE = 23098;

    Activity _this = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn_reserve = (Button) findViewById(R.id.button_reserve);
        Button btn_rooms = (Button) findViewById(R.id.button_rooms);
        btn_rooms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(_this, RoomListActivity.class);
                _this.startActivity(intent);
            }
        });
        btn_reserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(_this, ReserveActivity.class);
                _this.startActivity(intent);
            }
        });

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        if ((!prefs.contains("phone") || prefs.getString("phone", "").isEmpty()) && PackageManager.PERMISSION_GRANTED == permissionCheck) {
            getPhoneNumber();
        } else if ((!prefs.contains("phone") || prefs.getString("phone", "").isEmpty()) && !prefs.getBoolean("noAutoDetect", false)) {
            //request permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    READ_PHONE_STATE_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case READ_PHONE_STATE_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    getPhoneNumber();

                } else {

                    PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("noAutoDetect", true).apply(); //never ask the user again
                    Toast.makeText(this, "Permission denied. Your phone number will not be auto-determined.", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private void getPhoneNumber(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //see http://stackoverflow.com/a/2480307/1896516
        TelephonyManager telephonyManager = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        String phoneNumber = telephonyManager.getLine1Number();
        if(phoneNumber == null || phoneNumber.contains("?")){
            Toast.makeText(this, "Unable to determine phone number!", Toast.LENGTH_LONG).show();
        }else{
            phoneNumber = phoneNumber.replace("(", "").replace(")", "").replace("+", "").replace(" ", ""); //remove potentially-present unwanted characters
            if(phoneNumber.length() > 10){
                phoneNumber = phoneNumber.substring(phoneNumber.length()-10); //trim leading digits
            }
            prefs.edit().putString("phone", phoneNumber).apply();
            Toast.makeText(this, "Phone number auto-detected for use with D!bs", Toast.LENGTH_SHORT).show();
        }
    }
}
