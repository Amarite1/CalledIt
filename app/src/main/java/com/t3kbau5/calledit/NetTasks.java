package com.t3kbau5.calledit;

import android.os.Bundle;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by benwi on 2016-11-02.
 */
public class NetTasks {

    public interface RoomTaskListener{
        void roomsLoaded(List<RoomObject> rooms);
    }

    public static void loadRooms(final RoomTaskListener listener){
        NetWorker nw = new NetWorker();
        nw.loadUrl("http://queensu.evanced.info/dibsapi/rooms", new NetWorker.NetWorkerListener() {
            @Override
            public void onDataReceived(String data) {
                XmlPullParser parser = Xml.newPullParser();
                try {
                    parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                    parser.setInput(new StringReader(data));

                    List<RoomObject> rooms = new ArrayList<RoomObject>();

                    while (parser.next() != XmlPullParser.END_TAG) {
                        if (parser.getEventType() != XmlPullParser.START_TAG) {
                            continue;
                        }
                        String name = parser.getName();
                        // Starts by looking for the entry tag
                        if (name.equals("DibsRoom")) {
                            parser.require(XmlPullParser.START_TAG, null, "DibsRoom");
                            RoomObject room = new RoomObject();
                            while(parser.next() != XmlPullParser.END_TAG){
                                if (parser.getEventType() != XmlPullParser.START_TAG) {
                                    continue;
                                }

                                String roomTagName = parser.getName();
                                if(name.equals("Description")){
                                    parser.require(XmlPullParser.START_TAG, null, "Description");
                                    room.description = parser.getText();
                                    parser.require(XmlPullParser.END_TAG, null, "Description");
                                }else if(name.equals("Map")){
                                    parser.require(XmlPullParser.START_TAG, null, "Map");
                                    room.mapUrl = parser.getText();
                                    parser.require(XmlPullParser.END_TAG, null, "Map");
                                }else if(name.equals("Name")){
                                    parser.require(XmlPullParser.START_TAG, null, "Name");
                                    room.name = parser.getText();
                                    parser.require(XmlPullParser.END_TAG, null, "Name");
                                }else if(name.equals("Picture")){
                                    parser.require(XmlPullParser.START_TAG, null, "Picture");
                                    room.pictureUrl = parser.getText();
                                    parser.require(XmlPullParser.END_TAG, null, "Picture");
                                }else if(name.equals("RoomID")){
                                    parser.require(XmlPullParser.START_TAG, null, "RoomID");
                                    room.id = Integer.parseInt(parser.getText());
                                    parser.require(XmlPullParser.END_TAG, null, "RoomID");
                                }else{
                                    xmlskip(parser);
                                }
                            }
                            rooms.add(room);
                        } else {
                            xmlskip(parser);
                        }
                    }

                    listener.roomsLoaded(rooms);
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onLoadError(int code) {
                listener.roomsLoaded(null);
            }
        });
    }


    private static void xmlskip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
