package com.rodrigoamaro.takearide.activities;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.rodrigoamaro.takearide.R;
import com.rodrigoamaro.takearide.serverapi.SmartTaxiAsync;
import com.rodrigoamaro.takearide.serverapi.SmartTaxiResponseAdapter;
import com.rodrigoamaro.takearide.serverapi.models.TaxiModel;

import android.app.ActionBar.Tab;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TravelActivity extends FragmentActivity{

    private GoogleMap mMap;
    private Marker clientMarker;
    private Polyline routePoly;
    private Button buttonFinalize;
    protected boolean firstTime = true;
    private static final String TAG = "TravelActivity";
    
    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        setContentView(R.layout.travel_fragment_activity);
        setUpMapIfNeeded();
        Intent intent = getIntent();
        double latitude = intent.getDoubleExtra("latitude", 0);
        double longitude = intent.getDoubleExtra("longitude", 0);
        String address_name = intent.getStringExtra("address_name");
        String name = intent.getStringExtra("name");
        
        createRoute(latitude, longitude, address_name, name);
        
        buttonFinalize = (Button) findViewById(R.id.button_finalize);
        
        buttonFinalize.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                SmartTaxiAsync.getInstance(getApplicationContext()).changeStatus(TaxiModel.STATUS_AVAILABLE, new SmartTaxiResponseAdapter(){
                    @Override
                    public void changeStatusSuccess() {
                        Log.d(TAG, "Status cambiado con exito");
                    }
                    
                    @Override
                    public void onError(Exception e) {
                        // TODO Auto-generated method stub
                        Log.d(TAG, "Status cambiado con error");
                    }
                });
                
                finish();
            }
        });
        
        SmartTaxiAsync.getInstance(getApplicationContext()).changeStatus(TaxiModel.STATUS_WORKING, new SmartTaxiResponseAdapter(){
            @Override
            public void changeStatusSuccess() {
                Log.d(TAG, "Status cambiado con exito");
            }
            
            @Override
            public void onError(Exception e) {
                // TODO Auto-generated method stub
                Log.d(TAG, "Status cambiado con error");
            }
        });
    }
    
    private void createRoute(final double toLatitude, final double toLongitude, final String toAddress_name, final String name) {
        
        mMap.setOnMyLocationChangeListener(new OnMyLocationChangeListener() {
            
            @Override
            public void onMyLocationChange(Location myLocation) {
               if(firstTime ){
                   LatLng myPosition = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                   LatLng clientPosition = new LatLng(toLatitude, toLongitude);
                   
                   //Calculamos el poligono que encierra a los markadores
                   final LatLngBounds.Builder bc = new LatLngBounds.Builder();
                   bc.include(myPosition);
                   bc.include(clientPosition);
                   
                   if( clientMarker != null) clientMarker.remove();
                   clientMarker = mMap.addMarker(new MarkerOptions().position(clientPosition).title(toAddress_name).snippet(name));
                   mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), 100));
                   
                   if(routePoly != null) routePoly.remove();
                   routePoly = mMap.addPolyline(new PolylineOptions().add(myPosition, myPosition).geodesic(true));
                   firstTime = !firstTime;
               }
                
            }
        });
        
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the
        // map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }
    
    private void setUpMap() {
        mMap.setMyLocationEnabled(true);
    }
    
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        
        SmartTaxiAsync.getInstance(getApplicationContext()).changeStatus(TaxiModel.STATUS_AVAILABLE, new SmartTaxiResponseAdapter(){
            @Override
            public void changeStatusSuccess() {
                Log.d(TAG, "Status cambiado con exito");
            }
            
            @Override
            public void onError(Exception e) {
                // TODO Auto-generated method stub
                Log.d(TAG, "Status cambiado con error");
            }
        });
        
        finish();
    }
}
