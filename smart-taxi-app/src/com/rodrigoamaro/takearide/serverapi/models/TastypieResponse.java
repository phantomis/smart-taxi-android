package com.rodrigoamaro.takearide.serverapi.models;

import java.util.List;


public class TastypieResponse <T>{
    
    public int httpCodeResponse;
    public ErrorResponse error;
    public Metadata meta;
    public List<T> objects;
    
}
