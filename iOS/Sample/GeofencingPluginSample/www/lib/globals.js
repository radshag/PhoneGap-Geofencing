// JavaScript Document
// 


// Persistence Models
var Region = persistence.define('Region', {
  fid: "TEXT",
  accuracy: "INT",
  radius: "INT",
  latitude: "TEXT",
  longitude: "TEXT",
  currentlyHere: "TEXT",
  address: "TEXT",
  name: "TEXT"
});

$('#mainPage').live("pageshow", function() {
	console.log("page show");
	persistence.store.websql.config(persistence, 'regiontracker', 'Region Tracker DB', 5 * 1024 * 1024);
	persistence.schemaSync(function(tx) { 
		var regions = Region.all(); // Returns QueryCollection of all Projects in Database
		regions.list(null, function (results) {
			var list = $( "#mainPage" ).find( ".lstMyRegions" );
			//Empty current list
	        list.empty();
			//Use template to create items & add to list
			$( "#regionItem" ).tmpl( results ).appendTo( list );
			//Call the listview jQuery UI Widget after adding 
			//items to the list allowing correct rendering
			list.listview( "refresh" );
		});
	});
});

$('#projectOptions').live("pageshow", function() {
    $.mobile.showPageLoadingMsg();
	$( "#projectOptions" ).find( ".ui-title" ).html("Loading Project...");
	
	Project.findBy("fid", $.mobile.pageData.fid, function(project) {
		$( "#projectOptions" ).find( ".ui-title" ).html(project.name);
		
		//MIKE TODO - Add Check for notifications here. I hardcoded true for now...
		var shouldnotify = true;
		var params = {"fid": project.fid};
        DGPTimeTracker.getShouldAutoUpdateProjectEvents(
            params,
			function(result) {
				shouldnotify = (result.shouldautoupdate == 0) ? false:true;  
				$( "#notifyState" ).val((shouldnotify) ? "on" : "off").change();
				var here = result.currentlyhere;
		        if(here) {
		            $("#btnCheckin .ui-btn-text").text("Check Out");
		        } else {
		            $("#btnCheckin .ui-btn-text").text("Check In");
		        }
				$.mobile.hidePageLoadingMsg();
			},
			function(error) {
				console.log("Error : \r\n"+error); 
				$( "#notifyState" ).val((shouldnotify) ? "on" : "off").change();
				$("#btnCheckin .ui-btn-text").text("Check In");   
		        $.mobile.hidePageLoadingMsg();  
			}
		);
	});               
});
						
$('#map_page').live("pageshow", function() {
	$('#map_canvas').gmap(
		{ 'center' : new google.maps.LatLng(currentLocation.location.lat, currentLocation.location.lng), 
		  'mapTypeControl' : true
		}
	);
                    
	$('#map_canvas').gmap('refresh');
    
    
	$('#map_canvas').gmap('clear', 'markers');

	var marker = $('#map_canvas').gmap(
		'addMarker',{ id:'m_1', 'position':  new google.maps.LatLng(currentLocation.location.lat, currentLocation.location.lng), 'bounds': true , 'animation' : 	google.maps.Animation.DROP}
	)
	.click(function() {
		$('#map_canvas').gmap('openInfoWindow', { 'content': currentLocation.name }, this);
	});
});