package com.ogonium.phonegap.plugins;

import org.apache.cordova.GeoBroker;
import org.apache.cordova.GPSListener;
import org.apache.cordova.NetworkListener;
import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaPlugin;
import org.apache.cordova.api.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

public class DGGeofencing extends GeoBroker {
    private GPSListener gpsListener;
    private NetworkListener networkListener;
    private LocationManager locationManager; 
    
    /**
     * Constructor.
     */
    public DGGeofencing() {
    }
    
    /**
     * Executes the request and returns PluginResult.
     *
     * @param action 		The action to execute.
     * @param args 		JSONArry of arguments for the plugin.
     * @param callbackContext	The callback id used when calling back into JavaScript.
     * @return 			True if the action was valid, or false if not.
     */
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (this.locationManager == null) {
            this.locationManager = (LocationManager) this.cordova.getActivity().getSystemService(Context.LOCATION_SERVICE);
            this.networkListener = new NetworkListener(this.locationManager, this);
            this.gpsListener = new GPSListener(this.locationManager, this);
        }

        if ( locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ||
                locationManager.isProviderEnabled( LocationManager.NETWORK_PROVIDER )) {

            if (action.equals("initCallbackForRegionMonitoring")) {
            	JSONObject o = new JSONObject();
            	o.put("timestamp", System.currentTimeMillis());
            	o.put("message", "Region monitoring callback added");
            	o.put("callbacktype", "initmonitor");
            	PluginResult result = new PluginResult(PluginResult.Status.OK, o);
            	result.setKeepCallback(true);
                callbackContext.sendPluginResult(result);
            }
            else if (action.equals("startMonitoringRegion")) {
                String regionId = args.getString(0);
                String latitude = args.getString(1);
                String longitude = args.getString(2);
                double radius = args.getDouble(3);
            }
            else if (action.equals("clearWatch")) {
                String id = args.getString(0);
                this.clearWatch(id);
            }
            else {
                return false;
            }
        } else {
            PluginResult.Status status = PluginResult.Status.NO_RESULT;
            String message = "Location API is not available for this device.";
            PluginResult result = new PluginResult(status, message);
            callbackContext.sendPluginResult(result);
        }
        return true;
    }

    private void clearWatch(String id) {
        this.gpsListener.clearWatch(id);
        this.networkListener.clearWatch(id);
    }

    private void getCurrentLocation(CallbackContext callbackContext, boolean enableHighAccuracy, int timeout) {
        if (enableHighAccuracy) {
            this.gpsListener.addCallback(callbackContext, timeout);
        } else {
            this.networkListener.addCallback(callbackContext, timeout);
        }
    }

    private void addWatch(String timerId, CallbackContext callbackContext, boolean enableHighAccuracy) {
        if (enableHighAccuracy) {
            this.gpsListener.addWatch(timerId, callbackContext);
        } else {
            this.networkListener.addWatch(timerId, callbackContext);
        }
    }

    /**
     * Called when the activity is to be shut down.
     * Stop listener.
     */
    public void onDestroy() {
        if (this.networkListener != null) {
            this.networkListener.destroy();
            this.networkListener = null;
        }
        if (this.gpsListener != null) {
            this.gpsListener.destroy();
            this.gpsListener = null;
        }
    }

    /**
     * Called when the view navigates.
     * Stop the listeners.
     */
    public void onReset() {
        this.onDestroy();
    }

    public JSONObject returnLocationJSON(Location loc) {
        JSONObject o = new JSONObject();

        try {
            o.put("latitude", loc.getLatitude());
            o.put("longitude", loc.getLongitude());
            o.put("altitude", (loc.hasAltitude() ? loc.getAltitude() : null));
            o.put("accuracy", loc.getAccuracy());
            o.put("heading", (loc.hasBearing() ? (loc.hasSpeed() ? loc.getBearing() : null) : null));
            o.put("velocity", loc.getSpeed());
            o.put("timestamp", loc.getTime());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return o;
    }

    public void win(Location loc, CallbackContext callbackContext, boolean keepCallback) {
    	PluginResult result = new PluginResult(PluginResult.Status.OK, this.returnLocationJSON(loc));
    	result.setKeepCallback(keepCallback);
        callbackContext.sendPluginResult(result);
    }

    /**
     * Location failed.  Send error back to JavaScript.
     * 
     * @param code			The error code
     * @param msg			The error message
     * @throws JSONException 
     */
    public void fail(int code, String msg, CallbackContext callbackContext, boolean keepCallback) {
        JSONObject obj = new JSONObject();
        String backup = null;
        try {
            obj.put("code", code);
            obj.put("message", msg);
        } catch (JSONException e) {
            obj = null;
            backup = "{'code':" + code + ",'message':'" + msg.replaceAll("'", "\'") + "'}";
        }
        PluginResult result;
        if (obj != null) {
            result = new PluginResult(PluginResult.Status.ERROR, obj);
        } else {
            result = new PluginResult(PluginResult.Status.ERROR, backup);
        }

        result.setKeepCallback(keepCallback);
        callbackContext.sendPluginResult(result);
    }

    public boolean isGlobalListener(CordovaLocationListener listener)
    {
    	if (gpsListener != null && networkListener != null)
    	{
    		return gpsListener.equals(listener) || networkListener.equals(listener);
    	}
    	else
    		return false;
    }
}
