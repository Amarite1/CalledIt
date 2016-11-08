package com.t3kbau5.calledit;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by benwi on 2016-11-05.
 */
public class ScheduleAdapter extends BaseAdapter {

    private Context context;
    private List<Reservation> reservations;
    private int roomID;
    private int[] hours = {0, 0};

    public ScheduleAdapter(Activity activity, final int roomID){
        context = activity;
        this.roomID = roomID;

        final NetTasks nt = new NetTasks(activity);
        nt.loadRoomHours(Calendar.getInstance(), roomID, new NetTasks.HoursTaskListener() {
            @Override
            public void hoursLoaded(int[] h) {
                if(h == null) return;
                hours=h;
                notifyDataSetChanged();
                notifyDataSetInvalidated();
                nt.loadRoomReservations(Calendar.getInstance(), roomID, new NetTasks.ReservationsTaskListener() {
                    @Override
                    public void reservationsLoaded(List<Reservation> res) {
                        reservations=res;
                        notifyDataSetChanged();
                        notifyDataSetInvalidated();
                    }
                });
            }
        });

    }

    private int getNumberHours(){
        return (hours[1] - hours[0])/10000;
    }

    @Override
    public int getCount() {
        return getNumberHours();
    }

    @Override
    public Object getItem(int position) {
        if(reservations == null) return null;
        for(int i=0; i<reservations.size(); i++){
            if(reservations.get(i).start <= hours[0] + 10000*position && reservations.get(i).end > hours[0] + 10000*position){
                return reservations.get(i);
            }
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        TextView tv;
        if(convertView != null){
            tv = (TextView) convertView;
        }else{
            tv = new TextView(context);
            tv.setTextSize(20);
        }
        Object slot = getItem(position);
        if(slot == null){
            tv.setText(getTime(position) + " - Open (Tap to Book)");
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder adb = new AlertDialog.Builder(context);
                    adb.setTitle("Select Duration");

                    CharSequence[] options;

                    if((position+2 < getCount() && getItem(position+2) == null) && (position+1 < getCount() && getItem(position+1) == null)){
                        options = new CharSequence[]{"1 Hour", "2 Hours", "3 Hours"};
                    }else if(position+1 < getCount() && getItem(position+1) == null){
                        options = new CharSequence[]{"1 Hour", "2 Hours"};
                    }else{
                        options = new CharSequence[]{"1 Hour"};
                    }

                    adb.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                Intent intent = new Intent(context, ReserveActivity.class);
                                String postData = generatePostData(position, which+1);
                                intent.putExtra("postData", postData);

                                context.startActivity(intent);
                                dialog.dismiss();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                                Toast.makeText(context, "Error generating URL!", Toast.LENGTH_LONG).show();
                            }

                        }
                    });

                    adb.show();

                }
            });
        }else{
            Reservation res = (Reservation) slot;
            tv.setText(getTime(position) + " - Reserved (ends " + formatTime(res.end) + ")");
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Unable to reserve, someone else already has this time!", Toast.LENGTH_LONG).show();
                }
            });
        }
        return tv;
    }

    private String getTime(int position){
        int time = (hours[0] + position*10000);
        return formatTime(time);
    }

    private String formatTime(int time){
        String tstr = "" + time/100;
        if(time/100 < 1000){
            tstr = tstr.substring(0, 1) + ":" + tstr.substring(1);
        }else {
            tstr = tstr.substring(0, 2) + ":" + tstr.substring(2);
        }
        return tstr;
    }

    private String generatePostData(int position, int duration) throws UnsupportedEncodingException {
        Calendar cal = Calendar.getInstance();
        String searchDate = cal.get(Calendar.YEAR) + "/" + cal.get(Calendar.MONTH) + "/" + cal.get(Calendar.DAY_OF_MONTH);
        String startTime = searchDate + "+" + getTime(position) + ":00";

        String postData = "SelectedRoomSize=&SelectedStartTime=" + startTime + "&SelectedTime=" + duration + "&SelectedRoomID=" + roomID;
        postData += "&RoomIDPassedIn=True&SingleBuildingWorkflow=False&SelectedTimeSort=AnyTime&SelectedSearchDate=" + searchDate;
        postData += "&SelectedBuildingID=0";

        Log.d("SAR", postData);

        return postData;
    }
}
