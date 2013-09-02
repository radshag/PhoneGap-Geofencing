package org.apache.cordova.plugin.geo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by edewit on 9/2/13.
 */
public class StartServiceAtBootReceiver extends BroadcastReceiver{
    public void onReceive(Context context, Intent intent) {
        Intent serviceLauncher = new Intent(context, DGGeofencingService.class);
        context.startService(serviceLauncher);
        Log.v("TEST", "Service loaded at start");
        Log.d("TEST", "------------------------------- starting up ------------------------------");
        Toast.makeText(context, "service stated", 1000).show();
    }
}
