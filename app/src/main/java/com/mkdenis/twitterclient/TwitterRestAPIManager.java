package com.mkdenis.twitterclient;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class TwitterRestAPIManager {

    private final String CONSUMER_KEY = "i837a0S8sWQ6fvNpwQfBbXl8u";
    private final String CONSUMER_SECRET = "MgZQFLFY6z04XFOvW0dl8cw1snSMAOInrOshBJ2sBuRnaJc0UE";
    private final String OAUTH_CONSUMER_KEY = "oauth_consumer_key";
    private final String OAUTH_TOKEN = "oauth_token";
    private final String OAUTH_SIGNATURE_METHOD = "oauth_signature_method";
    private final String OAUTH_TIMESTAMP = "oauth_timestamp";
    private final String OAUTH_NONCE = "oauth_nonce";
    private final String OAUTH_VERSION = "oauth_version";
    private final String OAUTH_SIGNATURE = "oauth_signature";
    private static TwitterRestAPIManager instance;
    
    public static TwitterRestAPIManager getInstance(){
        if (instance == null) {
            instance = new TwitterRestAPIManager();
        }
        return instance;
    }

    private TwitterRestAPIManager() {};

    public ArrayList<Tweet> getTimeline(String url){
        ArrayList<Tweet> tweets = new ArrayList<Tweet>();
        try {
            tweets = JSONParser.parseTimeline(httpGetTwitterAPI(url));
        } catch (Exception e){
            Log.d("!!!!!!!!!!!!!!!", "getTimeline: " + e.toString());
        }
        return tweets;
    }

    public TwitterUser getUser(String userName){
        String url = "https://api.twitter.com/1.1/users/lookup.json?screen_name=";
        TwitterUser user = null;
        try {
            String response = httpGetTwitterAPI(url + userName);
            user = JSONParser.parseTwitterUser(response);
        } catch (Exception e){
            Log.d("!!!!!!!!!!!!!!!", "getUser: " + e.toString());
        }
        return user;
    }

    private String httpGetTwitterAPI(String urlAPI) throws Exception{
        HttpURLConnection connection;
        URL url = new URL(urlAPI);
        connection =  (HttpURLConnection) url.openConnection();
        final String HTTP_METHOD = "GET";
    //    getApplicationOnlyToken(CONSUMER_KEY, CONSUMER_SECRET);
    //    String bearer = "Bearer " + PreferenceSettingsManager.getString("access_token");
        String oauth = getAuthorizationParam(HTTP_METHOD, urlAPI); //user authentication

        connection.setRequestMethod(HTTP_METHOD);
        connection.setRequestProperty("Host", "api.twitter.com");
        connection.setRequestProperty("User-Agent", "MKTwitterClient");
        connection.setRequestProperty("Accept-Encoding", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Authorization", oauth);
        ReadResponse readResponse = new ReadResponse(connection);
        readResponse.execute();
        String jsonResponse = readResponse.get();
        return jsonResponse;
    }

    private void getApplicationOnlyToken(String cosumerKey, String consumerSecret) throws Exception {
        URL url = new URL("https://api.twitter.com/oauth2/token");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        String urlApiKey = URLEncoder.encode(cosumerKey, "UTF-8");
        String urlApiSecret = URLEncoder.encode(consumerSecret, "UTF-8");
        String combined = urlApiKey + ":" + urlApiSecret;
        String base64Encoded = Base64.encodeToString(combined.getBytes(), Base64.NO_WRAP);

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Host", "api.twitter.com");
        connection.setRequestProperty("User-Agent", "MKTwitterClient");
        connection.setRequestProperty("Authorization", "Basic " + base64Encoded);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        connection.setRequestProperty("Content-Length", "29");
        connection.setRequestProperty("Accept-Encoding", "UTF-8");
        WriteRequest writeRequest = new WriteRequest(connection, "grant_type=client_credentials");
        writeRequest.execute();
        if (writeRequest.get()){
            ReadResponse readResponse = new ReadResponse(connection);
            readResponse.execute();
            String accessToken = JSONParser.parseAccessToken(readResponse.get());
            PreferenceSettingsManager.addString("access_token", accessToken);
        }
    }

    private String getAuthorizationParam(String httpMethod, String url) {
        String result = "OAuth ";
        String oauthConsumerKey = CONSUMER_KEY;
        String oauthToken = PreferenceSettingsManager.getString("oauth_token");
        String oauthSignatureMethod = "HMAC-SHA1";
        String oauthTimestamp = String.valueOf(System.currentTimeMillis() / 1000);
        byte[] nonce = new byte[8];
        new Random().nextBytes(nonce);
        String oauthNonce =  Base64.encodeToString(nonce, Base64.URL_SAFE).substring(0, 11);
        String oauthVersion = "1.0";
        String oauthSignature = creatingSignatures(oauthSignatureMethod, oauthTimestamp, oauthNonce,
                oauthVersion, httpMethod, url);
        result += OAUTH_CONSUMER_KEY + "=\"" + oauthConsumerKey + "\","
                + OAUTH_TOKEN + "=\"" + oauthToken + "\","
                + OAUTH_SIGNATURE_METHOD + "=\"" + oauthSignatureMethod + "\","
                + OAUTH_TIMESTAMP + "=\"" + oauthTimestamp + "\","
                + OAUTH_NONCE + "=\"" + oauthNonce + "\","
                + OAUTH_VERSION + "=\"" + oauthVersion + "\","
                + OAUTH_SIGNATURE + "=\"" + oauthSignature + "\"";
        return result;
    }

    private String percentEncode(String value) {
        String encoded = "";
        try {
            encoded = URLEncoder.encode(value, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
    /*    String sb = "";
        char focus;
        for (int i = 0; i < encoded.length(); i++) {
            focus = encoded.charAt(i);
            if (focus == '*') {
                sb += "%2A";
            } else if (focus == '+') {
                sb += "%20";
            } else if (focus == '%' && i + 1 < encoded.length()
                    && encoded.charAt(i + 1) == '7' && encoded.charAt(i + 2) == 'E') {
                sb += '~';
                i += 2;
            } else {
                sb += focus;
            }
        }*/
        return encoded;
    }

    private String creatingSignatures(String oauthSignatureMethod, String oauthTimestamp, String oauthNonce,
                                      String oauthVersion, String httpMethod, String url){

    /*algorithm:
    https://dev.twitter.com/oauth/overview/creating-signatures */
    //Collecting parameters
        String oauthSignature = "";
        String oauthConsumerKey = CONSUMER_KEY;
        String oauthToken = PreferenceSettingsManager.getString("oauth_token");
        oauthConsumerKey = percentEncode(OAUTH_CONSUMER_KEY) +
                "=" + percentEncode(oauthConsumerKey);
        oauthNonce = percentEncode(OAUTH_NONCE) +
                "=" + percentEncode(oauthNonce);
        oauthSignatureMethod = percentEncode(OAUTH_SIGNATURE_METHOD) +
                "=" + percentEncode(oauthSignatureMethod);
        oauthTimestamp = percentEncode(OAUTH_TIMESTAMP) +
                "=" + percentEncode(oauthTimestamp);
        oauthToken = percentEncode(OAUTH_TOKEN) +
                "=" + percentEncode(oauthToken);
        oauthVersion = percentEncode(OAUTH_VERSION) +
                "=" + percentEncode(oauthVersion);
    //Sort parameters by alphabetical
        String baseUrl;
        String paramUrl = "";
        if (url.indexOf("?") > 0) {
            baseUrl = url.substring(0, url.lastIndexOf("?"));
            paramUrl = url.substring(url.lastIndexOf("?") + 1, url.length());
        }
        else {
            baseUrl = url;
            paramUrl = "";
        }
        String paramStr =  oauthConsumerKey + "&"
                + oauthNonce + "&"
                + oauthSignatureMethod + "&"
                + oauthTimestamp + "&"
                + oauthToken + "&"
                + oauthVersion + "&"
                + paramUrl;
        String[] param = paramStr.split("&");
        Arrays.sort(param);
        paramStr = "";
        for (int i = 0; i < param.length; i++) {
            if (i != param.length - 1)
                paramStr += param[i] + "&";
            else
                paramStr += param[i];
        }
    //Creating the signature base string
        String persentEncodedBaseUrl = percentEncode(baseUrl);
        String persentEncodedParamStr = percentEncode(paramStr);
        String signatureBaseString = httpMethod.toUpperCase() + "&" + persentEncodedBaseUrl
                + "&" + persentEncodedParamStr;
    //Getting a signing key
        String persentConsumerSecret = percentEncode(CONSUMER_SECRET);
        String persentEncodedOAuthSecret = percentEncode(PreferenceSettingsManager.getString("oauth_secret"));
        String signingKey = persentConsumerSecret + "&" + persentEncodedOAuthSecret;
    //Calculating the signature
        oauthSignature = Base64.encodeToString(calculateRFC2104HMAC(signatureBaseString, signingKey), Base64.NO_WRAP);
        oauthSignature = percentEncode(oauthSignature);
        return oauthSignature;
    }

    private final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
    private byte[] calculateRFC2104HMAC(String data, String key) {
        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);
        Mac mac = null;
        try {
            mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
            mac.init(signingKey);
        }
        catch (Exception e){
            Log.d("!!!!!!!!!!!!!!!", "calculateRFC2104HMAC: " + e.toString());
        }
        return mac.doFinal(data.getBytes());
    }

    public class ReadResponse extends AsyncTask<Void, Void, String> {
        private HttpURLConnection httpURLConnection;

        public ReadResponse(HttpURLConnection httpURLConnection) {
            this.httpURLConnection = httpURLConnection;
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
            catch (IOException e) {
                return "";
            }
            finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        return "";
                    }
                }
            }
        }

        protected void onPostExecute(String jsonStr) {
            // TODO: check this.exception
            // TODO: do something with the feed
        }
    }

    public class WriteRequest extends AsyncTask<Void, Void, Boolean> {
        private HttpURLConnection httpURLConnection;
        private String textBody;

        public WriteRequest(HttpURLConnection httpURLConnection, String textBody) {
            this.httpURLConnection = httpURLConnection;
            this.textBody = textBody;
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
            catch (IOException e) {
                return false;
            }
            finally {
                if (wr != null) {
                    try {
                        wr.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        protected void onPostExecute(Boolean writeRequest) {
            // TODO: check this.exception
            // TODO: do something with the feed
        }
    }
}


