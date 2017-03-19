package au.com.locator.support;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;



import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;


public class WebClient {

    private static final String URL = "https://demo-locator.azurewebsites.net/api/coordinate";
    private final ConnectivityManager connectivityManager;


    public WebClient(ConnectivityManager connectivityManager){
        this.connectivityManager = connectivityManager;
    }


    public boolean isConected(){
        NetworkInfo activeNetwork = this.connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    // TODO Add treatment for other errors like 400, 500

    public String post(JSONObject json) {

        HttpPost post = new HttpPost(URL);
        String jsonReceived = null;
        Log.i("JSON:", json.toString());
        try {
            post.setHeader("User-Agent", "ZUMO/2.0.0");
            post.setHeader("ZUMO-API-VERSION", "2.0.0");
            post.setHeader("Accept", "application/json");
            post.setHeader("Content-type", "application/json");
            //post.setHeader("Authorization", this.createAuthorizationString());
            post.setEntity(new StringEntity(json.toString()));
            DefaultHttpClient client = new DefaultHttpClient();
            HttpResponse response = client.execute(post);
            jsonReceived = EntityUtils.toString(response.getEntity());
            Log.i("Answer", jsonReceived);
        } catch (IOException i){
            Log.i("IO Error:", i.getMessage());
            i.printStackTrace();
            jsonReceived = "IO Error: "+ i.getMessage();
        }
        return jsonReceived;
    }


    /*
    private String createAuthorizationString() {
        byte[] bytes;
        String stringToCodify = this.user + ":" + this.password;
        try {
            bytes = stringToCodify.getBytes("ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            bytes = stringToCodify.getBytes();
        }
        String codifiedString = Base64.encodeToString(bytes, Base64.DEFAULT);
        codifiedString = codifiedString.trim();
        return "Basic " + codifiedString;
    }
    */

}
