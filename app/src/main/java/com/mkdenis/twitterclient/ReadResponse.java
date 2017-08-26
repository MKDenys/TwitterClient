package com.mkdenis.twitterclient;


import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

public class ReadResponse extends AsyncTask<Void, Void, String> {
    private HttpURLConnection httpURLConnection;
    private OnEventListener callBack;
    private Exception exception;
    private int eventType;

    public ReadResponse(HttpURLConnection httpURLConnection, OnEventListener callback, int eventType) {
        this.httpURLConnection = httpURLConnection;
        this.callBack = callback;
        this.eventType = eventType;
    }

    @Override
    protected String doInBackground(Void...connection) {
        BufferedReader br = null;
        try {
            StringBuilder str = new StringBuilder();
            br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String line = "";
            while((line = br.readLine()) != null) {
                str.append(line + System.getProperty("line.separator"));
            }
            return str.toString();
        }
        catch (Exception exception) {
            this.exception = exception;
        }
        finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception exception) {
                    this.exception = exception;
                }
            }
        }
        return "";
    }

    protected void onPostExecute(String jsonStr) {
        if (callBack != null) {
            if (exception == null) {
                callBack.onSuccess(jsonStr, eventType);
            } else {
                callBack.onFailure(exception);
            }
        }
    }
}
