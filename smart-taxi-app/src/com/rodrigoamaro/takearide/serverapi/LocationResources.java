package com.rodrigoamaro.takearide.serverapi;

import java.util.List;


public interface LocationResources {

    void addLocation(List<LocationModel> locations);
    
    StartTaxiResponse<LocationModel> getLocations();
}
