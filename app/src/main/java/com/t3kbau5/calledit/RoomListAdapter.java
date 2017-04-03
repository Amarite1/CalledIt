package com.t3kbau5.calledit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by benwi on 2016-11-02.
 */
public class RoomListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<Room> roomData = new ArrayList<Room>();
    private int mode;

    public RoomListAdapter(final Activity activity, int mode){
        this.context = activity;
        this.mode = mode;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final NetTasks nt = new NetTasks(activity);

        switch(mode){
            case 0:
                nt.loadRooms(new NetTasks.RoomTaskListener() {
                    @Override
                    public void roomsLoaded(List<Room> rooms) {
                        roomData = rooms;
                        notifyDataSetChanged();
                        notifyDataSetInvalidated();
                        if(activity.getClass() != RoomListActivity.class) return; //if this isn't the activity we expect
                        activity.findViewById(R.id.progressBar).setVisibility(View.GONE);
                    }
                });
                break;

            case 1:
                nt.loadRooms(new NetTasks.RoomTaskListener() {
                    @Override
                    public void roomsLoaded(final List<Room> rooms) {

                        final Calendar now = Calendar.getInstance();

                        for(int i=0; i<rooms.size(); i++){
                            final Room room = rooms.get(i);
                            final int finalI = i;
                            final int time = now.get(Calendar.HOUR_OF_DAY)*10000 + now.get(Calendar.MINUTE)*100;

                            nt.loadRoomHours(now, room.id, new NetTasks.HoursTaskListener() {
                                @Override
                                public void hoursLoaded(int[] hours) {
                                    if(hours != null && time >= hours[0] && time <= hours[1]){
                                        nt.loadRoomReservations(now, room.id, new NetTasks.ReservationsTaskListener() {
                                            @Override
                                            public void reservationsLoaded(List<Reservation> reservations) {

                                                boolean addRoom = true;
                                                for(int j=0; j<reservations.size(); j++){
                                                    Reservation res = reservations.get(j);
                                                    if(res.start <= time && time <= res.end){
                                                        if((res.end-res.start) >10000 && res.start >= time-10000) { //booking longer than 1 hr and started at least 1 hr ago
                                                            room.name = room.name + " (Possible)";
                                                            break;
                                                        }
                                                        addRoom = false;
                                                        break;
                                                    }
                                                }

                                                if(addRoom) roomData.add(room);

                                                if(finalI +1 == rooms.size()){
                                                    notifyDataSetChanged();
                                                    notifyDataSetInvalidated();

                                                    if(activity.getClass() != OpenRooms.class) return; //if this isn't the activity we expect
                                                    activity.findViewById(R.id.progressBar).setVisibility(View.GONE);
                                                    TextView status = (TextView) activity.findViewById(R.id.statusText);
                                                    if(roomData.size() >0){
                                                        status.setText("Rooms currently open: (Tap to Book)");
                                                    }else{
                                                        status.setText("No rooms currently open!");
                                                    }
                                                }

                                            }
                                        });
                                    }else{
                                        if(finalI +1 == rooms.size()){
                                            notifyDataSetChanged();
                                            notifyDataSetInvalidated();

                                            if(activity.getClass() != OpenRooms.class) return; //if this isn't the activity we expect
                                            activity.findViewById(R.id.progressBar).setVisibility(View.GONE);
                                            TextView status = (TextView) activity.findViewById(R.id.statusText);
                                            if(roomData.size() >0){
                                                status.setText("Rooms currently open: (Tap to Book)");
                                            }else{
                                                status.setText("No rooms currently open!");
                                            }
                                        }
                                    }
                                }
                            });
                        }
                    }
                });
                break;
        }

    }

    @Override
    public int getCount() {
        return roomData.size();
    }

    @Override
    public Object getItem(int position) {
        return roomData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LinearLayout ll;
        if(convertView != null){
            ll = (LinearLayout) convertView;
        }else{
            ll = (LinearLayout) inflater.inflate(R.layout.listitem_room, null);
        }

        final Room room = roomData.get(position);

        ((TextView) ll.findViewById(R.id.text_name)).setText(room.name);
        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                switch(mode){
                    case 0:
                        intent = new Intent(context, RoomInfoActivity.class);
                        intent.putExtra("room", room);
                        break;

                    case 1:
                        intent = new Intent(context, ReserveActivity.class);
                        intent.putExtra("roomID", room.id);
                        break;

                    default: return;
                }
                context.startActivity(intent);
            }
        });

        if(room.hasPhone){
            ll.findViewById(R.id.hasPhone).setVisibility(View.VISIBLE);
        }else{
            ll.findViewById(R.id.hasPhone).setVisibility(View.INVISIBLE);
        }

        if(room.hasTV){
            ll.findViewById(R.id.hasTV).setVisibility(View.VISIBLE);
        }else{
            ll.findViewById(R.id.hasTV).setVisibility(View.INVISIBLE);
        }

        TextView sizeText = (TextView) ll.findViewById(R.id.roomSize);

        switch(room.size){
            case 1:
                sizeText.setText("S");
                break;
            case 2:
                sizeText.setText("M");
                break;
            case 3:
                sizeText.setText("L");
                break;
        }

        return ll;
    }
}
