package com.rodrigoamaro.takearide.serverapi;

import java.util.List;

import com.rodrigoamaro.takearide.ErrorResponse;
import com.rodrigoamaro.takearide.Metadata;

public class StartTaxiResponse <T>{
    
    int httpCodeResponse;
    ErrorResponse error;
    Metadata meta;
    List<T> objects;
    
}
