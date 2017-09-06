package com.mkdenis.twitterclient;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

public class WriteRequest extends AsyncTask<Void, Void, Boolean> {
    private HttpURLConnection httpURLConnection;
    private String textBody;
    private OnEventListener callBack;
    private Exception exception;

    public WriteRequest(HttpURLConnection httpURLConnection, String textBody, OnEventListener callback) {
        this.httpURLConnection = httpURLConnection;
        this.textBody = textBody;
        this.callBack = callback;
    }

    @Override
    protected Boolean doInBackground(Void...connection) {
        BufferedWriter wr = null;
        try {
            wr = new BufferedWriter(new OutputStreamWriter(this.httpURLConnection.getOutputStream()));
            wr.write(this.textBody);
            wr.flush();
            return true;
        }
        catch (Exception exception) {
            this.exception = exception;
        }
        finally {
            if (wr != null) {
                try {
                    wr.close();
                } catch (Exception exception) {
                    Log.d("!!!!!!!!!!!!!!!", "doInBackground: " + exception);
                }
            }
        }
        return false;
    }

    protected void onPostExecute(Boolean writeRequest) {
        if (this.callBack != null) {
            if (exception == null) {
                this.callBack.onSuccess(writeRequest);
            } else {
                this.callBack.onFailure(this.exception);
            }
        }
    }
}
