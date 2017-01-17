package com.t3kbau5.calledit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.IOException;
import java.net.URL;
import java.util.Calendar;

public class RoomInfoActivity extends AppCompatActivity {

    private Activity _this = this;
    private ScheduleAdapter sa;

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
                URL url;
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
        sa = new ScheduleAdapter(this, room.id);
        reservationsList.setAdapter(sa);

        final TextView textDate = (TextView) findViewById(R.id.textDate);
        textDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = sa.getCalendar();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                Calendar cal = Calendar.getInstance();
                                cal.set(year, monthOfYear, dayOfMonth);
                                sa.setCalendar(cal);
                                textDate.setText(dayOfMonth + "/" + (monthOfYear+1) + "/" + year + "( Tap to Change)");
                            }
                        },
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "DPD");
            }
        });
        Calendar cal = sa.getCalendar();
        textDate.setText(cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH)+1) + "/" + cal.get(Calendar.YEAR) + "( Tap to Change)");

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
