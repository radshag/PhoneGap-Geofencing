package org.apache.cordova.plugin.geo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static org.apache.cordova.plugin.geo.DGGeofencingService.TAG;

/**
 * @author edewit@redhat.com
 */
public class ProximityReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    String id = (String) intent.getExtras().get("id");
    Log.d(TAG, "received proximity alert for region " + id);

    DGGeofencing.getInstance().fireRegionChangedEvent(intent);
  }
}
