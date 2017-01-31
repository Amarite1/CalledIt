package com.t3kbau5.calledit;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    private static final int READ_PHONE_STATE_CODE = 23098;
    private static final int RECEIVE_SMS_CODE = 15987;

    private Activity _this = this;

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn_reserve = (Button) findViewById(R.id.button_reserve);
        Button btn_rooms = (Button) findViewById(R.id.button_rooms);
        Button btn_open = (Button) findViewById(R.id.button_open);
        ListView list_reservations = (ListView) findViewById(R.id.reservationsList);

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
        btn_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(_this, OpenRooms.class);
                _this.startActivity(intent);
            }
        });

        list_reservations.setAdapter(new ReservationsAdapter(this));

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        if ((!prefs.contains("phone") || prefs.getString("phone", "").isEmpty()) && PackageManager.PERMISSION_GRANTED == permissionCheck) {
            getPhoneNumber();
        } else if ((!prefs.contains("phone") || prefs.getString("phone", "").isEmpty()) && !prefs.getBoolean("noAutoDetect", false)) {
            //request permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    READ_PHONE_STATE_CODE);
        }

        permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);
        if (PackageManager.PERMISSION_GRANTED == permissionCheck) {

        } else if (!prefs.getBoolean("noSMS", false)) {
            smsPrompt();
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

                    prefs.edit().putBoolean("noAutoDetect", true).apply(); //never ask the user again
                    Toast.makeText(this, "Permission denied. Your phone number will not be auto-determined.", Toast.LENGTH_LONG).show();
                }
                return;
            }
            case RECEIVE_SMS_CODE:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(_this, "My Reservations enabled!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "You can enable this feature later if you wish.", Toast.LENGTH_LONG).show();
                    prefs.edit().putBoolean("noSMS", true).apply();
                }
                return;
            }
        }
    }

    private void getPhoneNumber(){

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

    private void smsPrompt(){
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("My Reservations");
        adb.setMessage("Called!t can automatically save your reservations for your reference" +
                " by reading text messages sent by D!BS. To do this, we need permission to read" +
                " your incoming texts. We only read the ones from D!BS and nothing else and your " +
                "texts never get sent anywhere. (you can look at our code on GitHub if you don't " +
                "believe us!) Would you like to enable this feature?");
        adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(_this, Manifest.permission.RECEIVE_SMS)){
                    Toast.makeText(_this, "Permission already granted, you're good to go!", Toast.LENGTH_SHORT).show();
                }else {
                    ActivityCompat.requestPermissions(_this,
                            new String[]{Manifest.permission.RECEIVE_SMS},
                            RECEIVE_SMS_CODE);
                }
                dialog.dismiss();
            }
        });
        adb.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                prefs.edit().putBoolean("noSMS", true).apply();
                dialog.dismiss();
                Toast.makeText(_this, "Okay, no worries! You can enable this feature later if you wish.", Toast.LENGTH_LONG).show();
            }
        });
        adb.show();
    }
}
