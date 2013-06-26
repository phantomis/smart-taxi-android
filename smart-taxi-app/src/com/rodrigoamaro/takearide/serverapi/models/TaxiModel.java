
package com.rodrigoamaro.takearide.serverapi.models;

public class TaxiModel {
    public final static int STATUS_AVAILABLE = 1;
    public final static int STATUS_NOTAVAILABLE = 2;
    public final static int STATUS_AWAY = 3;

    public int id;
    public String license_plate;
    public String status;
    public String resource_uri;

}
