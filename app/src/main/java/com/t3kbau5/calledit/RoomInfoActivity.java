package com.t3kbau5.calledit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class RoomInfoActivity extends AppCompatActivity {

    private Activity _this = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_info);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        final Room room = (Room) intent.getExtras().getSerializable("room");
        final ImageView image = (ImageView) findViewById(R.id.roomImage);
        TextView name = (TextView) findViewById(R.id.roomName);
        TextView desc = (TextView) findViewById(R.id.roomDescription);
        Button reserve = (Button) findViewById(R.id.button_reserve);

        //load image
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url = null;
                try {
                    url = new URL(room.pictureUrl);
                    final Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            image.setImageBitmap(bmp);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    image.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
                }
            }
        }).start();

        name.setText(room.name);
        desc.setText(room.description);

        reserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(_this, ReserveActivity.class);
                intent.putExtra("roomID", room.id);
                _this.startActivity(intent);
                _this.finish();
            }
        });


        ListView reservationsList = (ListView) findViewById(R.id.reservationsList);
        reservationsList.setAdapter(new ScheduleAdapter(this, room.id));

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
