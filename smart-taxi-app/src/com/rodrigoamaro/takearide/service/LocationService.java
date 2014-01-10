
package com.rodrigoamaro.takearide.service;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.rodrigoamaro.takearide.R;
import com.rodrigoamaro.takearide.R.drawable;
import com.rodrigoamaro.takearide.activities.MainMapFragment;
import com.rodrigoamaro.takearide.serverapi.SmartTaxiAsync;
import com.rodrigoamaro.takearide.serverapi.models.LocationModel;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

public class LocationService extends Service {
    private NotificationManager mNM;
    private final static int NOTIF_ID = 1;
    private final static String TAG = "LocationService";
    // Milliseconds per second
    private static final int MILLISECONDS_PER_SECOND = 1000;
    // Update frequency in seconds
    public static final int UPDATE_INTERVAL_IN_SECONDS = 15;
    // Update frequency in milliseconds
    private static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    // The fastest update frequency, in seconds
    private static final int FASTEST_INTERVAL_IN_SECONDS = 10;
    // A fast frequency ceiling in milliseconds
    private static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;

    // This is the object that receives interactions from clients. See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();

    
    public static double actualLatitude = 0;
    public static double actualLongitude = 0;
    private Handler mHandler = new Handler();
    private LocationClient mLocationClient;
    private LocationRequest mLocationRequest;
    private Runnable timedTask = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            LocationModel l = new LocationModel(1,2,3);
            
            mHandler.postDelayed(timedTask, 10 * 1000);
        }
    };

    public class LocalBinder extends Binder {
        public LocationService getService() {
            return LocationService.this;
        }
    }

    /**
     * @see android.app.Service#onBind(Intent)
     */
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Put your code here
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
        mNM.cancel(NOTIF_ID);
        mLocationClient.disconnect();
        // Tell the user we stopped.
        Toast.makeText(this, "Stoped service", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreate() {
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Display a notification about us starting. We put an icon in the
        // status bar.
        showNotification();

        mHandler.post(timedTask);
        startLocationUpdater();
    }

    private void startLocationUpdater() {
        // TODO Auto-generated method stub
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Set the update interval to 5 seconds
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        // Set the fastest update interval to 1 second
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        mLocationClient = new LocationClient(this, new ConnectionCallbacks() {

            @Override
            public void onDisconnected() {
                Toast.makeText(getApplicationContext(), "LocationClient onDisconnected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onConnected(Bundle connectionHint) {

                Toast.makeText(getApplicationContext(), "LocationClient onConnected", Toast.LENGTH_SHORT).show();
                mLocationClient.requestLocationUpdates(mLocationRequest, new LocationListener() {

                    @Override
                    public void onLocationChanged(Location location) {
                        LocationService.actualLatitude = location.getLatitude();
                        LocationService.actualLongitude = location.getLongitude();
                        Log.d(TAG,
                                "onLocationChanged lat:" + location.getLatitude() + " long:" + location.getLongitude() + " speed:"
                                        + location.getSpeed());
                        LocationModel l = new LocationModel(location.getLatitude(),location.getLongitude(),location.getSpeed());
                        SmartTaxiAsync.getInstance(getApplicationContext()).addLocation(l);
                    }
                });

            }
        }, new OnConnectionFailedListener() {

            @Override
            public void onConnectionFailed(ConnectionResult result) {
                Toast.makeText(getApplicationContext(), "onConnectionFailed", Toast.LENGTH_SHORT).show();
            }

        });

        mLocationClient.connect();
    }

    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!")
                        .setOngoing(true)
                        .setAutoCancel(true);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainMapFragment.class);

        // The stack builder object will contain an artificial back stack for
        // the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainMapFragment.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                        );
        mBuilder.setContentIntent(resultPendingIntent);

        // mId allows you to update the notification later on.
        mNM.notify(NOTIF_ID, mBuilder.build());
    }

}
