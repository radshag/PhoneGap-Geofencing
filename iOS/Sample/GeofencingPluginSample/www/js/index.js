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
    }
};

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
function onRetrieveLocationSuccess(position){
	currentLandL = position.coords.latitude+","+position.coords.longitude;
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
			$( "#locationItem" ).tmpl( nearbyLocations ).appendTo( list );
			//Call the listview jQuery UI Widget after adding 
			//items to the list allowing correct rendering
			list.listview( "refresh" ); 
		},
		onFailure: function(data) {
			alert('Failed to retrieve locations. Please Try again. : ' + data.response);
			$.mobile.hidePageLoadingMsg();
		}
	});
}
function onRetrieveLocationError(){
	$.mobile.hidePageLoadingMsg();
	//alert('code: '    + error.code    + '\n' +
	//	  'message: ' + error.message + '\n');
	alert("Please enable location services for Region Tracker in your Settings App and then try again.");
} 
function clearNearbyLocations() {
	var list = $( "#selectlocation .locationlist" );
    //Empty current list
    list.empty();
	//Call the listview jQuery UI Widget after adding 
	//items to the list allowing correct rendering
	list.listview( "refresh" );
} 

function doSelectLocation(id) {
	// Check if project already exists
	var regions = Region.all(); // Returns QueryCollection of all Regions in Database
	var boolRegionExists = false;
	regions.list(null, function (results) {
        $(results).each(function(index, item){
            if (id == item.fid) {
                $.mobile.changePage("#mainPage");
                boolRegionExists = true;
            }
        });
		if(!boolRegionExists) {
            $(nearbyLocations).each(function(index, item){
				if( id == item.id) {
					currentLocation = item;
                	doAddLocation();
					return;
				}
			});	
		}
	});
}

function doAddLocation() {
	$.mobile.showPageLoadingMsg();


	// Send Add to Native Code for Region Monitoring
	var params = {"fid": currentLocation.id, "radius": 15, "latitude": currentLocation.location.lat, "longitude": currentLocation.location.lng, "accuracy": ""};
	DGGeofencing.addRegion(
		params,
		function(result) { 
			var region = new Region();
			region.fid = currentLocation.id;
			region.name = currentLocation.name;
			region.accuracy = 0;
			region.radius = 15;
			region.address = currentLocation.location.address;
			region.latitude = currentLocation.location.lat;
			region.longitude = currentLocation.location.lng;
			region.currentlyHere = false;
		    persistence.add(region); 
		    persistence.flush(function() {
			  	$.mobile.changePage("#mainPage");	
				$.mobile.hidePageLoadingMsg();
			});   
      	},
      	function(error) {   
	  		alert("failed to add region");
      	}
	);

}
    
function showMapForLocation(id) {
//        $(nearbyLocations).each(function(index, item){
//			if( id == item.id) {
//				currentLocation = item;
//				$.mobile.changePage("#map_page");
//				return;
//			}
//		});
}