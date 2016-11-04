package com.t3kbau5.calledit;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

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
    }
}
