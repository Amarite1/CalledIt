package com.t3kbau5.calledit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

public class RoomListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);
        ListView list_rooms = (ListView) findViewById(R.id.list_rooms);
        list_rooms.setAdapter(new RoomListAdapter(this));
    }
}
