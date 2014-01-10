
package com.rodrigoamaro.takearide.activities;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;

import com.google.android.gms.internal.al;
import com.rodrigoamaro.takearide.R;
import com.rodrigoamaro.takearide.serverapi.SmartTaxiAsync;
import com.rodrigoamaro.takearide.serverapi.SmartTaxiResponseAdapter;
import com.rodrigoamaro.takearide.serverapi.models.ClientModel;
import com.rodrigoamaro.takearide.serverapi.models.NotificationModel;
import com.rodrigoamaro.takearide.serverapi.models.TastypieResponse;
import com.rodrigoamaro.takearide.service.LocationService;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ClientesFragment extends ListFragment implements OnItemClickListener {

    protected static final String TAG = "ClientsFragment";
    private NotificationAdapter mAdapter;
    
    
    SmartTaxiResponseAdapter onNotificationAcepted = new SmartTaxiResponseAdapter() {
        @Override
        public void gotNotifications(TastypieResponse<NotificationModel> notif) {
            Log.d(TAG, "le tinimos nuevas notificaciones " + notif.objects.size());
            mAdapter.clear();
            for(NotificationModel noty : notif.objects){
                Log.d(TAG, "notif " + noty.taxi);
                if(noty.status.equals("2")){
                    mAdapter.add(noty);
                }
            }
            
            mAdapter.notifyDataSetChanged();
        }
    };

    private BroadcastReceiver mGcmHandler = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String notif_url = intent.getStringExtra("url");
            Log.d(TAG, "notif en activity " + notif_url);
            SmartTaxiAsync.getInstance(getActivity().getApplicationContext()).getNotifications(onNotificationAcepted);
        }

    };
    private OnReceivedClient mCallbackActivity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if ( activity instanceof OnReceivedClient){
            
            mCallbackActivity = (OnReceivedClient) activity;
        }
    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // setListAdapter(new
        // ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,TITLES));
        mAdapter = new NotificationAdapter(getActivity());
        setListAdapter(mAdapter);
        getListView().setOnItemClickListener(this);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.i("FragmentList", "Item clicked: " + id);
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mGcmHandler, new IntentFilter("gcm_received"));
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mGcmHandler);
        super.onPause();
    }

    static public class NotificationAdapter extends ArrayAdapter<NotificationModel> {

        static class ViewHolder {
            TextView name;
            TextView address_name;
        }

        public NotificationAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            ViewHolder holder;
            if (v == null) {
                v = LayoutInflater.from(getContext()).inflate(R.layout.row_notif, null);
                holder = new ViewHolder();
                holder.name = (TextView) v.findViewById(R.id.notif_name);
                holder.address_name = (TextView) v.findViewById(R.id.notif_address_name);
                v.setTag(holder);

            }
            NotificationModel c = getItem(position);
            if (c != null) {
                holder = (ViewHolder) v.getTag();
                try {
                    Log.d(TAG, "client: " + c.client.location.latitude + " " + c.client.location.longitude);
                    Log.d(TAG, "you" + LocationService.actualLatitude + " " + LocationService.actualLongitude);
                    double distance = ClientesFragment.distance(c.client.location.latitude, c.client.location.longitude, LocationService.actualLatitude, LocationService.actualLongitude);
                    holder.name.setText(new String(c.client.name.getBytes(), "UTF-8"));
                    DecimalFormat df = new DecimalFormat("###.##");
                    
                    holder.address_name.setText("a " + df.format(distance) + "Kms");
                    
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                

            }

            return v;
        }

    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
        Log.d(TAG, "item seleciconado! " + mAdapter.getItem(arg2).client.name);

        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("Aceptando un viaje");
        alert.setMessage("Desea aceptar al usuario " + mAdapter.getItem(arg2).client.name + " ? ");
        alert.setPositiveButton("Aceptar", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                SmartTaxiAsync.getInstance(getActivity()).acceptNotification(mAdapter.getItem(arg2).id, new SmartTaxiResponseAdapter() {
                    @Override
                    public void acceptedNotification() {
                        // TODO Auto-generated method stub
                        super.acceptedNotification();
                        Log.d(TAG, "notificacion aceptada, realizando viaje!");
                        SmartTaxiAsync.getInstance(getActivity().getApplicationContext()).getNotifications(onNotificationAcepted);
                        mCallbackActivity.onReceivedClient(mAdapter.getItem(arg2));
                    }
                    
                    @Override
                    public void onError(Exception e) {
                        // TODO Auto-generated method stub
                        Log.d(TAG, "error aceptando notificacion");
                        super.onError(e);
                        
                    }
                });
            }
        });
        alert.setNegativeButton("Rechazar", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                SmartTaxiAsync.getInstance(getActivity()).cancelNotification(mAdapter.getItem(arg2).id, new SmartTaxiResponseAdapter() {
                    @Override
                    public void canceledNotification() {
                        // TODO Auto-generated method stub
                        Log.d(TAG, "notificacion rechazada correctamente, realizando viaje!");
                        SmartTaxiAsync.getInstance(getActivity().getApplicationContext()).getNotifications(onNotificationAcepted);
                        super.canceledNotification();
                    }
                    @Override
                    public void onError(Exception e) {
                        // TODO Auto-generated method stub
                        Log.d(TAG, "error rechazando notificacion");
                        super.onError(e);
                    }
                });
            }
        });
        
        alert.create().show();

    }
    
    //TODO: mandar posicion a la actividad con el mapa! 
    
    public interface OnReceivedClient{
        void onReceivedClient(NotificationModel client);
    }
    
    public static double distance(double firstLatitude, double firstLongitude, double secondLatitude, double secondLongitude){
        int earth_radium_km = 6371;
     // Conversão de graus pra radianos das latitudes
        double firstLatToRad = Math.toRadians(firstLatitude);
        double secondLatToRad = Math.toRadians(secondLatitude);

        // Diferença das longitudes
        double deltaLongitudeInRad = Math.toRadians(secondLongitude
        - firstLongitude);

        // Cálcula da distância entre os pontos
        return Math.acos(Math.cos(firstLatToRad) * Math.cos(secondLatToRad)
        * Math.cos(deltaLongitudeInRad) + Math.sin(firstLatToRad)
        * Math.sin(secondLatToRad))
        * earth_radium_km;
    }
}
