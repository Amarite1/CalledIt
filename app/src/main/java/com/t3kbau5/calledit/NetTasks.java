package com.t3kbau5.calledit;

import android.app.Activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by benwi on 2016-11-02.
 */
public class NetTasks {

    Activity activity;

    public NetTasks(Activity activity){
        this.activity=activity;
    }

    public interface RoomTaskListener{
        void roomsLoaded(List<Room> rooms);
    }

    public interface ReservationsTaskListener{
        void reservationsLoaded();
    }

    public void loadRooms(final RoomTaskListener listener){
        NetWorker nw = new NetWorker();
        nw.loadUrl("http://queensu.evanced.info/dibsapi/rooms", "text/json", new NetWorker.NetWorkerListener() {
            @Override
            public void onDataReceived(String data) {
                final List<Room> rooms = new ArrayList<Room>();
                try {
                    JSONArray arrayOfRooms = new JSONArray(data);
                    for(int i=0; i<arrayOfRooms.length();i++){
                        Room room = new Room();
                        JSONObject roomJson = arrayOfRooms.getJSONObject(i);
                        room.name = roomJson.getString("Name");
                        room.mapUrl = roomJson.getString("Map");
                        room.description = roomJson.getString("Description");
                        room.pictureUrl = roomJson.getString("Picture");
                        room.id = roomJson.getInt("RoomID");
                        room.determineAttributes();
                        rooms.add(room);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.roomsLoaded(rooms);
                    }
                });
            }

            @Override
            public void onLoadError(int code) {
                listener.roomsLoaded(null);
            }
        });
    }

    public void loadRoomReservations(Calendar date, int roomID, final ReservationsTaskListener listener){
        NetWorker nw = new NetWorker();
        nw.loadUrl("http://queensu.evanced.info/dibsapi/reservations/" + date.get(Calendar.YEAR) + "-" + date.get(Calendar.MONTH) + "-" + date.get(Calendar.DAY_OF_MONTH) + "/" + roomID, "text/json", new NetWorker.NetWorkerListener() {
            @Override
            public void onDataReceived(String data) {
                try {
                    JSONArray reservationData = new JSONArray(data);
                } catch (JSONException e) {
                    e.printStackTrace();
                    listener.reservationsLoaded();
                }

            }

            @Override
            public void onLoadError(int code) {
                listener.reservationsLoaded();
            }
        });
    }
}
