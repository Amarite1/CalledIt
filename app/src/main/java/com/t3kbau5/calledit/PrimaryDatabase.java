package com.t3kbau5.calledit;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class PrimaryDatabase {
    private Context context;
    private SQLiteDatabase db;
    private boolean isOpen = false;
    private static final String TAG = "PDB";

    public static abstract class ReservationsStructure implements BaseColumns {
        public static final String TABLE_NAME = "reservations";
        public static final String COLUMN_NAME_ID = "id";
        public static final String _ID = COLUMN_NAME_ID;
        public static final String COLUMN_NAME_ROOM_ID = "room_id";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_TIME = "time";
        public static final String COLUMN_NAME_DURATION = "duration";
    }

    public static abstract class RoomsStructure implements BaseColumns {
        public static final String TABLE_NAME = "rooms";
        public static final String COLUMN_NAME_ID = "id";
        public static final String _ID = COLUMN_NAME_ID;
        public static final String COLUMN_NAME_TV = "hasTV";
        public static final String COLUMN_NAME_PHONE = "hasPhone";
        public static final String COLUMN_NAME_SIZE = "size";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_PICTURE = "pictureURL";
        public static final String COLUMN_NAME_MAP = "mapURL";

    }

    public PrimaryDatabase(Context context){
        this.context = context;
    }

    public void open(){
        db = context.openOrCreateDatabase("primarydb", Context.MODE_PRIVATE, null);

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='" + ReservationsStructure.TABLE_NAME + "';", null);
        if(c.getCount() == 0){ //database doesn't exist: create it
            db.execSQL("CREATE TABLE IF NOT EXISTS " + ReservationsStructure.TABLE_NAME + "(" + ReservationsStructure.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    ReservationsStructure.COLUMN_NAME_ROOM_ID + " INTEGER," +
                    ReservationsStructure.COLUMN_NAME_DATE + " INTEGER," +
                    ReservationsStructure.COLUMN_NAME_TIME + " INTEGER," +
                    ReservationsStructure.COLUMN_NAME_DURATION + " INTEGER)");
        }

        c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='" + RoomsStructure.TABLE_NAME + "';", null);
        if(c.getCount() == 0){ //database doesn't exist: create it
            db.execSQL("CREATE TABLE IF NOT EXISTS " + RoomsStructure.TABLE_NAME + "(" + RoomsStructure.COLUMN_NAME_ID + " INTEGER PRIMARY KEY," +
                    RoomsStructure.COLUMN_NAME_NAME + " TEXT," +
                    RoomsStructure.COLUMN_NAME_DESCRIPTION + " TEXT," +
                    RoomsStructure.COLUMN_NAME_SIZE + " INTEGER," +
                    RoomsStructure.COLUMN_NAME_TV + " INTEGER," +
                    RoomsStructure.COLUMN_NAME_PHONE + " INTEGER," +
                    RoomsStructure.COLUMN_NAME_PICTURE + " TEXT," +
                    RoomsStructure.COLUMN_NAME_MAP + " TEXT)");
        }

        isOpen = true;
    }

    public void close(){
        db.close();
        isOpen = false;
    }

    public boolean isOpen(){
        return isOpen;
    }

    public boolean addReservation(int room, int date, int time, int duration){
        if(!isOpen) return false;

        db.execSQL("INSERT INTO " + ReservationsStructure.TABLE_NAME + " ( " + ReservationsStructure.COLUMN_NAME_ROOM_ID + ", " + ReservationsStructure.COLUMN_NAME_DATE + ", " + ReservationsStructure.COLUMN_NAME_TIME + ", " + ReservationsStructure.COLUMN_NAME_DURATION + ") VALUES (" + room + ", " + date + ", " + time + ", " + duration + ")");

        return true;
    }

    public List<Bundle> getReservations(){
        if(!isOpen) return null;

        Cursor c = db.rawQuery("SELECT * FROM " + ReservationsStructure.TABLE_NAME, null);

        if(c.getCount() > 0){
            List<Bundle> reservations = new ArrayList<>();

            Calendar cal = Calendar.getInstance();
            int today = cal.get(Calendar.DAY_OF_MONTH)*100 + (cal.get(Calendar.MONTH)+1) + cal.get(Calendar.YEAR)*10000;

            while(c.moveToNext()){

                //cleanup old reservations if they're still in the DB

                Log.d(TAG, c.getInt(2) + " ? " + today);
                if(c.getInt(2) < today){
                    deleteReservation(c.getInt(0));
                    continue;
                }

                Bundle b = new Bundle();
                b.putInt("room", c.getInt(1));
                b.putInt("date", c.getInt(2));
                b.putInt("time", c.getInt(3));
                b.putInt("duration", c.getInt(4));

                reservations.add(b);
            }

            return reservations;
        }else{
            return new ArrayList<Bundle>();
        }
    }

    public boolean deleteReservation(int id){
        if(!isOpen) return false;

        db.execSQL("DELETE FROM " + ReservationsStructure.TABLE_NAME + " WHERE " + ReservationsStructure.COLUMN_NAME_ID + " = " + id);

        return true;
    }

    public boolean storeRooms(Room[] rooms){
        if(!isOpen) return false;

        db.execSQL("DELETE FROM " + RoomsStructure.TABLE_NAME + " WHERE 1"); //clear the old rooms

        for(int i=0; i<rooms.length; i++){
            db.execSQL("INSERT INTO " + RoomsStructure.TABLE_NAME +
                    " ( " + RoomsStructure.COLUMN_NAME_ID + ", "+
                    RoomsStructure.COLUMN_NAME_NAME + ", " +
                    RoomsStructure.COLUMN_NAME_DESCRIPTION + ", " +
                    RoomsStructure.COLUMN_NAME_SIZE + ", " +
                    RoomsStructure.COLUMN_NAME_TV + ", " +
                    RoomsStructure.COLUMN_NAME_PHONE + ", " +
                    RoomsStructure.COLUMN_NAME_PICTURE + ", " +
                    RoomsStructure.COLUMN_NAME_MAP + ")" +
                    " VALUES(" +
                    rooms[i].id + ", " +
                    "'" + rooms[i].name + "', " +
                    "'" + rooms[i].description + "', " +
                    rooms[i].size + ", " +
                    rooms[i].hasTV + ", " +
                    rooms[i].hasPhone + ", " +
                    "'" + rooms[i].pictureUrl + "'" +
                    "'" + rooms[i].mapUrl + ")");
        }
        return true;
    }

    public Room[] getRooms(){
        if(!isOpen) return null;
        Cursor c = db.rawQuery("SELECT * FROM " + RoomsStructure.TABLE_NAME, null);

        Room[] rooms = new Room[c.getCount()];
        while(c.moveToNext()){
            Room room = new Room();
            room.id = c.getInt(0);
            room.name = c.getString(1);
            room.description = c.getString(2);
            room.size = c.getInt(3);
            room.hasTV = (c.getInt(4) != 0);
            room.hasPhone = (c.getInt(5) != 0);
            room.pictureUrl = c.getString(6);
            room.mapUrl = c.getString(7);

            rooms[c.getPosition()] = room;
        }

        return rooms;
    }
}
