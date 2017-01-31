package com.t3kbau5.calledit;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ReservationsAdapter extends BaseAdapter {

    private Context context;
    private Bundle[] reservations;
    private int rtid=1, ttid=2;

    public ReservationsAdapter(Context context){
        this.context = context;
        PrimaryDatabase pdb = new PrimaryDatabase(context);
        pdb.open();
        reservations = pdb.getReservations();
        pdb.close();
    }

    @Override
    public int getCount() {
        return reservations.length;
    }

    @Override
    public Object getItem(int position) {
        return reservations[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Bundle res = reservations[position];

        LinearLayout ll;
        TextView roomText, timeText;


        /*if(convertView != null){
            ll = (LinearLayout) convertView;
            //override the id type check, as api < 17 doesn't allow for generated ids
            //noinspection ResourceType
            roomText = (TextView) ll.findViewById(1);
            //noinspection ResourceType
            timeText = (TextView) ll.findViewById(2);
        }else{*/
            ll = new LinearLayout(context);
            ll.setOrientation(LinearLayout.VERTICAL);
            roomText = new TextView(context);
            //override the id type check, as api < 17 doesn't allow for generated ids
            //noinspection ResourceType
            roomText.setId(rtid);
            timeText = new TextView(context);
            //noinspection ResourceType
            timeText.setId(ttid);
        //}

        roomText.setText("BMH " + res.getInt("room"));
        String tstr = res.getInt("date")/10000 + "/" + (res.getInt("date")%10000)/100 + "/" + res.getInt("date")%100;
        tstr += " " + res.getInt("time")/100 + ":" + res.getInt("time")%100;
        timeText.setText(tstr);

        ll.addView(roomText);
        ll.addView(timeText);

        return ll;
    }
}
