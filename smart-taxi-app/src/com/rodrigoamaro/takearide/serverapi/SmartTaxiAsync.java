
package com.rodrigoamaro.takearide.serverapi;

import java.lang.reflect.Type;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Base64;
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
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.rodrigoamaro.takearide.serverapi.models.NotificationModel;
import com.rodrigoamaro.takearide.serverapi.models.TokenResponse;
import com.rodrigoamaro.takearide.serverapi.models.LocationModel;
import com.rodrigoamaro.takearide.serverapi.models.TastypieResponse;
import com.rodrigoamaro.takearide.serverapi.models.TaxiModel;
import com.rodrigoamaro.takearide.serverapi.resources.LocationResources;
import com.rodrigoamaro.takearide.serverapi.resources.NotificationResources;
import com.rodrigoamaro.takearide.serverapi.resources.TaxiResources;

public class SmartTaxiAsync implements LocationResources, TaxiResources, NotificationResources {
    private static SmartTaxiAsync INSTANCE = null;
    private static RequestQueue mRequestQueue;
    private static final String DEV_URL = "http://192.168.0.103:8080/api/v1/";
    private static final String PROD_URL = "http://smart-taxi.herokuapp.com/api/v1/";
    private static final String BASE_URL = PROD_URL;
    protected static final String TAG = "SmartTaxiAsync";
    public static String apiKey = "3f474d91e206165791ded130564a61c8d07bcd3c";
    public static String userName = "johndoe";
    public static TaxiModel taxi = null;

    private SmartTaxiAsync() {
    }

    private synchronized static void createInstance(Context c) {
        if (INSTANCE == null) {
            INSTANCE = new SmartTaxiAsync();
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

    public void doLogin(final String username, String password, final SmartTaxiResponseListener listener) {
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("username", username != null ? username : "");
            jsonRequest.put("password", password != null ? password : "");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d(TAG, "request data: " + jsonRequest.toString());
        GsonRequest<TokenResponse> jr = new GsonRequest<TokenResponse>(
                Request.Method.GET,
                getAbsoluteUrl("token/auth/"),
                TokenResponse.class,
                new Response.Listener<TokenResponse>() {

                    @Override
                    public void onResponse(TokenResponse response) {
                        SmartTaxiAsync.apiKey = response.key;
                        SmartTaxiAsync.userName = username;
                        listener.gotLoginSuccess(response);

                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onError(error);

                    }
                });

        setBasicAuthHeader(jr, username, password);
        mRequestQueue.add(jr);

    }

    private String makeAuthBasic(String user, String pass) {
        String authString = user + ":" + pass;
        Log.d(TAG, authString);
        String authEncBytes = Base64.encodeToString(authString.getBytes(), Base64.NO_WRAP);
        String auth = "Basic " + authEncBytes;
        Log.d(TAG, auth);
        return auth;

    }

    @Override
    public void addLocation(LocationModel l) {
        JSONObject locJson = new JSONObject();
        try {
            locJson = new JSONObject();
            locJson.put("latitude", "" + l.latitude);
            locJson.put("longitude", "" + l.longitude);
            locJson.put("speed", "" + l.speed);
            locJson.put("timestamp", "" + (l.timestamp / 1000));
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
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            String a = new String(error.networkResponse.data);
                            Log.d(TAG, "onErrorResponse " + a);
                        }

                    }
                });
        jr.setHeader("Authorization", "ApiKey " + SmartTaxiAsync.userName + ":" + SmartTaxiAsync.apiKey);
        mRequestQueue.add(jr);
    }

    @Override
    public TastypieResponse<LocationModel> getLocations() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void changeStatus(int status, final SmartTaxiResponseAdapter responder) {
        JSONObject locJson = new JSONObject();
        try {
            locJson = new JSONObject();
            locJson.put("status", "" + status);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d(TAG, "" + locJson);
        GsonRequest<Void> jr = new GsonRequest<Void>(
                Request.Method.PUT,
                getAbsoluteUrl("taxi/" + taxi.id + "/"),
                Void.class,
                locJson,
                new Response.Listener<Void>() {

                    @Override
                    public void onResponse(Void response) {
                        responder.changeStatusSuccess();
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        responder.onError(error);

                    }
                });

        setAuthHeader(jr);

        mRequestQueue.add(jr);
    }

    @Override
    public void getTaxisDetail(final SmartTaxiResponseAdapter responder) {
        Type fooType = new TypeToken<TastypieResponse<TaxiModel>>() {
        }.getType();

        GsonRequest<TastypieResponse<TaxiModel>> jr = new GsonRequest<TastypieResponse<TaxiModel>>(
                Request.Method.GET,
                getAbsoluteUrl("taxi/"),
                fooType,
                new Response.Listener<TastypieResponse<TaxiModel>>() {

                    @Override
                    public void onResponse(TastypieResponse<TaxiModel> response) {
                        taxi = response.objects.get(0);
                        responder.gotTaxis(response);
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        responder.onError(error);

                    }
                });
        setAuthHeader(jr);
        mRequestQueue.add(jr);
    }

    private void setAuthHeader(GsonRequest<?> jr) {
        jr.setHeader("Authorization", "ApiKey " + SmartTaxiAsync.userName + ":" + SmartTaxiAsync.apiKey);
    }

    private void setBasicAuthHeader(GsonRequest<?> jr, String user, String pass) {
        jr.setHeader("Authorization", makeAuthBasic(user, pass));
    }

    @Override
    public void setDeviceData(String regId, String devId, final SmartTaxiResponseAdapter responder) {
        JSONObject deviceData = new JSONObject();
        try {
            deviceData = new JSONObject();
            deviceData.put("reg_id", regId);
            deviceData.put("dev_id", devId);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d(TAG, "" + deviceData);
        GsonRequest<Void> jr = new GsonRequest<Void>(
                Request.Method.PUT,
                getAbsoluteUrl("taxi/" + taxi.id + "/device/"),
                Void.class,
                deviceData,
                new Response.Listener<Void>() {

                    @Override
                    public void onResponse(Void response) {
                        responder.addDeviceSuccess();

                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        responder.onError(error);

                    }
                });

        setAuthHeader(jr);

        mRequestQueue.add(jr);
    }

    @Override
    public void getNotifications(final SmartTaxiResponseAdapter responder) {

        Type fooType = new TypeToken<TastypieResponse<NotificationModel>>() {
        }.getType();

        GsonRequest<TastypieResponse<NotificationModel>> jr = new GsonRequest<TastypieResponse<NotificationModel>>(
                Request.Method.GET,
                getAbsoluteUrl("notificaciones/"),
                fooType,
                new Response.Listener<TastypieResponse<NotificationModel>>() {

                    @Override
                    public void onResponse(TastypieResponse<NotificationModel> response) {
                        responder.gotNotifications(response);
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        responder.onError(error);

                    }
                });
        setAuthHeader(jr);
        mRequestQueue.add(jr);

    }

    @Override
    public void acceptNotification(int id, final SmartTaxiResponseAdapter responder) {
        changeStatusNotification(NOTIF_RESPONDED, id, responder);
    }

    @Override
    public void cancelNotification(int id, SmartTaxiResponseAdapter responder) {
        changeStatusNotification(NOTIF_REJECTED, id, responder);
    }
    
    private void changeStatusNotification(int status, int id, final SmartTaxiResponseAdapter responder){
        JSONObject locJson = new JSONObject();
        try {
            locJson = new JSONObject();
            locJson.put("status", "" + status);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d(TAG, "" + locJson);
        GsonRequest<Void> jr = new GsonRequest<Void>(
                Request.Method.PUT,
                getAbsoluteUrl("notificaciones/" + id + "/"),
                Void.class,
                locJson,
                new Response.Listener<Void>() {

                    @Override
                    public void onResponse(Void response) {
                        responder.acceptedNotification();
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        responder.onError(error);

                    }
                });

        setAuthHeader(jr);

        mRequestQueue.add(jr);
    }

}
