package org.apache.cordova.plugin.geo;

import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * @author edewit@redhat.com
 */
public class DGGeofencingService extends Service implements LocationListener {
  public static final int INTERVAL_TIME = 60000;
  public static final int MIN_DISTANCE = 10;
  static final String TAG = DGGeofencingService.class.getSimpleName();

  static final String PROXIMITY_ALERT_INTENT = "geoFencingProximityAlert";

  private LocationManager locationManager;
  private Set<LocationChangedListener> listeners = new HashSet<LocationChangedListener>();

    private final IBinder binder = new LocalBinder();
    private GeofenceStore geofenceStore;

    public class LocalBinder extends Binder {
        DGGeofencingService getService() {
            return DGGeofencingService.this;
        }
    }

    @Override
    public void onCreate() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            onLocationChanged(location);
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, INTERVAL_TIME, MIN_DISTANCE, this);

        geofenceStore = new GeofenceStore(getApplicationContext());
        for (String id : geofenceStore.getGeofences()) {
            Geofence fence = geofenceStore.getGeofence(id);
            addFence(id, fence);
        }
    }

  public void addRegion(String id, double latitude, double longitude, float radius) {
      Geofence geofence = new Geofence(id, latitude, longitude, radius);
      geofenceStore.setGeofence(id, geofence);
      addFence(id, geofence);
  }

    private void addFence(String id, Geofence geofence) {
        PendingIntent proximityIntent = createIntent(id);
        locationManager.addProximityAlert(geofence.getLatitude(), geofence.getLongitude(),
                geofence.getRadius(), geofence.getExpirationDuration(), proximityIntent);
    }

    public void removeRegion(String id) {
      geofenceStore.clearGeofence(id);
      PendingIntent proximityIntent = createIntent(id);
      locationManager.removeProximityAlert(proximityIntent);
  }

  private PendingIntent createIntent(String id) {
    Intent intent = new Intent(PROXIMITY_ALERT_INTENT);
    Uri uri = new Uri.Builder().appendPath("proximity").appendPath(id).build();
    intent.setDataAndType(uri, "vnd.geofencing.region/update");
    return PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
  }

  public void addLocationChangedListener(LocationChangedListener listener) {
    this.listeners.add(listener);
  }

  public void removeLocationChangedListener(LocationChangedListener listener) {
    this.listeners.remove(listener);
  }

  public Set<String> getWachedRegionIds() {
    return new HashSet<String>(geofenceStore.getGeofences());
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
    public IBinder onBind(Intent intent) {
        return binder;
    }
}
