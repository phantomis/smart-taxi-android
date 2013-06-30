package com.rodrigoamaro.takearide.serverapi;

import com.rodrigoamaro.takearide.serverapi.models.NotificationModel;
import com.rodrigoamaro.takearide.serverapi.models.TastypieResponse;
import com.rodrigoamaro.takearide.serverapi.models.TaxiModel;
import com.rodrigoamaro.takearide.serverapi.models.TokenResponse;

public class SmartTaxiResponseAdapter implements SmartTaxiResponseListener{

    @Override
    public void onException() {}

    @Override
    public void gotLoginSuccess(TokenResponse response) {}

    @Override
    public void gotTaxis(TastypieResponse<TaxiModel> taxis) {}

    @Override
    public void onError(Exception e) {}

    @Override
    public void changeStatusSuccess() {}

    @Override
    public void addDeviceSuccess() {}

    @Override
    public void gotNotifications(TastypieResponse<NotificationModel> notif) {}

    @Override
    public void acceptedNotification() {}

    @Override
    public void canceledNotification() {}

}
