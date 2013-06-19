package com.rodrigoamaro.takearide.serverapi;

import java.util.List;


public interface TaxiResources {
    final static int STATUS_AVAILABLE = 1;
    final static int STATUS_NOTAVAILABLE = 2;
    final static int STATUS_AWAY = 3;
    
    void changeStatus(int status);
}
