package org.apache.cordova.plugin.geo;

import android.content.*;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaInterface;
import org.apache.cordova.api.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

import static org.apache.cordova.plugin.geo.DGGeofencingService.TAG;

/**
 * @author edewit@redhat.com
 */
public class DGGeofencing extends CordovaPlugin {

  private LocationChangedListener locationChangedListener;
  public DGGeofencingService service;
  private Location oldLocation;

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            DGGeofencingService.LocalBinder binder = (DGGeofencingService.LocalBinder) service;
            DGGeofencing.this.service = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    public void onNewIntent(Intent intent) {
        fireRegionChangedEvent(intent);
    }

    @Override
  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    super.initialize(cordova, webView);

    Intent intent = new Intent(cordova.getActivity(), DGGeofencingService.class);
    cordova.getActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE);
  }

  @Override
  public void onDestroy() {
    cordova.getActivity().unbindService(connection);
  }

  @Override
  public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {
    try {
      if ("addRegion".equals(action)) {
        JSONObject params = parseParameters(data);
        String id = params.getString("fid");
        Log.d(TAG, "adding region " + id);
        service.addRegion(id, params.getDouble("latitude"), params.getDouble("longitude"),
                (float) params.getInt("radius"));
        callbackContext.success();
        return true;
      }
      if ("removeRegion".equals(action)) {
        JSONObject params = parseParameters(data);
        String id = params.getString("fid");
        service.removeRegion(id);
		callbackContext.success();
        return true;
      }
      if ("getWatchedRegionIds".equals(action)) {
        callbackContext.success(new JSONArray(service.getWachedRegionIds()));
        return true;
      }

      if ("startMonitoringSignificantLocationChanges".equals(action)) {
        Log.d(TAG, "startMonitoringSignificantLocationChanges");
        if (locationChangedListener == null) {
          locationChangedListener = new LocationChangedListener() {
            @Override
            public void onLocationChanged(Location location) {
              fireLocationChangedEvent(location);
            }
          };
        }
        service.addLocationChangedListener(locationChangedListener);
        callbackContext.success();
		return true;
      }

      if ("stopMonitoringSignificantLocationChanges".equals(action)) {
        service.removeLocationChangedListener(locationChangedListener);
        callbackContext.success();
        return true;
      }

    } catch (Exception e) {
      StringWriter writer = new StringWriter();
      PrintWriter err = new PrintWriter(writer);
      e.printStackTrace(err);
      callbackContext.error(writer.toString());
    }

    return false;
  }

  void fireLocationChangedEvent(final Location location) {
    Log.d(TAG, "fireLocationChangedEvent");
    cordova.getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        webView.loadUrl("javascript:DGGeofencing.locationMonitorUpdate(" + createLocationEvent(location) + ")");
        oldLocation = location;
      }
    });
  }

  private String createLocationEvent(Location location) {
    JSONObject object = new JSONObject();
    try {
      addLocation(location, object, "new");
      if (oldLocation != null) {
        addLocation(oldLocation, object, "old");
      }
    } catch (JSONException e) {
      throw new RuntimeException("location could not be serialized to json", e);
    }

    return object.toString();
  }

  private void addLocation(Location location, JSONObject object, String prefix) throws JSONException {
    object.put(prefix + "_timestamp", location.getTime());
    object.put(prefix + "_speed", location.getSpeed());
    object.put(prefix + "_course", location.getBearing());
    object.put(prefix + "_verticalAccuracy", location.getAccuracy());
    object.put(prefix + "_horizontalAccuracy", location.getAccuracy());
    object.put(prefix + "_altitude", location.getAltitude());
    object.put(prefix + "_latitude", location.getLatitude());
    object.put(prefix + "_longitude", location.getLongitude());
  }

  void fireRegionChangedEvent(final Intent intent) {
    cordova.getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        String status = intent.getStringExtra("staus");
        String id = intent.getStringExtra("id");
        webView.loadUrl("javascript:DGGeofencing.regionMonitorUpdate(" + createRegionEvent(id, status) + ")");
      }
    });
  }

  private String createRegionEvent(String id, String status) {
    return "{fid:" + id + ",status:\"" + status + "\"}";
  }

  private JSONObject parseParameters(JSONArray data) throws JSONException {
    if (data.length() == 1 && !data.isNull(0)) {
      return (JSONObject) data.get(0);
    } else {
      throw new IllegalArgumentException("Invalid arguments specified!");
    }
  }

}
