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
	#define KEY_REGION_ID       @"fid"
	#define KEY_REGION_LAT      @"latitude"
    #define KEY_REGION_LNG      @"longitude"
    #define KEY_REGION_RADIUS   @"radius"
    #define KEY_REGION_ACCURACY @"accuracy"
	*/
     addRegion: function(params, success, fail) {
          return PhoneGap.exec(success, fail, "DGGeofencing", "addRegion", [params]);
     },

     /*
	Params:
	#define KEY_REGION_ID      @"fid"
	#define KEY_REGION_LAT     @"latitude"
    #define KEY_REGION_LNG     @"longitude"
	*/
     removeRegion: function(params, success, fail) {
          return PhoneGap.exec(success, fail, "DGGeofencing", "removeRegion", [params]);
     },
     /*
	Params:
	NONE
	*/
	getWatchedRegionIds: function(success, fail) {
		return PhoneGap.exec(success, fail, "DGGeofencing", "getWatchedRegionIds", []);
	}
};