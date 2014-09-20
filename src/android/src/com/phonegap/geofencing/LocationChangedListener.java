package com.phonegap.geofencing;


import android.location.Location;

/**
 * Listener to receive location updates.
 */
public interface LocationChangedListener {

    void onLocationChanged(Location location);
}
