package org.apache.cordova.plugin.geo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;

import static org.apache.cordova.plugin.geo.DGGeoFencingService.TAG;

/**
 * @author edewit@redhat.com
 */
public class ProximityReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    Integer id = (Integer) intent.getExtras().get("id");
    Log.d(TAG, "received proximity alert for region " + id);

    DGGeoFencing.getInstance().fireRegionChangedEvent(intent);
  }
}
