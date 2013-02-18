package org.apache.cordova.plugin.geo;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.util.*;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * @author edewit@redhat.com
 */
public class DGGeofencingService extends Service implements LocationListener {
    public static final int INTERFAL_TIME = 60000;
    public static final int MIN_DISTANCE = 10;
    private final IBinder binder = new DGGeofencingServiceBinder();
    private static final String TAG = DGGeofencingService.class.getSimpleName();

    static final String PROXIMITY_ALERT_INTENT = "geoFencingProximityAlert";

    private Map<Integer, PendingIntent> regionIdIntentMapping = new HashMap<Integer, PendingIntent>();
    private LocationManager locationManager;
    private Set<LocationChangedListener> listeners = new HashSet<LocationChangedListener>();

    public boolean isGpsEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public void addRegion(int id, double latitude, double longitude, float radius) {
        Intent intent = new Intent(PROXIMITY_ALERT_INTENT + id);
        intent.putExtra("id", id);
        PendingIntent proximityIntent = PendingIntent.getBroadcast(this, 0, intent, FLAG_ACTIVITY_NEW_TASK);
        regionIdIntentMapping.put(id, proximityIntent);

        locationManager.addProximityAlert(latitude, longitude, radius, -1, proximityIntent);
    }

    public void removeRegion(int id) {
        locationManager.removeProximityAlert(regionIdIntentMapping.get(id));
    }

    public void addLocationChangedListener(LocationChangedListener listener) {
        this.listeners.add(listener);
    }

    public void removeLocationChangedListener(LocationChangedListener listener) {
        this.listeners.remove(listener);
    }

    @Override
    public void onLocationChanged(Location location) {
        String text = String.format("\nLat:\t %f\nLong:\t %f\nAlt:\t %f\nBearing:\t %f", location.getLatitude(),
                location.getLongitude(), location.getAltitude(), location.getBearing());
        Log.d(TAG, "onLocationChanged with location " + text);

        for (LocationChangedListener changedListener : listeners) {
            changedListener.onLocationChanged(location);
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {
    }

    @Override
    public void onCreate() {
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            onLocationChanged(location);
    }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, INTERFAL_TIME, MIN_DISTANCE, this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public Set<Integer> getWatchedRegionIds() {
        return regionIdIntentMapping.keySet();
    }

    public class DGGeofencingServiceBinder extends Binder {
        DGGeofencingService getService() {
            return DGGeofencingService.this;
        }
    }

}
