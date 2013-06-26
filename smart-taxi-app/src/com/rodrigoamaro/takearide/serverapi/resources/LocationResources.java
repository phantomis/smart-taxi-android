
package com.rodrigoamaro.takearide.serverapi.resources;


import com.rodrigoamaro.takearide.serverapi.models.LocationModel;
import com.rodrigoamaro.takearide.serverapi.models.TastypieResponse;

public interface LocationResources {

    void addLocation(LocationModel location);

    TastypieResponse<LocationModel> getLocations();
}
