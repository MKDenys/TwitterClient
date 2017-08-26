package com.mkdenis.twitterclient;

import android.os.AsyncTask;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

public class WriteRequest extends AsyncTask<Void, Void, Boolean> {
    private HttpURLConnection httpURLConnection;
    private String textBody;
    private OnEventListener callBack;
    private Exception exception;
    private int eventType;

    public WriteRequest(HttpURLConnection httpURLConnection, String textBody, OnEventListener callback, int eventType) {
        this.httpURLConnection = httpURLConnection;
        this.textBody = textBody;
        this.callBack = callback;
        this.eventType = eventType;
    }

    @Override
    protected Boolean doInBackground(Void...connection) {
        BufferedWriter wr = null;
        try {
            wr = new BufferedWriter(new OutputStreamWriter(httpURLConnection.getOutputStream()));
            wr.write(textBody);
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
                    this.exception = exception;
                }
            }
        }
        return false;
    }

    protected void onPostExecute(Boolean writeRequest) {
        if (callBack != null) {
            if (exception == null) {
                callBack.onSuccess(writeRequest, eventType);
            } else {
                callBack.onFailure(exception);
            }
        }
    }
}
