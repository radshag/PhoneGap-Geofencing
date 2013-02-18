 /*!
 * jQuery UI Google Map 3.0-alpha
 * http://code.google.com/p/jquery-ui-map/
 * Copyright (c) 2010 - 2011 Johan Säll Larsson
 * Licensed under the MIT license: http://www.opensource.org/licenses/mit-license.php
 *
 * Depends:
 *      jquery.ui.map.js
 */
( function($) {

	$.extend($.ui.gmap.prototype, {
		 
		/**
		 * Gets the current position
		 * @param callback:function(position, status)
		 * @param geoPositionOptions:object, see https://developer.mozilla.org/en/XPCOM_Interface_Reference/nsIDOMGeoPositionOptions
		 */
		getCurrentPosition: function(a, b) {
			var c = this;
			if ( navigator.geolocation ) {
				navigator.geolocation.getCurrentPosition ( 
					function(d) {
						c._call(a, d, "OK");
					}, 
					function(error) {
						c._call(a, null, error);
					}, 
					b 
				);	
			} else {
				c._call(a, null, "NOT_SUPPORTED");
			}
		},
		
		/**
		 * Watches current position
		 * To clear watch, call navigator.geolocation.clearWatch(this.get('watch'));
		 * @param callback:function(position, status)
		 * @param geoPositionOptions:object, see https://developer.mozilla.org/en/XPCOM_Interface_Reference/nsIDOMGeoPositionOptions
		 */
		watchPosition: function(a, b) {
			var c = this;
			if ( navigator.geolocation ) {
				this.set('watch', navigator.geolocation.watchPosition ( 
					function(d) {
						c._call(a, d, "OK");
					}, 
					function(error) {
						c._call(a, null, error);
					}, 
					b 
				));	
			} else {
				c._call(a, null, "NOT_SUPPORTED");
			}
		},

		/**
		 * Clears any watches
		 */
		clearWatch: function() {
			if ( navigator.geolocation ) {
				navigator.geolocation.clearWatch(this.get('watch'));
			}
		},
		
		/**
		 * Autocomplete using Google Geocoder
		 * @param panel:string/node/jquery
		 * @param callback:function(results, status)
		 */
		autocomplete: function(a, b) {
			var self = this;
			$(this._unwrap(a)).autocomplete({
				source: function( request, response ) {
					self.search({'address':request.term}, function(results, status) {
						if ( status === 'OK' ) {
							response( $.map( results, function(item) {
								return { label: item.formatted_address, value: item.formatted_address, position: item.geometry.location }
							}));
						} else if ( status === 'OVER_QUERY_LIMIT' ) {
							alert('Google said it\'s too much!');
						}
					});
				},
				minLength: 3,
				select: function(event, ui) { 
					self._call(b, ui);
				},
				open: function() { $( this ).removeClass( "ui-corner-all" ).addClass( "ui-corner-top" ); },
				close: function() { $( this ).removeClass( "ui-corner-top" ).addClass( "ui-corner-all" ); }
			});
		},
		
		/**
		 * Retrieves a list of Places in a given area. The PlaceResultss passed to the callback are stripped-down versions of a full PlaceResult. A more detailed PlaceResult for each Place can be obtained by sending a Place Details request with the desired Place's reference value.
		 * @param a:google.maps.places.PlaceSearchRequest, http://code.google.com/apis/maps/documentation/javascript/reference.html#PlaceSearchRequest
		 * @param b:function(result:google.maps.places.PlaceResult, status:google.maps.places.PlacesServiceStatus), http://code.google.com/apis/maps/documentation/javascript/reference.html#PlaceResult
		 */
		placesSearch: function(a, b) {
			this.get('services > PlacesService', new google.maps.places.PlacesService(this.get('map'))).search(a, b);
		},
		
		/**
		 * Clears any directions
		 */
		clearDirections: function() {
			var a = this.get('services > DirectionsRenderer');
			if (a) {
				a.setMap(null);
				a.setPanel(null);
			}
		}
		
		/**
		 * A layer that displays data from Panoramio.
		 * @param panoramioLayerOptions:google.maps.panoramio.PanoramioLayerOptions, http://code.google.com/apis/maps/documentation/javascript/reference.html#PanoramioLayerOptions
		 */
		/*loadPanoramio: function(a) {
			if ( !this.get('overlays').PanoramioLayer ) {
				this.get('overlays').PanoramioLayer = new google.maps.panoramio.PanoramioLayer();
			}
			this.get('overlays').PanoramioLayer.setOptions(jQuery.extend({'map': this.get('map') }, a));
		},*/
		
		/**
		 * Makes an elevation request along a path, where the elevation data are returned as distance-based samples along that path.
		 * @param a:google.maps.PathElevationRequest, http://code.google.com/apis/maps/documentation/javascript/reference.html#PathElevationRequest
		 * @param b:function(result:google.maps.ElevationResult, status:google.maps.ElevationStatus), http://code.google.com/intl/sv-SE/apis/maps/documentation/javascript/reference.html#ElevationResult
		 */
		/*elevationPath: function(a, b) {
			this.get('services > ElevationService', new google.maps.ElevationService()).getElevationAlongPath(a, b);
		},*/
		
		/**
		 * Makes an elevation request for a list of discrete locations.
		 * @param a:google.maps.PathElevationRequest, http://code.google.com/apis/maps/documentation/javascript/reference.html#PathElevationRequest
		 * @param b:function(result:google.maps.ElevationResult, status:google.maps.ElevationStatus), http://code.google.com/intl/sv-SE/apis/maps/documentation/javascript/reference.html#ElevationResult
		 */
		/*elevationLocations: function(a, b) {
			this.get('services > ElevationService', new google.maps.ElevationService()).getElevationForLocations(a, b);
		},*/
		
		/* PLACES SERVICE */		
		
		/**
		 * Retrieves a list of Places in a given area. The PlaceResultss passed to the callback are stripped-down versions of a full PlaceResult. A more detailed PlaceResult for each Place can be obtained by sending a Place Details request with the desired Place's reference value.
		 * @param a:google.maps.places.PlaceSearchRequest, http://code.google.com/apis/maps/documentation/javascript/reference.html#PlaceSearchRequest
		 * @param b:function(result:google.maps.places.PlaceResult, status:google.maps.places.PlacesServiceStatus), http://code.google.com/apis/maps/documentation/javascript/reference.html#PlaceResult
		 */
		/*placesSearch: function(a, b) {
			this.get('services > PlacesService', new google.maps.places.PlacesService(this.get('map'))).search(a, b);
		},*/
		
		/**
		 * Retrieves details about the Place identified by the given reference.
		 * @param a:google.maps.places.PlaceDetailsRequest, http://code.google.com/apis/maps/documentation/javascript/reference.html#PlaceDetailsRequest
		 * @param b:function(result:google.maps.places.PlaceResult, status:google.maps.places.PlacesServiceStatus), http://code.google.com/apis/maps/documentation/javascript/reference.html#PlaceResult
		 */
		/*placesDetails: function(a, b) {
			this.get('services > PlacesService', new google.maps.places.PlacesService(this.get('map'))).getDetails(a, b);
		},*/
		
		/**
		 * A service to predict the desired Place based on user input. The service is attached to an <input> field in the form of a drop-down list. The list of predictions is updated dynamically as text is typed into the input field. 
		 * @param a:jquery/node/string
		 * @param b:google.maps.places.AutocompleteOptions, http://code.google.com/apis/maps/documentation/javascript/reference.html#AutocompleteOptions
		 */		
		/*placesAutocomplete: function(a, b) {
			this.get('services > Autocomplete', new google.maps.places.Autocomplete(this._unwrap(a)));
		},*/
		
		/* DISTANCE MATRIX SERVICE */
		
		/**
		 * Issues a distance matrix request.
		 * @param a:google.maps.DistanceMatrixRequest, http://code.google.com/apis/maps/documentation/javascript/reference.html#DistanceMatrixRequest 
		 * @param b:function(result:google.maps.DistanceMatrixResponse, status: google.maps.DistanceMatrixStatus), http://code.google.com/apis/maps/documentation/javascript/reference.html#DistanceMatrixResponse
		 */
		/*displayDistanceMatrix: function(a, b) {
			this.get('services > DistanceMatrixService', new google.maps.DistanceMatrixService()).getDistanceMatrix(a, b);
		}*/
	
	});
	
} (jQuery) );