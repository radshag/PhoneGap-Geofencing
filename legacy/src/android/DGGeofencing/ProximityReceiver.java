package org.apache.cordova.plugin.geo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.util.Log;

import static org.apache.cordova.plugin.geo.DGGeofencingService.TAG;

/**
 * @author edewit@redhat.com
 */
public class ProximityReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    String id = intent.getData().getLastPathSegment();
    Log.d(TAG, "received proximity alert for region " + id);

    Intent cordovaIntent = new Intent(context.getApplicationContext(), HelloCordova.class);
    cordovaIntent.putExtra("id", id);
    String status = intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, false) ? "enter" : "left";
    cordovaIntent.putExtra("status", status);
    cordovaIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

//    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, cordovaIntent, 0);
//    Notification notification = createNotification();
//    NotificationManager notificationManager =
//              (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//    notification.setLatestEventInfo(context,
//            "Proximity Alert!", "You are near your point of interest.", pendingIntent);
//
//    notificationManager.notify(1000, notification);

    context.startActivity(cordovaIntent);
  }

  private Notification createNotification() {
    Notification notification = new Notification();
    notification.icon = R.drawable.icon;
    notification.when = System.currentTimeMillis();
    notification.flags |= Notification.FLAG_AUTO_CANCEL;
    notification.flags |= Notification.FLAG_SHOW_LIGHTS;
    notification.defaults |= Notification.DEFAULT_VIBRATE;
    notification.defaults |= Notification.DEFAULT_LIGHTS;
    notification.ledARGB = Color.WHITE;
    notification.ledOnMS = 1500;
    notification.ledOffMS = 1500;
    return notification;

  }

}
