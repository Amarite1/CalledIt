package com.t3kbau5.calledit;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by benwi on 2016-11-02.
 */
public class NetWorker extends AsyncTask<String, Void, Void> {

    NetWorkerListener nwl;
    String accept;

    public interface NetWorkerListener{
        void onDataReceived(String data);
        void onLoadError(int code);
    }

    public void loadUrl(String url, NetWorkerListener listener){
        nwl = listener;
        accept = "*";
        this.execute(url);
    }

    public void loadUrl(String url, String accept, NetWorkerListener listener){
        nwl = listener;
        this.accept = accept;
        this.execute(url);
    }

    @Override
    protected Void doInBackground(String[] params) {
        try {
            URL url = new URL(params[0]);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("Accept", accept);
            con.setReadTimeout(15000);
            con.setConnectTimeout(15000);
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.connect();
            int response = con.getResponseCode();
            Log.d("Net", Integer.toString(response));
            if(response >= 300){
                con.disconnect();
                nwl.onLoadError(response);
                return null;
            }
            InputStreamReader is = new InputStreamReader(con.getInputStream());
            BufferedReader reader = new BufferedReader(is);
            StringBuilder builder = new StringBuilder();
            for (String line = null; (line = reader.readLine()) != null; ) {
                builder.append(line);
            }
            con.disconnect();
            reader.close();
            is.close();
            nwl.onDataReceived(builder.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
