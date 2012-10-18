PhoneGap-Geofencing
===================

Geofencing Plugin For PhoneGap.

## DESCRIPTION ##

* This plugin provides a simple way to use iOS Region Monitoring in PhoneGap applications.
* Simple JS interface is exposed to allow the adding and removing of regions to monitor.
* Included ability to receive realtime region notifications when regions are entered and exited.

## WHAT IS GEOFENCING ##

Geofencing is a way to monitor geographic regions.  In iOS it allows an app to be informed when a specified geographic region is entered or exited.

## SETUP ##

Using this plugin requires [Cordova iOS](https://github.com/apache/incubator-cordova-ios).

1. Make sure your Xcode project has been [updated for Cordova](https://github.com/apache/incubator-cordova-ios/blob/master/guides/Cordova%20Upgrade%20Guide.md)
2. Drag and drop the .h and .m files from the DGGeofencing folder in Finder to your Plugins folder in XCode.
3. Add the .js files to your `www` folder on disk, and add reference(s) to the .js files using <script> tags in your html file(s)

    <script type="text/javascript" src="/js/plugins/Geofencing.js"></script>

4. Add new entry with key `DGGeofencing` and value `DGGeofencing` to `Plugins` in `Cordova.plist/Cordova.plist`

## INCLUDED FUNTIONS ##

DGGeofencing.js contains the following functions:

1. addRegion - Adds a new region to be monitored.
2. removeRegion - Clears an existing region from being monitored.
3. getWatchedRegionIds - Returns a list of currently monitored region identifiers.

## PLUGIN CODE EXAMPLE ##

To add a new region to be monitored use the DGGeofencing addRegion function.
The parameters are:

1. fid - String - This is a unique identifier.
2. radius - Integer - Specifies the radius in meters of the region.
3. latitude - String - latitude of the region.
4. longitude - String - latitude of the region.
5. accuracy - TODO.

Example:

    var params = {"fid": location.id, "radius": 15, "latitude": location.lat, "longitude": location.lng, "accuracy": ""};
	console.log(params);
	DGGeofencing.addRegion(
		params,
		function(result) { 
			console.log("add success");
      	},
      	function(error) {   
	  		alert("failed to add region");
      	}
	);

To remove an existing region use the DGGeofencing removeRegion function.
The parameters are:
1. fid - String - This is a unique identifier.
2. latitude - String - latitude of the region.
3. longitude - String - latitude of the region.

Example:

	var params = {"fid": item.fid, "latitude": item.latitude, "longitude": item.longitude};
	DGGeofencing.removeRegion(
		params,
		function(result) { 
			alert("delete success") 				   
		},
		function(error) {  
			alert("delete error");   
		}
	);

To retrieve the list of identifiers of currently monitored regions use the DGGeofencing getWatchedRegionIds function.
No parameters.

The result object contains an array of strings in regionids 

Example:

	DGGeofencing.getWatchedRegionIds(
		function(result) { 
			alert("success: " + result.regionids) 				   
		},
		function(error) {  
			alert("error");   
		}
	);
	
## HOW TO SETUP REGION NOTIFICATIONS ##



The MIT License

Copyright (c) 2012 Dov Goldberg
EMAIL: dov.goldberg@ogonium.com   
WEBSITE: http://www.ogonium.com

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.