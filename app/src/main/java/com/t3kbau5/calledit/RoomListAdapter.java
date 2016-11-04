package com.t3kbau5.calledit;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by benwi on 2016-11-02.
 */
public class RoomListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<RoomObject> roomData = new ArrayList<RoomObject>();

    public RoomListAdapter(Context context){
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        NetTasks.loadRooms(new NetTasks.RoomTaskListener() {
            @Override
            public void roomsLoaded(List<RoomObject> rooms) {
                roomData = rooms;
                notifyDataSetChanged();
                notifyDataSetInvalidated();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout ll;
        if(convertView != null){
            ll = (LinearLayout) convertView;
        }else{
            ll = (LinearLayout) inflater.inflate(R.layout.listitem_room, null);
        }
        ((TextView) ll.findViewById(R.id.text_name)).setText(roomData.get(position).name);
        return ll;
    }
}
