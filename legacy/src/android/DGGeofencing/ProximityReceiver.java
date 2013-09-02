package org.apache.cordova.plugin.geo;

import android.app.Notification;
import android.app.NotificationManager;
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
    String id = intent.getStringExtra("id");
    Log.d(TAG, "received proximity alert for region " + id);

//    NotificationManager notificationManager =
//            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

//    Intent pluginIntent = new Intent(context, HelloCordova.class);
//      Intent pluginIntent = new Intent(Intent.ACTION_VIEW);
//    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, pluginIntent, FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

//    Notification notification = createNotification();
//    notification.setLatestEventInfo(context,
//            "Proximity Alert!", "You are near your point of interest.", pendingIntent);

//    notificationManager.notify(1000, notification);

      Intent cordovaIntent = new Intent(context.getApplicationContext(), HelloCordova.class);
      cordovaIntent.putExtra("id", id);
      String status = intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, false) ? "enter" : "left";
      cordovaIntent.putExtra("status", status);
      cordovaIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
