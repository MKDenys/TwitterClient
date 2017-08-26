package com.mkdenis.twitterclient;

import android.util.Base64;
import android.util.Log;
import android.util.Pair;

import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class TwitterRestAPIManager {

    private final static String CONSUMER_KEY = "i837a0S8sWQ6fvNpwQfBbXl8u";
    private final static String CONSUMER_SECRET = "MgZQFLFY6z04XFOvW0dl8cw1snSMAOInrOshBJ2sBuRnaJc0UE";
    private final static String OAUTH_CONSUMER_KEY = "oauth_consumer_key";
    private final static String OAUTH_TOKEN = "oauth_token";
    private final static String OAUTH_SIGNATURE_METHOD = "oauth_signature_method";
    private final static String OAUTH_TIMESTAMP = "oauth_timestamp";
    private final static String OAUTH_NONCE = "oauth_nonce";
    private final static String OAUTH_VERSION = "oauth_version";
    private final static String OAUTH_SIGNATURE = "oauth_signature";
    private final static String HMAC_SHA1_ALGORITHM = "HmacSHA1";
    private static TwitterRestAPIManager instance;
    
    public static TwitterRestAPIManager getInstance(){
        if (instance == null) {
            instance = new TwitterRestAPIManager();
        }
        return instance;
    }

    private TwitterRestAPIManager() {}

    private void setRequestProperty(HttpURLConnection connection, String httpMethod, String authorizationValue){
        Pair<String, String> host = new Pair("Host", "api.twitter.com");
        Pair<String, String> userAgent = new Pair("User-Agent", "MKTwitterClient");
        Pair<String, String> acceptEncoding = new Pair("Accept-Encoding", "UTF-8");
        Pair<String, String> contentType = new Pair("Content-Type", "application/x-www-form-urlencoded");
        Pair<String, String> authorization = new Pair("Authorization", authorizationValue);
        try {
            connection.setRequestMethod(httpMethod);
            connection.setRequestProperty(host.first, host.second);
            connection.setRequestProperty(userAgent.first, userAgent.second);
            connection.setRequestProperty(acceptEncoding.first, acceptEncoding.second);
            connection.setRequestProperty(contentType.first, contentType.second);
            connection.setRequestProperty(authorization.first, authorization.second);
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
    }

    public HttpURLConnection httpGetTwitterAPI(String urlAPI) throws Exception{
        HttpURLConnection connection;
        URL url = new URL(urlAPI);
        connection =  (HttpURLConnection) url.openConnection();
        final String HTTP_METHOD = "GET";
        String oauth = getAuthorizationParam(HTTP_METHOD, urlAPI);
     //   getApplicationOnlyToken(CONSUMER_KEY, CONSUMER_SECRET);
     //   String oauth = "Bearer " + PreferenceSettingsManager.getString("access_token");
        setRequestProperty(connection, HTTP_METHOD, oauth);
        return connection;
    }

    public void getApplicationOnlyToken(String cosumerKey, String consumerSecret) {
        URL url;
        HttpURLConnection connection = null;
        try {
            url = new URL(TwitterAPIURL.APPLICATION_ONLY_OAUTH);
            connection = (HttpURLConnection) url.openConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String urlApiKey = percentEncode(cosumerKey);
        String urlApiSecret = percentEncode(consumerSecret);
        String combined = urlApiKey + ":" + urlApiSecret;
        String base64Encoded = Base64.encodeToString(combined.getBytes(), Base64.NO_WRAP);
        String authorizationValue = "Basic " + base64Encoded;
        String httpMethod = "POST";
        setRequestProperty(connection, httpMethod, authorizationValue);
        final HttpURLConnection con = connection;
        String body = "grant_type=client_credentials";
        WriteRequest writeRequest = new WriteRequest(con, body, new OnEventListener() {
            @Override
            public void onSuccess(Object object, int eventType) {
                if ((boolean) object){
                    ReadResponse readResponse = new ReadResponse(con, new OnEventListener() {
                        @Override
                        public void onSuccess(Object object, int eventType) {
                            String accessToken = JSONParser.parseAccessToken((String) object);
                            PreferenceSettingsManager.addString("access_token", accessToken);
                        }

                        @Override
                        public void onFailure(Exception e) {
                        }
                    }, OnEventListener.LOAD_ACCESS_TOKEN);
                    readResponse.execute();
                }
            }
            @Override
            public void onFailure(Exception e) {
            }
        }, OnEventListener.LOAD_ACCESS_TOKEN);
        writeRequest.execute();
    }

    private String getAuthorizationParam(String httpMethod, String url) {
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
        String result = "OAuth ";
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
        return encoded;
    }

    private String creatingSignatures(String oauthSignatureMethod, String oauthTimestamp, String oauthNonce,
                                      String oauthVersion, String httpMethod, String url){
    /*algorithm:
    https://dev.twitter.com/oauth/overview/creating-signatures */
    //Collecting and sort parameters by alphabetical
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
        String oauthToken = PreferenceSettingsManager.getString("oauth_token");
        String paramStr =  percentEncode(OAUTH_CONSUMER_KEY) + "=" + percentEncode(CONSUMER_KEY) + "&"
                + percentEncode(OAUTH_NONCE) + "=" + percentEncode(oauthNonce) + "&"
                + percentEncode(OAUTH_SIGNATURE_METHOD) + "=" + percentEncode(oauthSignatureMethod) + "&"
                + percentEncode(OAUTH_TIMESTAMP) + "=" + percentEncode(oauthTimestamp) + "&"
                + percentEncode(OAUTH_TOKEN) + "=" + percentEncode(oauthToken) + "&"
                + percentEncode(OAUTH_VERSION) + "=" + percentEncode(oauthVersion) + "&"
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
        String oauthSignature = Base64.encodeToString(calculateRFC2104HMAC(signatureBaseString, signingKey), Base64.NO_WRAP);
        oauthSignature = percentEncode(oauthSignature);
        return oauthSignature;
    }

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
}


