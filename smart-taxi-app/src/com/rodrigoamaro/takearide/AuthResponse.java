package com.rodrigoamaro.takearide;

public class AuthResponse {
    boolean success;
    String api_key;
    String reason;
    public boolean getSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }
    public String getApiKey() {
        return api_key;
    }
    public void setApiKey(String api_key) {
        this.api_key = api_key;
    }
    public String getReason() {
        return reason;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "sucess:" + this.success + " reason:" + this.reason+ " apiKey:" + this.api_key;
    }
    
}
