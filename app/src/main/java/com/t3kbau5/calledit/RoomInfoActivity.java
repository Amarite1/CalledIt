package com.t3kbau5.calledit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class RoomInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_info);

        Intent intent = getIntent();
        final Room room = (Room) intent.getExtras().getSerializable("room");
        final ImageView image = (ImageView) findViewById(R.id.roomImage);
        TextView name = (TextView) findViewById(R.id.roomName);
        TextView desc = (TextView) findViewById(R.id.roomDescription);

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

    }
}
