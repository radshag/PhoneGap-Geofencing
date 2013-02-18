package org.apache.cordova.plugin.geo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.location.Location;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaInterface;
import org.apache.cordova.api.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

import static android.location.LocationManager.KEY_PROXIMITY_ENTERING;

/**
 * @author edewit@redhat.com
 */
public class DGGeofencing extends CordovaPlugin {
    public DGGeofencingService service;
    private boolean bound;
    private LocationChangedListener locationChangedListener;
    private Location oldLocation;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        bindGeoFencingService(cordova.getActivity());
    }

    private void bindGeoFencingService(Activity activity) {
        Intent intent = new Intent(activity, DGGeofencingService.class);
        activity.bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bound) {
            cordova.getActivity().unbindService(connection);
            bound = false;
        }
    }

    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {
        try {
            if ("addRegion".equals(action)) {
                JSONObject params = parseParameters(data);
                int id = params.getInt("fid");
                service.addRegion(id, params.getDouble("latitude"), params.getDouble("longitude"),
                        (float) params.getInt("radius"));
                registerListener(id);
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
            callbackContext.error(e.getMessage());
        }

        return false;
    }

    private void fireLocationChangedEvent(Location location) {
        webView.loadUrl("javascript:DGGeofencing.locationMonitorUpdate(" + createLocationEvent(location) + ")");
        oldLocation = location;
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

    private void registerListener(int id) {
        IntentFilter filter = new IntentFilter(DGGeofencingService.PROXIMITY_ALERT_INTENT + id);
        cordova.getActivity().registerReceiver(new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                fireRegionChangedEvent(intent);
            }
        }, filter);
    }

    private void fireRegionChangedEvent(Intent intent) {
        Log.d("DGGeofencingService", "received proximity alert");

        String status = intent.getBooleanExtra(KEY_PROXIMITY_ENTERING, false) ? "enter" : "left";
        Integer id = (Integer) intent.getExtras().get("id");

        webView.loadUrl("javascript:DGGeofencing.regionMonitorUpdate(" + createRegionEvent(id, status) + ")");
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

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            DGGeofencingService.DGGeofencingServiceBinder binder = (DGGeofencingService.DGGeofencingServiceBinder) service;
            DGGeofencing.this.service = binder.getService();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
        }
    };

}
