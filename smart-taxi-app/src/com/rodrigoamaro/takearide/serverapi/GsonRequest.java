
package com.rodrigoamaro.takearide.serverapi;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

public class GsonRequest<T> extends Request<T> {
    /** Charset for request. */
    private static final String PROTOCOL_CHARSET = "utf-8";
    /** Content type for request. */
    private static final String PROTOCOL_CONTENT_TYPE = String.format("application/json; charset=%s", PROTOCOL_CHARSET);
    private final Gson mGson;
    private final Class<T> mClazz;
    private final Listener<T> mListener;
    private final String mRequestBody;
    private Map<String, String> headers = new HashMap<String, String>();
    private Type mTypez;

    public GsonRequest(int method,String url, Class<T> clazz, Listener<T> listener, ErrorListener errorListener) {
        super(method, url, errorListener);
        this.mClazz = clazz;
        this.mListener = listener;
        mGson = new Gson();
        mRequestBody = null;
    }

    public GsonRequest(int method, String url, Class<T> clazz, JSONObject requestObject, Listener<T> listener, ErrorListener errorListener) {
        super(method, url, errorListener);
        this.mClazz = clazz;
        this.mListener = listener;
        mGson = new Gson();
        mRequestBody = requestObject == null ? null : requestObject.toString();
    }

    public GsonRequest(int method, String url, Class<T> clazz, Listener<T> listener, ErrorListener errorListener, Gson gson) {
        super(method, url, errorListener);
        this.mClazz = clazz;
        this.mListener = listener;
        mGson = gson;
        mRequestBody = null;
    }
    
    public GsonRequest(int method, String url, Type type, Listener<T> listener, ErrorListener errorListener) {
        super(method, url, errorListener);
        this.mClazz = null;
        this.mTypez = type;
        this.mListener = listener;
        mGson = new Gson();
        mRequestBody = null;
    }

    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            Log.d("VOLLEY", HttpHeaderParser.parseCharset(response.headers));
            String json = new String(response.data, PROTOCOL_CHARSET);
            Log.d("VOLLEY", json);
            if(mClazz != null){
                return Response.success(mGson.fromJson(json, mClazz), HttpHeaderParser.parseCacheHeaders(response));
            }else{
                return Response.success((T)mGson.fromJson(json, mTypez), HttpHeaderParser.parseCacheHeaders(response));
            }
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    public byte[] getBody() {
        try {
            return mRequestBody == null ? null : mRequestBody.getBytes(PROTOCOL_CHARSET);
        } catch (UnsupportedEncodingException uee) {
            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                    mRequestBody, PROTOCOL_CHARSET);
            return null;
        }
    }

    @Override
    public String getBodyContentType() {
        return PROTOCOL_CONTENT_TYPE;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers;
    }

    public void setHeader(String title, String content) {
        headers.put(title, content);
    }
}
