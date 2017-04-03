package com.t3kbau5.calledit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class BCR extends BroadcastReceiver {

    private static final String TAG = "C!BCR";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");
        String action = intent.getAction();
        if (action.equalsIgnoreCase("android.provider.Telephony.SMS_RECEIVED")) {
            Log.d(TAG, "smsReceived");
            Bundle pdusBundle = intent.getExtras();
            Object[] pdus = (Object[]) pdusBundle.get("pdus");
            SmsMessage message = SmsMessage.createFromPdu((byte[]) pdus[0]);


            String msgBody = message.getMessageBody();
            String sender = message.getOriginatingAddress().replace("+", "").replace("(","").replace(")","").replace("-","").replace(" ", "");
            String[] words = msgBody.split(" ");

            Log.d(TAG, "sender: " + sender);
            if(sender.equals("3473826231")){ //dibs phone number. add this as a value somewhere?
                Log.d(TAG, "sender is dibs");
                if (msgBody.isEmpty() || msgBody.equals("") || words.length < 1) return; //error prevention
                if(msgBody.toLowerCase().contains("d!bs!  your request for")){ //yes, there is a double-space in the text for some reason...
                    Log.d(TAG, "dibs reservation text");
                    String roomS = ""; //room number, eg. 111 or 221
                    String dateS ="",timeS ="";
                    for(int i=0; i<words.length; i++){
                        if(words[i].matches("\\d{1,2}\\/\\d{1,2}\\/\\d{4}")){
                            String[] tmp = words[i].split("/");
                            dateS = (tmp[0].length()==1?"0"+tmp[0]:tmp[0]) + (tmp[1].length()==1?"0"+tmp[1]:tmp[1]) + tmp[2];
                            dateS = dateS.substring(4) + dateS.substring(0, 2) + dateS.substring(2,4);
                        }else if(words[i].matches("\\d{1,2}:\\d{2}")){
                            timeS = words[i] +" " + words[i+1];
                        }else if(words[i].toLowerCase().contains("bmh")){
                            roomS = words[i+1];
                        }
                    }
                    if(!timeS.equals("") && !dateS.equals("") && !roomS.equals("")){
                        int time = Integer.parseInt(timeS.substring(0, timeS.indexOf(" ")).replace(":", "")) + (timeS.contains("PM")?1200:0);
                        int date = Integer.parseInt(dateS);
                        int room = Integer.parseInt(roomS);

                        PrimaryDatabase pdb = new PrimaryDatabase(context);
                        pdb.open();
                        pdb.addReservation(room, date, time, 0); //duration is 0 because the text doesn't include it
                        pdb.close();
                        Log.d(TAG, "Reservation parsed and saved!");
                    }else{
                        Log.d(TAG, "Reservation parsing failed!");
                    }
                }
            }
        }
    }
}
