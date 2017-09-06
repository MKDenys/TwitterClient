package com.mkdenis.twitterclient;


import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

public class ReadResponse extends AsyncTask<Void, Void, String> {
    private HttpURLConnection httpURLConnection;
    private OnEventListener callBack;
    private Exception exception;

    public ReadResponse(HttpURLConnection httpURLConnection, OnEventListener callback) {
        this.httpURLConnection = httpURLConnection;
        this.callBack = callback;
    }

    @Override
    protected String doInBackground(Void...connection) {
        BufferedReader br = null;
        try {
            StringBuilder str = new StringBuilder();
            br = new BufferedReader(new InputStreamReader(this.httpURLConnection.getInputStream()));
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
                    Log.d("!!!!!!!!!!!!!!!", "doInBackground: " + exception.toString());
                }
            }
        }
        return "";
    }

    protected void onPostExecute(String jsonStr) {
        if (this.callBack != null) {
            if (this.exception == null) {
                this.callBack.onSuccess(jsonStr);
            } else {
                this.callBack.onFailure(this.exception);
            }
        }
    }
}
