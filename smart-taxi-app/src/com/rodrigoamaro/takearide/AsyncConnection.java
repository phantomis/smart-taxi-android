package com.rodrigoamaro.takearide;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.lang.IllegalArgumentException;

import org.json.JSONObject;

import android.util.Log;

public class AsyncConnection implements Runnable {
	JSONObject jsonToSend;
	ConnectionStatusListener listener;
	final String TAG = "AsyncConnection";

	public AsyncConnection(JSONObject jsonToSend, ConnectionStatusListener listener) {
		// store parameter for later user
		this.jsonToSend = jsonToSend;
		this.listener = listener;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			URL url = new URL("http://10.0.2.1:88899/api/v1/location/");

			HttpURLConnection httpCon = (HttpURLConnection) url
					.openConnection();
			httpCon.setRequestProperty("Content-Type",
					"application/json; charset=utf-8");
			httpCon.setDoOutput(true);
			httpCon.setDoInput(true);
			httpCon.setUseCaches(false);
			httpCon.setRequestMethod("POST");
			OutputStream o = httpCon.getOutputStream();
			OutputStreamWriter out = new OutputStreamWriter(o);
			out.write(jsonToSend.toString());
			out.flush();
			out.close();

			InputStream is = httpCon.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer response = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
			Log.d(TAG, "response: " + httpCon.getResponseCode() + " "
					+ response.toString());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			listener.onConnectionFail(TAG, e);
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			listener.onConnectionFail(TAG, e);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			listener.onConnectionFail(TAG, e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			listener.onConnectionFail(TAG, e);
		}
	}

}
