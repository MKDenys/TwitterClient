package com.mkdenis.twitterclient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class TwitterRestAPIManager {

    private final static String TWITTER_TOKEN_URL = "https://api.twitter.com/oauth2/token";
    private final static String TWITTER_USER_TIMELINE_URL = "https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=";
    private final static String CONSUMER_KEY = "i837a0S8sWQ6fvNpwQfBbXl8u";
    private final static String CONSUMER_SECRET = "MgZQFLFY6z04XFOvW0dl8cw1snSMAOInrOshBJ2sBuRnaJc0UE";


    public static String getUserTimeline(String userName) throws Exception{
        HttpURLConnection connection = null;
        String jsonResponse;
        URL url = new URL(TWITTER_USER_TIMELINE_URL + userName);
        connection =  (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setRequestProperty("Host", "api.twitter.com");
        connection.setRequestProperty("User-Agent", "MKTwitterCkient");
        connection.setRequestProperty("Authorization", "Bearer " + PreferenceSettingsManager.getString("auth_token"));
        connection.setRequestProperty("Accept-Encoding", "gzip");
        return jsonResponse = readResponse(connection);
    }

    private static boolean writeRequest(HttpURLConnection connection, String textBody) throws IOException {
        BufferedWriter wr = null;
        try {
            wr = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            wr.write(textBody);
            wr.flush();

            return true;
        }
        catch (IOException e) {
            return false;
        }
        finally {
            if (wr != null) {
                wr.close();
            }
        }
    }

    private static String readResponse(HttpURLConnection connection) throws IOException {
        BufferedReader br = null;
        try {
            StringBuilder str = new StringBuilder();

            br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = "";
            while((line = br.readLine()) != null) {
                str.append(line + System.getProperty("line.separator"));
            }
            return str.toString();
        }
        catch (IOException e) { return ""; }
        finally {
            if (br != null) {
                br.close();
            }
        }
    }
}
