package org.apache.cordova.plugin.geo;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * @author edewit@redhat.com
 */
public class DGGeofencingService implements LocationListener {
  public static final int INTERFAL_TIME = 60000;
  public static final int MIN_DISTANCE = 10;
  static final String TAG = DGGeofencingService.class.getSimpleName();

  static final String PROXIMITY_ALERT_INTENT = "geoFencingProximityAlert";

  private LocationManager locationManager;
  private Set<LocationChangedListener> listeners = new HashSet<LocationChangedListener>();
  private final Activity activity;

  public DGGeofencingService(Activity activity) {
    this.activity = activity;
    locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    if (location != null) {
      onLocationChanged(location);
    }
    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, INTERFAL_TIME, MIN_DISTANCE, this);
  }

  public void addRegion(String id, double latitude, double longitude, float radius) {
    PendingIntent proximityIntent = createIntent(id);
    locationManager.addProximityAlert(latitude, longitude, radius, -1, proximityIntent);
  }

  public void removeRegion(String id) {
    PendingIntent proximityIntent = createIntent(id);
    locationManager.removeProximityAlert(proximityIntent);
  }

  private PendingIntent createIntent(String id) {
    Intent intent = new Intent(PROXIMITY_ALERT_INTENT);
    intent.putExtra("id", id);
    return PendingIntent.getBroadcast(activity, 0, intent, FLAG_ACTIVITY_NEW_TASK);
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
}
