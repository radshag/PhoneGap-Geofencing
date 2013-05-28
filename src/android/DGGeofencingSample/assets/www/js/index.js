/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
var app = {
	// Application Constructor
	initialize: function() {
		this.bindEvents();
	},
	// Bind Event Listeners
	//
	// Bind any events that are required on startup. Common events are:
	// `load`, `deviceready`, `offline`, and `online`.
	bindEvents: function() {
		document.addEventListener('deviceready', this.onDeviceReady, false);
	},
	// deviceready Event Handler
	//
	// The scope of `this` is the event. In order to call the `receivedEvent`
	// function, we must explicity call `app.receivedEvent(...);`
	onDeviceReady: function() {
		fsclient = new FourSquareClient(fsAPI_KEY, fsAPI_SECRET, "", true);

		console.log("doc ready");

		persistence.store.websql.config(persistence, 'regiontracker', 'Region Tracker DB', 5 * 1024 * 1024);
		persistence.schemaSync(function(tx) {

		});

		DGGeofencing.initCallbackForRegionMonitoring(new Array(), processRegionMonitorCallback, function(error) {
			console.log("init error");
		});
	}
};

processRegionMonitorCallback = function(result) {
	var callbacktype = result.callbacktype;
	if (callbacktype == "initmonitor") {
		
		console.log("initmonitor");
		
	} else if (callbacktype == "locationupdate") {// monitor for region with id fid removed
		
		var fid = result.regionId;
		console.log("locationupdate");
		
		var new_timestamp = result.new_timestamp;
		var new_speed = result.new_speed;
		var new_course = result.new_course;
		var new_verticalAccuracy = result.new_verticalAccuracy;
		var new_horizontalAccuracy = result.new_horizontalAccuracy;
		var new_altitude = result.new_altitude;
		var new_latitude = result.new_latitude;
		var new_longitude = result.new_longitude;

		var old_timestamp = result.old_timestamp;
		var old_speed = result.old_speed;
		var old_course = result.old_course;
		var old_verticalAccuracy = result.old_verticalAccuracy;
		var old_horizontalAccuracy = result.old_horizontalAccuracy;
		var old_altitude = result.old_altitude;
		var old_latitude = result.old_latitude;
		var old_longitude = result.old_longitude;

		console.log("Location Update Event: " + event);
        
	} else if (callbacktype == "monitorremoved") {// monitor for region with id fid removed
		
		var fid = result.regionId;
		console.log("monitorremoved: " + fid);
		var regions = Region.all().filter("fid", '=', fid);
		regions.list(null, function(results) {
                     $(results).each(function(index, item) {
                                     if (fid == item.fid) {
                                     persistence.remove(item);
                                     persistence.flush(function() {
                                                       var regions = Region.all(); // Returns QueryCollection of all Projects in Database
                                                       regions.list(null, function(results) {
                                                                    var list = $("#mainPage").find(".lstMyRegions");
                                                                    //Empty current list
                                                                    list.empty();
                                                                    //Use template to create items & add to list
                                                                    $("#regionItem").tmpl(results).appendTo(list);
                                                                    //Call the listview jQuery UI Widget after adding
                                                                    //items to the list allowing correct rendering
                                                                    list.listview("refresh");
                                                                    });
                                                       $.mobile.hidePageLoadingMsg();
                                                       });
                                     }
                                     });
                     });
        
	} else if (callbacktype == "monitorfail") {// monitor for region with id fid failed

		var fid = result.regionId;
		console.log("monitorfail");

	} else if (callbacktype == "monitorstart") { // monitor for region with id fid succeeded
		
		console.log("monitorstart");
		var region = new Region();
		region.fid = currentLocation.id;
		region.name = currentLocation.name;
		region.accuracy = 0;
		region.radius = 15;
		region.address = currentLocation.location.address;
		region.latitude = currentLocation.location.lat;
		region.longitude = currentLocation.location.lng;
		var here = confirm('Are you already at ' + currentLocation.name + '?');
		if (here) {
			region.currentlyHere = "yes";
		} else {
			region.currentlyHere = "no";
		}
		persistence.add(region);
		persistence.flush(function() {
			console.log("persistence flush success");
			$.mobile.changePage("#mainPage");
			$.mobile.hidePageLoadingMsg();
		});
		
	} else { // enter or exit region processing
		
		var fid = result.regionId;
		var status = callbacktype;
		var regions = Region.all().filter("fid", '=', fid);
		regions.list(null, function(results) {
			$(results).each(function(index, item) {
				if (fid == item.fid) {
					if (status == "enter") {
						item.currentlyHere = "yes";
					} else {
						item.currentlyHere = "no";
					}
				}
			});
		});
		persistence.flush(function() {
			var regions = Region.all(); // Returns QueryCollection of all Projects in Database
			regions.list(null, function(results) {
				var list = $("#mainPage").find(".lstMyRegions");
				//Empty current list
				list.empty();
				//Use template to create items & add to list
				$("#regionItem").tmpl(results).appendTo(list);
				//Call the listview jQuery UI Widget after adding
				//items to the list allowing correct rendering
				list.listview("refresh");
			});
		});
	}
	
}

// Global Variables
var root = this;
var fsclient
var currentLocation;
var currentLandL;
var nearbyLocations;

// Set this to your FourSquare API Key and Secret
var fsAPI_KEY = "1OYPMZW55HEI5CHOJ0AH4EGJATOF0TQD3Z03PRNAJZIKWTPM";
var fsAPI_SECRET = "HG4IHVAI4E01RFR135PLJ5TERNKYTDAGQWG0VUSRWEGIKLIG";

function retrieveLocations() {
	$.mobile.showPageLoadingMsg();
	navigator.geolocation.getCurrentPosition(onRetrieveLocationSuccess, onRetrieveLocationError);
}

function onRetrieveLocationSuccess(position) {
	currentLandL = position.coords.latitude + "," + position.coords.longitude;

	var parameters = {
		ll: currentLandL,
		limit: '10',
		radius: '550'
	};
	fsclient.venuesClient.search(parameters, {
		onSuccess: function(data) {
			nearbyLocations = data.response.venues;
			$.mobile.changePage("#selectlocation");
			clearNearbyLocations();
			$.mobile.hidePageLoadingMsg();
			var list = $("#selectlocation .locationlist");
			//Use template to create items & add to list
			$("#locationItem").tmpl(nearbyLocations).appendTo(list);
			//Call the listview jQuery UI Widget after adding 
			//items to the list allowing correct rendering
			list.listview("refresh");
		},
		onFailure: function(data) {
			alert('Failed to retrieve locations. Please Try again. : ' + data.response);
			$.mobile.hidePageLoadingMsg();
		}
	});
}

function onRetrieveLocationError() {
	$.mobile.hidePageLoadingMsg();
	//alert('code: '    + error.code    + '\n' +
	//	  'message: ' + error.message + '\n');
	alert("Please enable location services for Region Tracker in your Settings App and then try again.");
}

function clearNearbyLocations() {
	var list = $("#selectlocation .locationlist");
	//Empty current list
	list.empty();
	//Call the listview jQuery UI Widget after adding 
	//items to the list allowing correct rendering
	list.listview("refresh");
}

function doSelectLocation(id) {
	// Check if project already exists
	var regions = Region.all(); // Returns QueryCollection of all Regions in Database
	var boolRegionExists = false;
	regions.list(null, function(results) {
		$(results).each(function(index, item) {
			if (id == item.fid) {
				$.mobile.changePage("#mainPage");
				boolRegionExists = true;
			}
		});
		if (!boolRegionExists) {
			$(nearbyLocations).each(function(index, item) {
				if (id == item.id) {
					currentLocation = item;
					doAddLocation(item);
					return;
				}
			});
		}
	});
}

function doAddLocation(location) {
	$.mobile.showPageLoadingMsg();

	var params = [location.id, location.location.lat, location.location.lng, "10", "3"];
	DGGeofencing.startMonitoringRegion(params, function(result) {}, function(error) {
		alert("failed to add region");
	});
}



function deleteRegion(id) {
	var regions = Region.all().filter("fid", '=', id);
	regions.list(null, function(results) {
		$(results).each(function(index, item) {
			if (id == item.fid) {
				var params = [item.fid, item.latitude, item.longitude];
				DGGeofencing.stopMonitoringRegion(
				params, function(result) {

					// not used.

				}, function(error) {
					// not used
				});
			}
		});
	});
}

function showMapForLocation(id) {
	$(nearbyLocations).each(function(index, item) {
		if (id == item.id) {
			currentLocation = item;
			$.mobile.changePage("#map_page");
			return;
		}
	});
}
