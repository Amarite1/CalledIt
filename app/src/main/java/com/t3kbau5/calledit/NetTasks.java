package com.t3kbau5.calledit;

import android.app.Activity;
import android.util.Log;

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
        void reservationsLoaded(List<Reservation> reservations);
    }

    public interface HoursTaskListener{
        void hoursLoaded(int[] hours);
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
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.roomsLoaded(null);
                        }
                    });
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
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.roomsLoaded(null);
                    }
                });
            }
        });
    }

    public void loadRoomReservations(Calendar date, int roomID, final ReservationsTaskListener listener){
        NetWorker nw = new NetWorker();
        nw.loadUrl("http://queensu.evanced.info/dibsapi/reservations/" + date.get(Calendar.YEAR) + "-" + (date.get(Calendar.MONTH)+1) + "-" + date.get(Calendar.DAY_OF_MONTH) + "/" + roomID, "text/json", new NetWorker.NetWorkerListener() {
            @Override
            public void onDataReceived(String data) {
                try {
                    JSONArray reservationData = new JSONArray(data);
                    final List<Reservation> reservations = new ArrayList<Reservation>();
                    for(int i=0;i<reservationData.length();i++){
                        JSONObject reservationObject = reservationData.getJSONObject(i);
                        Reservation reservation = new Reservation();

                        String start = reservationObject.getString("StartTime").split("T")[1].replaceAll(":", "");
                        reservation.start = Integer.parseInt(start);

                        String end = reservationObject.getString("EndTime").split("T")[1].replaceAll(":", "");
                        reservation.end = Integer.parseInt(end);

                        reservations.add(reservation);
                    }

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.reservationsLoaded(reservations);
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.reservationsLoaded(null);
                        }
                    });
                }

            }

            @Override
            public void onLoadError(int code) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.reservationsLoaded(null);
                    }
                });
            }
        });
    }

    public void loadRoomHours(Calendar date, int roomID, final HoursTaskListener listener){
        NetWorker nw = new NetWorker();
        String url = "http://queensu.evanced.info/dibsapi/roomHours/" + date.get(Calendar.YEAR) + "-" + (date.get(Calendar.MONTH)+1) + "-" + date.get(Calendar.DAY_OF_MONTH) + "/" + roomID;
        Log.d("url", url);
        nw.loadUrl(url, "text/json", new NetWorker.NetWorkerListener() {
            @Override
            public void onDataReceived(String data) {
                try {
                    JSONArray hoursData = new JSONArray(data);
                    final int[] hours = new int[2];
                    if(hoursData.length() < 1){
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.hoursLoaded(null);
                            }
                        });
                        return;
                    }
                    JSONObject reservationObject = hoursData.getJSONObject(0);

                    String start = reservationObject.getString("StartTime").split("T")[1].replaceAll(":", "");
                    hours[0] = Integer.parseInt(start);

                    String end = reservationObject.getString("EndTime").split("T")[1].replaceAll(":", "");
                    hours[1] = Integer.parseInt(end);

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.hoursLoaded(hours);
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.hoursLoaded(null);
                        }
                    });
                }

            }

            @Override
            public void onLoadError(int code) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.hoursLoaded(null);
                    }
                });
            }
        });
    }
}
