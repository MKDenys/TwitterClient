package com.mkdenis.twitterclient;

import android.util.Base64;
import android.util.Log;

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
    private final static String KEY_OAUTH_CONSUMER_KEY = "oauth_consumer_key";
    private final static String KEY_OAUTH_TOKEN = "oauth_token";
    private final static String OAUTH_TOKEN = PreferenceSettingsManager.getOAuthToken();
    private final static String KEY_OAUTH_SIGNATURE_METHOD = "oauth_signature_method";
    private final static String OAUTH_SIGNATURE_METHOD = "HMAC-SHA1";
    private final static String KEY_OAUTH_TIMESTAMP = "oauth_timestamp";
    private final static String KEY_OAUTH_NONCE = "oauth_nonce";
    private final static String KEY_OAUTH_VERSION = "oauth_version";
    private final static String OAUTH_VERSION = "1.0";
    private final static String KEY_OAUTH_SIGNATURE = "oauth_signature";
    private final static String HMAC_SHA1_ALGORITHM = "HmacSHA1";
    private final static String OAUTH_SECRET = PreferenceSettingsManager.getOAuthSecret();
    private static final String KEY_HOST = "Host";
    private static final String HOST = "api.twitter.com";
    private static final String KEY_USER_AGENT = "User-Agent";
    private static final String USER_AGENT = "MKTwitterClient";
    private static final String KEY_ACCEPT_ENCODING = "Accept-Encoding";
    private static final String ENCODING = "UTF-8";
    private static final String KEY_CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_TYPE = "application/x-www-form-urlencoded";
    private static final String KEY_AUTHORIZATION = "Authorization";
    private final static int NONCE_LENGTH = 11;
    private static TwitterRestAPIManager instance;
    
    public static TwitterRestAPIManager getInstance(){
        if (instance == null) {
            instance = new TwitterRestAPIManager();
        }
        return instance;
    }

    private TwitterRestAPIManager() {}

    private void setRequestProperty(HttpURLConnection connection, String httpMethod, String authorizationValue){
        try {
            connection.setRequestMethod(httpMethod);
            connection.setRequestProperty(KEY_HOST, HOST);
            connection.setRequestProperty(KEY_USER_AGENT, USER_AGENT);
            connection.setRequestProperty(KEY_ACCEPT_ENCODING, ENCODING);
            connection.setRequestProperty(KEY_CONTENT_TYPE, CONTENT_TYPE);
            connection.setRequestProperty(KEY_AUTHORIZATION, authorizationValue);
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
    }

    public HttpURLConnection httpRequestTwitterAPI(String httpMethod, String urlAPI) throws Exception{
        HttpURLConnection connection;
        URL url = new URL(urlAPI);
        connection =  (HttpURLConnection) url.openConnection();
        String oauth = getAuthorizationParam(httpMethod, urlAPI);
        setRequestProperty(connection, httpMethod, oauth);
        return connection;
    }

    private String getAuthorizationParam(String httpMethod, String url) {
        String oauthTimestamp = String.valueOf(System.currentTimeMillis() / 1000);
        byte[] nonce = new byte[NONCE_LENGTH];
        new Random().nextBytes(nonce);
        String oauthNonce =  Base64.encodeToString(nonce, Base64.URL_SAFE).substring(0, NONCE_LENGTH);
        String oauthSignature = creatingSignatures(oauthTimestamp, oauthNonce, httpMethod, url);
        String result = "OAuth ";
        result += KEY_OAUTH_CONSUMER_KEY + "=\"" + CONSUMER_KEY + "\","
                + KEY_OAUTH_TOKEN + "=\"" + OAUTH_TOKEN + "\","
                + KEY_OAUTH_SIGNATURE_METHOD + "=\"" + OAUTH_SIGNATURE_METHOD + "\","
                + KEY_OAUTH_TIMESTAMP + "=\"" + oauthTimestamp + "\","
                + KEY_OAUTH_NONCE + "=\"" + oauthNonce + "\","
                + KEY_OAUTH_VERSION + "=\"" + OAUTH_VERSION + "\","
                + KEY_OAUTH_SIGNATURE + "=\"" + oauthSignature + "\"";
        return result;
    }

    private String percentEncode(String value) {
        String encoded = "";
        try {
            encoded = URLEncoder.encode(value, ENCODING);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encoded;
    }

    private String creatingSignatures(String oauthTimestamp, String oauthNonce, String httpMethod, String url){
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
        String paramStr =  percentEncode(KEY_OAUTH_CONSUMER_KEY) + "=" + percentEncode(CONSUMER_KEY) + "&"
                + percentEncode(KEY_OAUTH_NONCE) + "=" + percentEncode(oauthNonce) + "&"
                + percentEncode(KEY_OAUTH_SIGNATURE_METHOD) + "=" + percentEncode(OAUTH_SIGNATURE_METHOD) + "&"
                + percentEncode(KEY_OAUTH_TIMESTAMP) + "=" + percentEncode(oauthTimestamp) + "&"
                + percentEncode(KEY_OAUTH_TOKEN) + "=" + percentEncode(OAUTH_TOKEN) + "&"
                + percentEncode(KEY_OAUTH_VERSION) + "=" + percentEncode(OAUTH_VERSION) + "&"
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
        String persentEncodedBaseUrl = percentEncode(baseUrl);
        String persentEncodedParamStr = percentEncode(paramStr);
        String signatureBaseString = httpMethod.toUpperCase() + "&" + persentEncodedBaseUrl
                + "&" + persentEncodedParamStr;
        String persentConsumerSecret = percentEncode(CONSUMER_SECRET);
        String persentEncodedOAuthSecret = percentEncode(OAUTH_SECRET);
        String signingKey = persentConsumerSecret + "&" + persentEncodedOAuthSecret;
        String oauthSignature = "";
        try {
            oauthSignature = Base64.encodeToString(calculateRFC2104HMAC(signatureBaseString, signingKey),
                    Base64.NO_WRAP);
            oauthSignature = percentEncode(oauthSignature);
        }
        catch (Exception e){
            Log.d("!!!!!!!!!!!!!!!", "creatingSignatures: " + e.toString());
        }
        return oauthSignature;
    }

    private byte[] calculateRFC2104HMAC(String data, String key) throws Exception {
        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);
        Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
        mac.init(signingKey);
        return mac.doFinal(data.getBytes());
    }
}


