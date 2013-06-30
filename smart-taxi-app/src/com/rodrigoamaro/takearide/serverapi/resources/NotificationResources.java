
package com.rodrigoamaro.takearide.serverapi.resources;

import com.rodrigoamaro.takearide.serverapi.SmartTaxiResponseAdapter;
import com.rodrigoamaro.takearide.serverapi.models.NotificationModel;
import com.rodrigoamaro.takearide.serverapi.models.TastypieResponse;

public interface NotificationResources {
    
    public final static int NOTIF_CREATED = 1;
    public final static int NOTIF_SENDED = 2;
    public final static int NOTIF_RESPONDED = 3;
    public final static int NOTIF_REJECTED = 4;
    

    void getNotifications(SmartTaxiResponseAdapter responder);
    void acceptNotification(int id, SmartTaxiResponseAdapter responder);
    void cancelNotification(int id, SmartTaxiResponseAdapter responder);
}
