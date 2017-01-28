package com.t3kbau5.calledit;

import java.io.Serializable;

/**
 * Created by benwi on 2016-11-02.
 */
public class Room implements Serializable{
    public String description = null;
    public String mapUrl = null;
    public String pictureUrl = null;
    public String name = null;
    public int id = -1;

    public boolean hasTV = false;
    public boolean hasPhone = false;
    public int size = 0;

    private boolean hasAttributes = false;


    public void determineAttributes(){
        String tdesc = description.toLowerCase();

        if(tdesc.contains("tv")) hasTV=true;
        if(tdesc.contains("phone")) hasPhone=true;

        if(tdesc.contains("small")) size=1;
        if(tdesc.contains("medium")) size=2;
        if(tdesc.contains("large")) size=3;

        hasAttributes = true;
    }

    public boolean hasAttributes(){
        return hasAttributes;
    }

}
