package org.apache.cordova.plugin.geo;

import android.location.Location;

/**
 * Listener to receive location updates.
 * @author edewit@redhat.com
 */
public interface LocationChangedListener {

    void onLocationChanged(Location location);
}
