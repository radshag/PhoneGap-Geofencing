package org.apache.cordova.plugin.geo;

import android.content.*;
import android.location.Location;
import android.location.LocationManager;
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
  public static final String PREFS_NAME = "watchedRegionIds";

  public DGGeofencingService service;
  private LocationChangedListener locationChangedListener;
  private Location oldLocation;
  private BroadcastReceiver receiver;
  private static DGGeofencing instance;
  private Set<String> regionIds;

  public static DGGeofencing getInstance() {
    return instance;
  }

  public DGGeofencing() {
    instance = this;
  }

  @Override
  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    super.initialize(cordova, webView);

    SharedPreferences settings = cordova.getActivity().getSharedPreferences(PREFS_NAME, 0);
    regionIds = settings.getStringSet(PREFS_NAME, new HashSet<String>());

    service = new DGGeofencingService(cordova.getActivity());
  }

  @Override
  public void onDestroy() {
    if (receiver != null) {
      cordova.getActivity().unregisterReceiver(receiver);
    }

    SharedPreferences settings = cordova.getActivity().getSharedPreferences(PREFS_NAME, 0);
    SharedPreferences.Editor editor = settings.edit();
    editor.putStringSet(PREFS_NAME, regionIds);
    editor.commit();
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
        registerListener();
        regionIds.add(id);
        callbackContext.success();
        return true;
      }
      if ("removeRegion".equals(action)) {
        JSONObject params = parseParameters(data);
        String id = params.getString("fid");
        service.removeRegion(id);
        regionIds.remove(id);
        callbackContext.success();
        return true;
      }
      if ("getWatchedRegionIds".equals(action)) {
        callbackContext.success(new JSONArray(regionIds));
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
    	    Log.d(TAG, "javascript:DGGeofencing.locationMonitorUpdate(" + createLocationEvent(location) + ")");
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

  private void registerListener() {
    IntentFilter filter = new IntentFilter(DGGeofencingService.PROXIMITY_ALERT_INTENT);
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
        String id = (String) intent.getExtras().get("id");
	    Log.d(TAG, "javascript:DGGeofencing.regionMonitorUpdate(" + createRegionEvent(id, status) + ")");
        webView.loadUrl("javascript:DGGeofencing.regionMonitorUpdate(" + createRegionEvent(id, status) + ")");
      }
    });
  }

  private String createRegionEvent(String id, String status) {
	  	JSONObject event = new JSONObject();
	  	try {
			event.put("fid", id);
			event.put("status", status);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("region could not be serialized to json", e);
		}
	  	
	  
	  	return event.toString();
  }

  private JSONObject parseParameters(JSONArray data) throws JSONException {
    if (data.length() == 1 && !data.isNull(0)) {
      return (JSONObject) data.get(0);
    } else {
      throw new IllegalArgumentException("Invalid arguments specified!");
    }
  }

}
