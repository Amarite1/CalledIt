package com.t3kbau5.calledit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by benwi on 2016-11-02.
 */
public class RoomListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<Room> roomData = new ArrayList<Room>();

    public RoomListAdapter(final Activity activity){
        this.context = activity;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        NetTasks nt = new NetTasks(activity);
        nt.loadRooms(new NetTasks.RoomTaskListener() {
            @Override
            public void roomsLoaded(List<Room> rooms) {
                roomData = rooms;
                notifyDataSetChanged();
                notifyDataSetInvalidated();
                activity.findViewById(R.id.progressBar).setVisibility(View.GONE);
            }
        });
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
                Intent intent = new Intent(context, RoomInfoActivity.class);
                intent.putExtra("room", room);
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
