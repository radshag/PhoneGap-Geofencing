/**
 * Geofencing.js
 *
 * Phonegap Geofencing Plugin
 * Copyright (c) Dov Goldberg 2012
 * http://www.ogonium.com
 * dov.goldberg@ogonium.com
 *
 */
var DGGeofencing = {
/*
     Params:
     NONE
     */
	initCallbackForRegionMonitoring: function(params, success, fail) {
		return Cordova.exec(success, fail, "DGGeofencing", "initCallbackForRegionMonitoring", params);
	},

/*
     Params:
     #define KEY_REGION_ID       @"fid"
     #define KEY_REGION_LAT      @"latitude"
     #define KEY_REGION_LNG      @"longitude"
     #define KEY_REGION_RADIUS   @"radius"
     #define KEY_REGION_ACCURACY @"accuracy"
     */
	startMonitoringRegion: function(params, success, fail) {
		return Cordova.exec(success, fail, "DGGeofencing", "startMonitoringRegion", params);
	},

/*
	Params:
	#define KEY_REGION_ID      @"fid"
	#define KEY_REGION_LAT     @"latitude"
    #define KEY_REGION_LNG     @"longitude"
	*/
	stopMonitoringRegion: function(params, success, fail) {
		return Cordova.exec(success, fail, "DGGeofencing", "stopMonitoringRegion", params);
	},

/*
	Params:
	NONE
	*/
	getWatchedRegionIds: function(success, fail) {
		return Cordova.exec(success, fail, "DGGeofencing", "getWatchedRegionIds", []);
	},

/*
	Params:
	NONE
	*/
	getPendingRegionUpdates: function(success, fail) {
		return Cordova.exec(success, fail, "DGGeofencing", "getPendingRegionUpdates", []);
	},

/*
	Params:
	NONE
	*/
	startMonitoringSignificantLocationChanges: function(success, fail) {
		return Cordova.exec(success, fail, "DGGeofencing", "startMonitoringSignificantLocationChanges", []);
	},

/*
	Params:
	NONE
	*/
	stopMonitoringSignificantLocationChanges: function(success, fail) {
		return Cordova.exec(success, fail, "DGGeofencing", "stopMonitoringSignificantLocationChanges", []);
	}
};
