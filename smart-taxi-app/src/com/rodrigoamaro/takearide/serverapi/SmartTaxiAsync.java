
package com.rodrigoamaro.takearide.serverapi;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.rodrigoamaro.takearide.AuthResponse;
import com.rodrigoamaro.takearide.GsonRequest;

public class SmartTaxiAsync implements LocationResources, TaxiResources {
    private static SmartTaxiAsync INSTANCE = null;

    private static Gson gson;

    private static RequestQueue mRequestQueue;

    private static final String BASE_URL = "http://192.168.0.103:8080/api/v1/";

    protected static final String TAG = "SmartTaxiAsync";
    public static String apiKey = null;
    public static String userName = null;

    private SmartTaxiAsync() {
    }

    // creador sincronizado para protegerse de posibles problemas multi-hilo
    // otra prueba para evitar instanciaci—n mœltiple
    private synchronized static void createInstance(Context c) {
        if (INSTANCE == null) {
            INSTANCE = new SmartTaxiAsync();
            gson = new GsonBuilder().create();
            mRequestQueue = Volley.newRequestQueue(c);

        }
    }

    public static SmartTaxiAsync getInstance(Context c) {
        createInstance(c);
        return INSTANCE;
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        
        String url = BASE_URL + relativeUrl;
        Log.d(TAG, "url: " + url);
        return url;
    }

    public void doLogin(final String username, String password, String devId, String regId, final SmartTaxiListener listener) {
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("username", username!= null?username:"");
            jsonRequest.put("password", password != null? password : "");
            jsonRequest.put("dev_id", devId != null? devId : "");
            jsonRequest.put("reg_id", regId != null? regId : "");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d(TAG, "request data: " + jsonRequest.toString());
        GsonRequest<AuthResponse> jr = new GsonRequest<AuthResponse>(
                Request.Method.POST,
                getAbsoluteUrl("account/login/"),
                AuthResponse.class,
                jsonRequest,
                new Response.Listener<AuthResponse>() {

                    @Override
                    public void onResponse(AuthResponse response) {
                        Log.d(TAG, "response " + response.toString());
                        if (response.getSuccess()) {
                            String apiKey = response.getApiKey();
                            SmartTaxiAsync.apiKey = apiKey;
                            SmartTaxiAsync.userName = username;
                            listener.gotLoginSucess();
                        } else {
                            Log.d(TAG, response.getReason());
                            listener.onException();
                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try{
                            String a = new String(error.networkResponse.data);
                            Log.d(TAG, "error " + a);
                        }catch(Exception e){
                            
                        }
                        Log.d(TAG, "error " + error.getLocalizedMessage());

                    }
                });
        
        mRequestQueue.add(jr);

    }
    
    public void addLocation(LocationModel l){
        JSONObject locJson = new JSONObject();
        try {
            locJson = new JSONObject();
            locJson.put("latitude", ""+l.latitude);
            locJson.put("longitude", ""+l.longitude);
            locJson.put("speed", ""+l.speed);
            locJson.put("timestamp", ""+(l.timestamp/1000));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d(TAG, "" + locJson);
        GsonRequest<Void> jr = new GsonRequest<Void>(
                Request.Method.POST,
                getAbsoluteUrl("location/"),
                Void.class,
                locJson,
                new Response.Listener<Void>() {

                    @Override
                    public void onResponse(Void response) {
                        Log.d(TAG, "response " + response.toString());
                        
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(error.networkResponse != null && error.networkResponse.data != null){
                            String a = new String(error.networkResponse.data);
                            Log.d(TAG, "onErrorResponse " + a);
                        }
                        
                        
                    }
                });
        jr.setHeader("Authorization", "ApiKey "+SmartTaxiAsync.userName + ":"+SmartTaxiAsync.apiKey);
        mRequestQueue.add(jr);
    }

    @Override
    public void addLocation(List<LocationModel> locations) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public StartTaxiResponse<LocationModel> getLocations() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void changeStatus(int status) {
        JSONObject locJson = new JSONObject();
        try {
            locJson = new JSONObject();
            locJson.put("status", ""+status);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d(TAG, "" + locJson);
        GsonRequest<Void> jr = new GsonRequest<Void>(
                Request.Method.PUT,
                getAbsoluteUrl("location/"),
                Void.class,
                locJson,
                new Response.Listener<Void>() {

                    @Override
                    public void onResponse(Void response) {
                        Log.d(TAG, "response " + response.toString());
                        
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(error.networkResponse != null && error.networkResponse.data != null){
                            String a = new String(error.networkResponse.data);
                            Log.d(TAG, "onErrorResponse " + a);
                        }
                        
                        
                    }
                });
        jr.setHeader("Authorization", "ApiKey "+SmartTaxiAsync.userName + ":"+SmartTaxiAsync.apiKey);
        mRequestQueue.add(jr);
    }
}
