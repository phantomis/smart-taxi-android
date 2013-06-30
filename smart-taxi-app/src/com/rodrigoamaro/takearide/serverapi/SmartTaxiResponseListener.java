
package com.rodrigoamaro.takearide.serverapi;

import com.rodrigoamaro.takearide.serverapi.models.NotificationModel;
import com.rodrigoamaro.takearide.serverapi.models.TastypieResponse;
import com.rodrigoamaro.takearide.serverapi.models.TaxiModel;
import com.rodrigoamaro.takearide.serverapi.models.TokenResponse;

interface SmartTaxiResponseListener {
    public void gotLoginSuccess(TokenResponse response);

    public void onException();
    
    public void onError(Exception e);

    public void gotTaxis(TastypieResponse<TaxiModel> taxis);
    
    public void changeStatusSuccess();
    
    public void addDeviceSuccess();
    
    public void gotNotifications(TastypieResponse<NotificationModel> notif);

    public void acceptedNotification();

    public void canceledNotification();
}
