package org.apache.cordova.plugin.geo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Set;

import static org.apache.cordova.plugin.geo.DGGeoFencingService.TAG;

/**
 * @author edewit@redhat.com
 */
public class DGGeoFencing extends CordovaPlugin {
  public DGGeoFencingService service;
  private LocationChangedListener locationChangedListener;
  private Location oldLocation;
  private BroadcastReceiver receiver;
  private static DGGeoFencing instance;

  public static DGGeoFencing getInstance() {
    return instance;
  }

  public DGGeoFencing() {
    instance = this;
  }

  @Override
  public void onDestroy() {
    if (receiver != null) {
      cordova.getActivity().unregisterReceiver(receiver);
    }
  }

  @Override
  public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {
    try {
      createService();

      if ("addRegion".equals(action)) {
        JSONObject params = parseParameters(data);
        int id = params.getInt("fid");
        Log.d(TAG, "adding region " + id);
        service.addRegion(id, params.getDouble("latitude"), params.getDouble("longitude"),
                (float) params.getInt("radius"));
        registerListener();
        callbackContext.success();
        return true;
      }
      if ("removeRegion".equals(action)) {
        JSONObject params = parseParameters(data);
        int id = params.getInt("fid");
        service.removeRegion(id);
        return true;
      }
      if ("getWatchedRegionIds".equals(action)) {
        Set<Integer> watchedRegionIds = service.getWatchedRegionIds();
        callbackContext.success(new JSONArray(watchedRegionIds));
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
      }

      if ("stopMonitoringSignificantLocationChanges".equals(action)) {
        service.removeLocationChangedListener(locationChangedListener);
        callbackContext.success();
      }

    } catch (Exception e) {
      StringWriter writer = new StringWriter();
      PrintWriter err = new PrintWriter(writer);
      e.printStackTrace(err);
      callbackContext.error(writer.toString());
    }

    return false;
  }

  private void createService() {
    if (service == null) {
      service = new DGGeoFencingService(cordova.getActivity());
    }
  }

  void fireLocationChangedEvent(final Location location) {
    Log.d(TAG, "fireLocationChangedEvent");
    cordova.getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        webView.loadUrl("javascript:DGGeoFencing.locationMonitorUpdate(" + createLocationEvent(location) + ")");
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

  private void registerListener() {
    IntentFilter filter = new IntentFilter(DGGeoFencingService.PROXIMITY_ALERT_INTENT);
    receiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, final Intent intent) {
        fireRegionChangedEvent(intent);
      }
    };
    cordova.getActivity().registerReceiver(receiver, filter);
  }

  void fireRegionChangedEvent(final Intent intent) {
    cordova.getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        String status = intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, false) ? "enter" : "left";
        Integer id = (Integer) intent.getExtras().get("id");
        webView.loadUrl("javascript:DGGeoFencing.regionMonitorUpdate(" + createRegionEvent(id, status) + ")");
      }
    });
  }

  private String createRegionEvent(Integer id, String status) {
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
