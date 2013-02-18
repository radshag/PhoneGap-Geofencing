 /*!
 * jQuery FN Google Map 3.0-beta
 * http://code.google.com/p/jquery-ui-map/
 * Copyright (c) 2010 - 2012 Johan Säll Larsson
 * Licensed under the MIT license: http://www.opensource.org/licenses/mit-license.php
 */
( function($) {
	
	/**
	 * This is how you write unmaintainable code :) - the size is small though.
	 * @param namespace:string
	 * @param name:string
	 * @param base:object
	 */
	$.a = function(a, b, c) {
		$[a] = $[a] || {};
		$[a][b] = function(a, b) {
			if ( arguments.length ) {
				this._s(a, b);
			}
		};
		$[a][b].prototype = c;
		$.fn[b] = function(d) {
			var e = this, f = Array.prototype.slice.call(arguments, 1), g = typeof d === 'string';
			if ( g && d.substring(0, 1) === '_' ) { return e; }
			this.each(function() {
				var h = $.data(this, b);
				if ( !h ) {
					h = $.data(this, b, new $[a][b](d, this));
				}
				if ( g ) {
					var i = h[d].apply(h, f);
					if (i != null) {
						e = i;
					}
				}
			});
			return e;  
		};
	};
	
	$.a("ui", "gmap", {
		
		/**
		 * Map options
		 * @see http://code.google.com/intl/sv-SE/apis/maps/documentation/javascript/reference.html#MapOptions
		 */
		options: {
			mapTypeId: 'roadmap',
			zoom: 5	
		},
		
		/**
		 * Get or set options
		 * @param key:string
		 * @param options:object
		 * @return object
		 */
		option: function(a, b) {
			if (b) {
				this.options[a] = b;
				this.get('map').setOptions(this.options);
			}
			return this.options[a];
		},
		
		/**
		 * Setup plugin basics, 
		 * @el is the jQuery element
		 * @options is the 
		 */
		_s: function(a, b) {
			this.el = $(b);
			jQuery.extend(this.options, a);
			this.options.center = this._latLng(this.options.center);
			this._create();
			if ( this._init ) { this._init(); }
		},
		
		/**
		 * Instanciate the Google Maps object
		 */
		_create: function() {
			var a = this; 
			a._a = { 'map': new google.maps.Map(a.el[0], a.options), 'markers': [], 'overlays': [], 'services': [], 'iw': new google.maps.InfoWindow };
			google.maps.event.addListenerOnce(a._a.map, 'bounds_changed', function() { a.el.trigger('init', a._a.map); });
			a._call(a.options.callback, a._a.map);
		},
		
		/**
		 * Adds a latitude longitude pair to the bounds.
		 * @param position:google.maps.LatLng/string
		 */
		addBounds: function(a) {
			this.get('bounds', new google.maps.LatLngBounds()).extend(this._latLng(a));
			this.get('map').fitBounds(this.get('bounds'));
		},
		
		/**
		 * Helper function to check if a LatLng is within the viewport
		 * @param marker:google.maps.Marker
		 */
		inViewport: function(a) {
			var b = this.get('map').getBounds();
			return (b) ? b.contains(a.getPosition()) : false;
		},
		
		/**
		 * Adds a custom control to the map
		 * @param panel:jquery/node/string	
		 * @param position:google.maps.ControlPosition	 
		 * @see http://code.google.com/intl/sv-SE/apis/maps/documentation/javascript/reference.html#ControlPosition
		 */
		addControl: function(a, b) {
			this.get('map').controls[b].push(this._unwrap(a));
		},
		
		/**
		 * Adds a Marker to the map
		 * @param markerOptions:google.maps.MarkerOptions
		 * @param callback:function(map:google.maps.Map, marker:google.maps.Marker) (optional)
		 * @param marker:function (optional)
		 * @return $(google.maps.Marker)
		 * @see http://code.google.com/intl/sv-SE/apis/maps/documentation/javascript/reference.html#MarkerOptions
		 */
		addMarker: function(a, b, c) {
			a.map = this.get('map');
			a.position = this._latLng(a.position);
			var d = new (c || google.maps.Marker)(a);
			var f = this.get('markers');
			if ( d.id ) {
				f[d.id] = d;
			} else {
				f.push(d);
			}
			if ( d.bounds ) {
				this.addBounds(d.getPosition());
			}
			this._call(b, a.map, d);
			return $(d);
		},
		
		/**
		 * Clears by type
		 * @param type:string i.e. 'markers', 'overlays', 'services'
		 */
		clear: function(a) {
			this._c(this.get(a));
			this.set(a, []);
		},
		
		_c: function(a) {
			for ( b in a ) {
				if ( a.hasOwnProperty(b) ) {
					if ( a[b] instanceof google.maps.MVCObject ) {
						google.maps.event.clearInstanceListeners(a[b]);
						a[b].setMap(null);
					} else if ( a[b] instanceof Array ) {
						this._c(a[b]);
					}
					a[b] = null;
				}
			}
		},
		
		/**
		 * Returns the objects with a specific property and value, e.g. 'category', 'tags'
		 * @param context:string	in what context, e.g. 'markers' 
		 * @param options:object	property:string	the property to search within, value:string, delimiter:string (optional)
		 * @param callback:function(marker:google.maps.Marker, isFound:boolean)
		 */
		find: function(a, b, c) {
			var d = this.get(a);
			for ( e in d ) {
				if ( d.hasOwnProperty(e) ) {
					c(d[e], (( b.delimiter && d[e][b.property] ) ? ( d[e][b.property].split(b.delimiter).indexOf(b.value) > -1 ) : ( d[e][b.property] === b.value )));
				}
			};
		},

		/**
		 * Returns an instance property by key. Has the ability to set an object if the property does not exist
		 * @param key:string
		 * @param value:object(optional)
		 */
		get: function(a, b) {
			var c = this._a;
			if (!c[a]) {
				if ( a.indexOf('>') > -1 ) {
					var e = a.replace(/ /g, '').split('>');
					for ( var i = 0; i < e.length; i++ ) {
						if ( !c[e[i]] ) {
							if (b) {
								c[e[i]] = ( (i + 1) < e.length ) ? [] : b;
							} else {
								return null;
							}
						}
						c = c[e[i]];
					}
					return c;
				} else if ( b && !c[a] ) {
					this.set(a, b);
				}
			}
			return c[a];
		},
		
		/**
		 * Triggers an InfoWindow to open
		 * @param infoWindowOptions:google.maps.InfoWindowOptions
		 * @param marker:google.maps.Marker (optional)
		 * @see http://code.google.com/intl/sv-SE/apis/maps/documentation/javascript/reference.html#InfoWindowOptions
		 */
		openInfoWindow: function(a, b) {
			this.get('iw').setOptions(a);
			this.get('iw').open(this.get('map'), this._unwrap(b)); 
		},
				
		/**
		 * Sets an instance property
		 * @param key:string
		 * @param value:object
		 */
		set: function(a, b) {
			this._a[a] = b;
		},
		
		/**
		 * Refreshes the map
		 */
		refresh: function(a) {
			var b = this.get('map');
			var c = b.getCenter();
			$(b).triggerEvent('resize');
			b.setCenter(c);
		},
		
		/**
		 * Destroys the plugin.
		 */
		destroy: function() {
			this.clear('markers');
			this.clear('services');
			this.clear('overlays');
			for ( b in this._a ) {
				this._a[b] = null;
			}
		},
		
		/**
		 * Helper method for calling a function
		 * @param callback
		 */
		_call: function(a) {
			if ( a && $.isFunction(a) ) {
				a.apply(this, Array.prototype.slice.call(arguments, 1));
			}
		},
		
		/**
		 * Helper method for google.maps.Latlng
		 * @param callback
		 */
		_latLng: function(a) {
			if (!a) {
				return new google.maps.LatLng(0.0,0.0);
			}
			if ( a instanceof google.maps.LatLng ) {
				return a;
			} else {
				var b = a.replace(/ /g,'').split(',');
				return new google.maps.LatLng(b[0],b[1]);
			}
		},
		
		/**
		 * Helper method for unwrapping jQuery/DOM/string elements
		 * @param callback
		 */
		_unwrap: function(a) {
			if ( !a ) {
				return null;
			} else if ( a instanceof jQuery ) {
				return a[0];
			} else if ( a instanceof Object ) {
				return a;
			}
			return $('#'+a)[0];
		}
		
	});
	
	jQuery.fn.extend( {
		
		click: function(a, b) { 
			return this.addEventListener('click', a, b);
		},
		
		rightclick: function(a) {
			return this.addEventListener('rightclick', a);
		},
		
		dblclick: function(a, b) {
			return this.addEventListener('dblclick', a, b);
		},
		
		mouseover: function(a, b) {
			return this.addEventListener('mouseover', a, b);
		},
		
		mouseout: function(a, b) {
			return this.addEventListener('mouseout', a, b);
		},
		
		drag: function(a) {
			return this.addEventListener('drag', a );
		},
		
		dragend: function(a) {
			return this.addEventListener('dragend', a );
		},
		
		triggerEvent: function(a) {
			google.maps.event.trigger(this[0], a);		
		},
		
		addEventListener: function(a, b, c) {
			if ( google.maps && this[0] instanceof google.maps.MVCObject ) {
				google.maps.event.addListener(this[0], a, b );
			} else {
				if (c) {
					this.bind(a, b, c);
				} else {
					this.bind(a, b);
				}	
			}
			return this;
		}
		
	});
	
} (jQuery) );