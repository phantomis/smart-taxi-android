package com.rodrigoamaro.takearide;

public interface ConnectionStatusListener {

	void onConnectionStart();
	void onConnectionFail(String tag, Exception e);
	
}
