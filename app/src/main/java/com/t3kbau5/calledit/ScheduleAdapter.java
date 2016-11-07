package com.t3kbau5.calledit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
            if(reservations.get(i).start == hours[0] + 10000*position){
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
                    try {
                        Intent intent = new Intent(context, ReserveActivity.class);

                        String postData = null;
                        postData = generatePostData(position);
                        intent.putExtra("postData", postData);

                        context.startActivity(intent);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Error generating URL!", Toast.LENGTH_LONG).show();
                    }

                }
            });
        }else{
            Reservation res = (Reservation) slot;
            tv.setText(getTime(position) + " - Reserved (ends " + formatTime(res.end) + ")");
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

    private String generatePostData(int position) throws UnsupportedEncodingException {
        Calendar cal = Calendar.getInstance();
        String searchDate = cal.get(Calendar.YEAR) + "/" + cal.get(Calendar.MONTH) + "/" + cal.get(Calendar.DAY_OF_MONTH);
        String startTime = searchDate + "+" + getTime(position) + ":00";

        /*startTime = URLEncoder.encode(startTime, "UTF8");
        searchDate = URLEncoder.encode(searchDate, "UTF8");*/

        String postData = "SelectedRoomSize=&SelectedStartTime=" + startTime + "&SelectedTime=1&SelectedRoomID=" + roomID;
        postData += "&RoomIDPassedIn=True&SingleBuildingWorkflow=False&SelectedTimeSort=AnyTime&SelectedSearchDate=" + searchDate;
        postData += "&SelectedBuildingID=0";

        return postData;
    }
}
