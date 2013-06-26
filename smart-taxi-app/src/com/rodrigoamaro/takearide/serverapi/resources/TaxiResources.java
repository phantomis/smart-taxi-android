
package com.rodrigoamaro.takearide.serverapi.resources;

import com.rodrigoamaro.takearide.serverapi.SmartTaxiResponseAdapter;
import com.rodrigoamaro.takearide.serverapi.models.TastypieResponse;
import com.rodrigoamaro.takearide.serverapi.models.TaxiModel;

public interface TaxiResources {

    public void changeStatus(int status,SmartTaxiResponseAdapter responder);

    public void getTaxisDetail(SmartTaxiResponseAdapter responder);
    
    public void setDeviceData(String regId, String devId,SmartTaxiResponseAdapter responder);
}
