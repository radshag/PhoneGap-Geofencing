package com.phonegap.geofencing;


import android.app.PendingIntent;
import android.content.*;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;




public class DGGeofencing extends CordovaPlugin implements LocationListener 
{
	public static final String TAG = "Geofencing"; 
	public static final String PROXIMITY_ALERT_INTENT = "geoFencingProximityAlert";
	public static final int INTERFAL_TIME = 60000;
	public static final int MIN_DISTANCE = 10;
	  
	private LocationManager locationManager;
	private LocationChangedListener locationChangedListener;
	private Set<LocationChangedListener> listeners = new HashSet<LocationChangedListener>();
	private Location oldLocation;
	private boolean listening = false;
	
	private BroadcastReceiver receiver;
	  
	private static DGGeofencing instance;
	
	private Context context;
	  
	private CallbackContext geofencingCallbacks;
	private CallbackContext currentCallbacks;

	
	enum DGLocationStatus
	{
	    PERMISSIONDENIED(1), POSITIONUNAVAILABLE(2), TIMEOUT(3);
	    
	    private int statusCode;
	    
	    private DGLocationStatus(int n)
	    {
	    	statusCode = n;
	    }
	    
	    public int getStatusCode()
	    {
	    	return statusCode;
	    }
	}
	
	enum DGGeofencingStatus
	{
	    GEOFENCINGPERMISSIONDENIED(4), GEOFENCINGUNAVAILABLE(5), GEOFENCINGTIMEOUT(6);

	    private int statusCode;
	    
	    private DGGeofencingStatus(int n)
	    {
	    	statusCode = n;
	    }
	    
	    public int getStatusCode()
	    {
	    	return statusCode;
	    }
	}
	
	  public static DGGeofencing getInstance() {
	    return instance;
	  }

	  public DGGeofencing() {
	    instance = this;
	  }
	
	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);

		context = cordova.getActivity();
    
	    locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
	    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	    if (location != null) {
	      onLocationChanged(location);
	    }
	}

	@Override
	public void onDestroy() 
	{
		context = null;
		geofencingCallbacks = null;	 
		
		if (receiver != null) {			
			cordova.getActivity().unregisterReceiver(receiver);
		}
	}

	@Override
	public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException
	{
		currentCallbacks = callbackContext; 

		try 
		{
			if ("initCallbackForRegionMonitoring".equals(action))
			{
				geofencingCallbacks = callbackContext;	 
				
				JSONObject returnInfo = new JSONObject();				
				returnInfo.put("timestamp", System.currentTimeMillis());
				returnInfo.put("message", "Region monitoring callback added");
				returnInfo.put("callbacktype", "initmonitor");
        	
				successCallback(returnInfo);
				
				return true;
	        }			
			else if ("startMonitoringRegion".equals(action))
	        {
		        /*JSONObject params = parseParameters(data);
		        String regionId = params.getString("fid");
		        double latitude = Double.valueOf(params.getString("latitude"));
		        double longitude = Double.valueOf(params.getString("longitude"));
		        float radius = Float.valueOf(params.getString("radius"));*/
		        
		        String regionId = data.getString(0);
		        double latitude = Double.valueOf(data.getString(1));
		        double longitude = Double.valueOf(data.getString(2));
		        float radius = Float.valueOf(data.getString(3));
		        
		        Log.d(TAG, "adding region " + regionId);
		        
		        if(checkGeofencingAvailable())
		        {
		        	this.startMonitoringRegion(regionId, latitude, longitude, radius);
		        	registerListener();
		        	
					JSONObject returnInfo = new JSONObject();				
					returnInfo.put("timestamp", System.currentTimeMillis());
					returnInfo.put("message", "Region was successfully added for monitoring.");
					returnInfo.put("regionId", regionId);
					returnInfo.put("callbacktype", "monitorstart");

					successCallback(returnInfo);
					
					currentCallbacks.success(returnInfo);
		        }
		        
		        return true;
		    }
	        else if ("stopMonitoringRegion".equals(action))
		    {
		        //JSONObject params = parseParameters(data);
		        //String regionId = params.getString("fid");
		        
		        String regionId = data.getString(0);
		        
		        stopMonitoringRegion(regionId);
		        
				JSONObject returnInfo = new JSONObject();				
				returnInfo.put("timestamp", System.currentTimeMillis());
				returnInfo.put("message", "Region was removed successfully");
				returnInfo.put("regionId", regionId);
				returnInfo.put("callbacktype", "monitorremoved");
        	
				successCallback(returnInfo);
				
				currentCallbacks.success(returnInfo);
		        
		        return true;
		    }
	        else if ("startMonitoringSignificantLocationChanges".equals(action))
		    {
		        Log.d(TAG, "startMonitoringSignificantLocationChanges");
	        	
		        if (checkGeofencingAvailable())
		        {
		        	startListening();
		        	
			        if (locationChangedListener == null)
			        {
			        	locationChangedListener = new LocationChangedListener()
			        	{
			        		@Override
			        		public void onLocationChanged(Location location)
			        		{
			        			fireLocationChangedEvent(location);
			        		}
			        	};
			        }
			        
			        addLocationChangedListener(locationChangedListener);
			        
			        JSONObject returnInfo = new JSONObject();
			        returnInfo.put("message", "Successfully started monitoring significant location changes.");
			        currentCallbacks.success(returnInfo);
		        }
		        
				return true;
		    }
	        else if ("stopMonitoringSignificantLocationChanges".equals(action))
	        {
		        Log.d(TAG, "stopMonitoringSignificantLocationChanges");
	        	
		        if (checkGeofencingAvailable())
		        {
		        	boolean removed = false;
		        	if (locationChangedListener != null)
		        	{
		        		removeLocationChangedListener(locationChangedListener);
		        		removed = true;
		        	}
		        	
		        	JSONObject returnInfo = new JSONObject();
		        	String message;
		        	if(removed)
		        	{
		        		message = "Location listener was successfully removed.";
		        	}
		        	else
		        	{
		        		message = "A location listener was not previously set.";
		        	}
		        	returnInfo.put("message", message);
		        	currentCallbacks.success(returnInfo);
		        }
		        
		        return true;
		    }
	    } 
	    catch (Exception ex)
	    {
			errorCallback(ex.toString());
	    }
	
	    return false;
	}

	private boolean checkGeofencingAvailable() throws JSONException
	{
		if (!isLocationServicesEnabled())
		{
			JSONObject returnInfo = new JSONObject();				
			returnInfo.put("code", DGLocationStatus.PERMISSIONDENIED.getStatusCode());
			returnInfo.put("message", "Location services are disabled.");

			errorCallback(returnInfo);

			if(currentCallbacks != null)
			{
				currentCallbacks.error(returnInfo);
			}

			return false;
		}
		else if (!isGooglePlayServicesAvailable())
		{
			JSONObject returnInfo = new JSONObject();				
			returnInfo.put("code", DGGeofencingStatus.GEOFENCINGUNAVAILABLE.getStatusCode());
			returnInfo.put("message", "Geofencing services are disabled.");

			errorCallback(returnInfo);

			if(currentCallbacks != null)
			{
				currentCallbacks.error(returnInfo);
			}

			return false;
		}

		return true;
	}

	private boolean isLocationServicesEnabled()
	{
		boolean gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		if (!gps_enabled || !network_enabled)
			return false;
		
		return true;
	}
	
	private boolean isGooglePlayServicesAvailable()
	{
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
		if (status != ConnectionResult.SUCCESS)
			return false;
		
		return true;
	}
	
	public void startMonitoringRegion(String id, double latitude, double longitude, float radius) {
		startListening();
		
		PendingIntent proximityIntent = createIntent(id);
	    locationManager.addProximityAlert(latitude, longitude, radius, -1, proximityIntent);
	}

	public void stopMonitoringRegion(String id) {
	    PendingIntent proximityIntent = createIntent(id);
	    locationManager.removeProximityAlert(proximityIntent);
	}

	private PendingIntent createIntent(String id) {
	    Intent intent = new Intent(PROXIMITY_ALERT_INTENT);
	    intent.putExtra("id", id);
	    
	    return PendingIntent.getBroadcast(context, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
	}

	public void addLocationChangedListener(LocationChangedListener listener) {
		listeners.add(listener);
		
		startListening();
	}

	public void removeLocationChangedListener(LocationChangedListener listener) {
		listeners.remove(listener);
		
		stopListeningIfNoListeners();
	}
	
	void fireLocationChangedEvent(final Location location) {
		Log.d(TAG, "fireLocationChangedEvent");
		
		JSONObject returnInfo = new JSONObject();
		try
		{
			addLocation(location, returnInfo, "new");
			if (oldLocation != null) 
				addLocation(oldLocation, returnInfo, "old");
			
			returnInfo.put("callbacktype", "locationupdate");
		}
		catch (JSONException ex)
		{
			errorCallback(ex.toString());
			
			throw new RuntimeException("location could not be serialized to json", ex);
		}
		
		successCallback(returnInfo);
	}

	private void addLocation(Location location, JSONObject object, String prefix) throws JSONException
	{
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
		IntentFilter filter = new IntentFilter(PROXIMITY_ALERT_INTENT);
		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, final Intent intent) {
				fireRegionChangedEvent(intent);
			}
		};
		context.registerReceiver(receiver, filter);
	}

	void fireRegionChangedEvent(final Intent intent) {
		String status = intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, false) ? "entering" : "exiting";
		String regionId = (String) intent.getExtras().get("id");
		
		if (geofencingCallbacks != null)
		{
			JSONObject returnInfo = new JSONObject();				

			if (status.equals("entering"))
			{
				try
				{
					returnInfo.put("timestamp", System.currentTimeMillis());
					returnInfo.put("message", "Region was entered");
					returnInfo.put("regionId", regionId);
					returnInfo.put("callbacktype", "enter");
			    }
				catch(JSONException ex)
			    {
					errorCallback(ex.toString());
					
					throw new RuntimeException("location could not be serialized to json", ex);
			    }
			}
			else // exiting
			{ 
				try
				{
					returnInfo.put("timestamp", System.currentTimeMillis());
					returnInfo.put("message", "Region was exited");
					returnInfo.put("regionId", regionId);
					returnInfo.put("callbacktype", "exit");
			    }
				catch(JSONException ex)
			    {
					errorCallback(ex.toString());
					
					throw new RuntimeException("location could not be serialized to json", ex);
			    }				
			}
			
			successCallback(returnInfo);
		}
	}

	private JSONObject parseParameters(JSONArray data) throws JSONException
	{
		if (data.length() == 1 && !data.isNull(0))
		{
			return (JSONObject) data.get(0);
		}
		else 
		{
			errorCallback("Invalid arguments specified!");
			
			throw new IllegalArgumentException("Invalid arguments specified!");
		}
	}
  
	private void successCallback(final JSONObject returnInfo)
	{
    	if (geofencingCallbacks == null)
    	{
			JSONObject returnErrorInfo = new JSONObject();
			try
			{
				returnErrorInfo.put("code", DGLocationStatus.PERMISSIONDENIED.getStatusCode());
				returnErrorInfo.put("message", "At first, Please call initCallbackForRegionMonitoring().");
			}
			catch (JSONException ex)
			{
				currentCallbacks.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, ex.toString()));
				return;
			}
			
			currentCallbacks.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, returnErrorInfo));
    	}
    	else
    	{
    		cordova.getThreadPool().execute(new Runnable() {
	            public void run() {
	            	
	        		PluginResult result = new PluginResult(PluginResult.Status.OK, returnInfo);
	        		result.setKeepCallback(true);
	        		geofencingCallbacks.sendPluginResult(result);
	            }
			});
    	}
	}
	
	private void errorCallback(final JSONObject returnInfo)
	{
    	if (geofencingCallbacks == null)
    	{
			JSONObject returnErrorInfo = new JSONObject();
			try
			{
				returnErrorInfo.put("code", DGLocationStatus.PERMISSIONDENIED.getStatusCode());
				returnErrorInfo.put("message", "At first, Please call initCallbackForRegionMonitoring().");
			}
			catch (JSONException ex)
			{
				currentCallbacks.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, ex.toString()));
				return;
			}
			
			currentCallbacks.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, returnErrorInfo));
    	}
    	else
    	{
    		cordova.getThreadPool().execute(new Runnable() {
	            public void run() {

	            	PluginResult result = new PluginResult(PluginResult.Status.ERROR, returnInfo);
	        		result.setKeepCallback(true);
	        		geofencingCallbacks.sendPluginResult(result);
	            }
			});
    	}
	}
	
	private void errorCallback(final String returnInfo)
	{
    	if (geofencingCallbacks == null)
    	{
			JSONObject returnErrorInfo = new JSONObject();
			try
			{
				returnErrorInfo.put("code", DGLocationStatus.PERMISSIONDENIED.getStatusCode());
				returnErrorInfo.put("message", "At first, Please call initCallbackForRegionMonitoring().");
			}
			catch (JSONException ex)
			{
				currentCallbacks.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, ex.toString()));
				return;
			}
			
			currentCallbacks.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, returnErrorInfo));
    	}
    	else
    	{
    		cordova.getThreadPool().execute(new Runnable() {
	            public void run() {
	            	
	        		PluginResult result = new PluginResult(PluginResult.Status.ERROR, returnInfo);
	        		result.setKeepCallback(true);
	        		geofencingCallbacks.sendPluginResult(result);
	            }
			});
    	}
	}
	
	private void startListening()
	{
		if(!listening)
		{
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, INTERFAL_TIME, MIN_DISTANCE, this);
			listening = true;
		}
	}
	
	private void stopListeningIfNoListeners()
	{
		if(listeners.size() == 0 && listening)
		{
			locationManager.removeUpdates(this);
			listening = false;
		}
	}

	@Override
	public void onLocationChanged(Location location)
	{
	    String text = String.format(Locale.getDefault(), "\nLat:\t %f\nLong:\t %f\nAlt:\t %f\nBearing:\t %f", location.getLatitude(),
	            location.getLongitude(), location.getAltitude(), location.getBearing());
	    Log.d(TAG, "onLocationChanged with location " + text);
	
	    for (LocationChangedListener changedListener : listeners) {
	    	changedListener.onLocationChanged(location);
	    }
	}

	@Override
	public void onStatusChanged(String s, int i, Bundle bundle)
	{
	}

	@Override
	public void onProviderEnabled(String s)
	{
	}

	@Override
	public void onProviderDisabled(String s)
	{
	}
}
