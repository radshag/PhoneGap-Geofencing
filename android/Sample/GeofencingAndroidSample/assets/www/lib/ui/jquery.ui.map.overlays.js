 /*!
 * jQuery UI Google Map 3.0-beta
 * http://code.google.com/p/jquery-ui-map/
 * Copyright (c) 2010 - 2012 Johan Säll Larsson
 * Licensed under the MIT license: http://www.opensource.org/licenses/mit-license.php
 *
 * Depends:
 *      jquery.ui.map.js
 */
( function($) {

	$.extend($.ui.gmap.prototype, {
		
		/**
		 * Adds a shape to the map
		 * @param type:string Polygon, Polyline, Rectangle, Circle
		 * @param options:object
		 * @return object
		 */
		addShape: function(a, b) {
			return $(this.get('overlays > ' + a, []).push(new google.maps[a](jQuery.extend({'map': this.get('map')}, b))));
		},
		
		/**
		 * Adds fusion data to the map.
		 * @param fusionTableOptions:google.maps.FusionTablesLayerOptions, http://code.google.com/intl/sv-SE/apis/maps/documentation/javascript/reference.html#FusionTablesLayerOptions
		 * @param fusionTableId:int
		 */
		loadFusion: function(a, b) {
			( (!b) ? this.get('overlays > FusionTablesLayer', new google.maps.FusionTablesLayer()) : this.get('overlays > FusionTablesLayer', new google.maps.FusionTablesLayer(b, a)) ).setOptions(jQuery.extend({'map': this.get('map') }, a));
		},
		
		/**
		 * Adds markers from KML file or GeoRSS feed
		 * @param uid:String - an identifier for the RSS e.g. 'rss_dogs'
		 * @param url:String - URL to feed
		 * @param kmlLayerOptions:google.maps.KmlLayerOptions, http://code.google.com/intl/sv-SE/apis/maps/documentation/javascript/reference.html#KmlLayerOptions
		 */
		loadKML: function(a, b, c) {
			this.get('overlays > ' + a, new google.maps.KmlLayer(b, jQuery.extend({'map': this.get('map')}, c)));
		}
	
	});
	
} (jQuery) );