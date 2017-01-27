package com.t3kbau5.calledit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class OpenRooms extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_rooms);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ListView roomList = (ListView) findViewById(R.id.roomList);
        TextView statusText = (TextView) findViewById(R.id.statusText);
        ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar);
        pb.setEnabled(true);
        pb.setVisibility(View.VISIBLE);
        statusText.setText("Loading... Please Wait.");

        RoomListAdapter rla = new RoomListAdapter(this,1);
        roomList.setAdapter(rla);


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
