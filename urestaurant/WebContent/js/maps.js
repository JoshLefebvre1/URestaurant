google.maps.visualRefresh = true;
var map;
var directionsDisplay;
var geocoder;
var viewportMarkers = []; // stores the events in the current viewport
var eventList = [];
var infos = [];
var currBounds;
var curUserLocaion;
var eventAddress;



function initMap(eventLocation) {
	var iniLat = 45.4601213;
	var iniLon = -75.7583640;
    var iniZoom = 12;
    directionsDisplay = new google.maps.DirectionsRenderer;
    var iniLatLng = new google.maps.LatLng(iniLat, iniLon);
    var mapOptions = {
            zoom: iniZoom, 
            center: iniLatLng, 
            mapTypeId: google.maps.MapTypeId.ROADMAP, 
            panControl: false,
            zoomControl: true, 
            disableDefaultUI: true,
            navigationControlOptions: { style: google.maps.NavigationControlStyle.SMALL },
            keyboardShortcuts: true,
            streetViewControl: false
        }
   
    if(eventLocation)
    {
    	map = new google.maps.Map(document.getElementById("map_canvas2"), mapOptions);
    	displayMarker(eventLocation);
    	geocoder = new google.maps.Geocoder();
    }
    else
    {
    	map = new google.maps.Map(document.getElementById("map_canvas"), mapOptions);
    	getPublicMapEvents();
    	geocoder = new google.maps.Geocoder();
    }
    	
    
    //if browser geolocation is enabled, center on coordinates
//    if (navigator.geolocation) {
//		navigator.geolocation.getCurrentPosition(function(position) {
//			if (position) {
//			    map.setCenter(new google.maps.LatLng(position.coords.latitude, position.coords.longitude));
//			}			
//		}, null, { timeout : 2000 });
//	}
   // google.maps.event.addListener(map, 'bounds_changed', function() {displayMarkers()});    
}

function getPublicMapEvents() {
	
	$.ajax({
		method: "get",
		dataType: "html",
		url: "/urestaurant/getmapevents",
		async: false
	}).done(function(data) {
		var obj = jQuery.parseJSON(data);
		eventList = obj.events;
		displayMarkers();
	}).error(function(data) {
		alert("8Oops! Sorry we cannot process the request at this time.");
	});
};

function displayMarkers() {
	//clear viewportMarkers from map
	if (viewportMarkers != null) {
        for (i = 0; i < viewportMarkers.length; i++) {
            viewportMarkers[i].setMap(null);
        }
        viewportMarkers = [];
        infos = [];
    }

	geocoder = new google.maps.Geocoder();
	for (var event in eventList){
		//get lat and lon based on address
		(function(event, eventList) {
			geocoder.geocode({
				'address' : eventList[event].address
			}, function(results, status) {
				if (status == google.maps.GeocoderStatus.OK) {
					eventList[event].latitude = results[0].geometry.location.lat().toString();
					eventList[event].longitude = results[0].geometry.location.lng().toString();
					var latLng = new google.maps.LatLng(eventList[event].latitude, eventList[event].longitude);
					
					//currBounds = map.getBounds();
					//check if in bounds, if so create marker
					//if (currBounds == null) return;
			      //  if (currBounds.contains(latLng)) {
					
						var markerIcon;
						if (eventList[event].userEventStatus == "1") markerIcon = 'https://maps.google.com/mapfiles/ms/icons/green-dot.png';
						else if (eventList[event].userEventStatus == "2") markerIcon = 'https://maps.google.com/mapfiles/ms/icons/yellow-dot.png';
						else markerIcon = 'https://maps.google.com/mapfiles/ms/icons/red-dot.png';
					
			        	var marker = new google.maps.Marker({
				            position: latLng,
				            title: eventList[event].name,
				            animation: google.maps.Animation.DROP,
				            icon : markerIcon
				        });
			        	
			        	// create InfoWindow object
			            var info = new google.maps.InfoWindow({
			                content: '<span style="font-family: Trebuchet MS; font-size:10pt; color: maroon"><b>'+ marker.title 
			                +'</b>&nbsp;&nbsp;&nbsp;&nbsp;'+ eventList[event].startDate
			                +'<br/>' + eventList[event].address
			                +'<br/><a href="/urestaurant/PopulateViewEvent?id='+ eventList[event].id + '"><i class="fa fa-eye icon-large"></i></a>'
			                +'&nbsp &nbsp <button id = "dirInfo">GetDirections</button>'

			            });
			            
			            
			        	marker.setMap(map);
			        	viewportMarkers.push(marker);
			        	google.maps.event.addListener(marker, 'click', function () { info.open(map, marker) });
			        	google.maps.event.addListener(info, 'domready', function () 
			        	{
			        		 $('#dirInfo').click(function() 
			        		{
			        			 getDirections(marker.position);	
			        		});	 
			        	});
			       // }   
				} 
			});
		})(event, eventList);
	}
}


function displayMarker(addr) {
	
	var latitude;
	var longitude;
	//clear viewportMarkers from map
	if (viewportMarkers != null) {
        for (i = 0; i < viewportMarkers.length; i++) {
            viewportMarkers[i].setMap(null);
        }
        viewportMarkers = [];
        infos = [];
    }

	geocoder = new google.maps.Geocoder();
		//get lat and lon based on address
		(function() {
			geocoder.geocode({
				'address' : addr
			}, function(results, status) {
				if (status == google.maps.GeocoderStatus.OK) {
					latitude = results[0].geometry.location.lat().toString();
					longitude = results[0].geometry.location.lng().toString();
					var latLng = new google.maps.LatLng(latitude, longitude);
					
					//currBounds = map.getBounds();
					//check if in bounds, if so create marker
					//if (currBounds == null) return;
			      //  if (currBounds.contains(latLng)) {
					
						var markerIcon;
						markerIcon = 'https://maps.google.com/mapfiles/ms/icons/green-dot.png';
						
					
			        	var marker = new google.maps.Marker({
				            position: latLng,
				            title: "",
				            animation: google.maps.Animation.DROP,
				            icon : markerIcon
				        });
			        	
			        	// create InfoWindow object
			            var info = new google.maps.InfoWindow({
			                content: '<span style="font-family: Trebuchet MS; font-size:10pt; color: maroon"><b>'+ marker.title 
			                +'</b>&nbsp;&nbsp;&nbsp;&nbsp;'
			                +'<br/>' + addr
			                
			                +'&nbsp &nbsp <button id = "dirInfo">GetDirections</button>'

			            });
			            
			            
			        	marker.setMap(map);
			        	map.setCenter(latLng);
			        	viewportMarkers.push(marker);
			        	google.maps.event.addListener(marker, 'click', function () { info.open(map, marker) });
			        	google.maps.event.addListener(info, 'domready', function () 
			        	{
			        		 $('#dirInfo').click(function() 
			        		{
			        			 getDirections(marker.position);	
			        		});	 
			        	});
			       // }   
				} 
			});
		})();
	
}


function getDirections(destPosition)
{
	var directionsService = new google.maps.DirectionsService;
	 directionsDisplay.setMap(map);
	 
	 //If current position is set by user
	 if(curUserLocaion != null)
	 {
		 var currentPos = curUserLocaion;
			directionsService.route({
		        origin: currentPos,
		        destination: destPosition,
		        travelMode: google.maps.TravelMode.DRIVING
		      }, function(response, status) {
		        if (status === google.maps.DirectionsStatus.OK) {
		        	//window.alert('Directions request failed due to ' + status);
		          directionsDisplay.setDirections(response);
		        } else {
		          window.alert('Directions request failed due to ' + status);
		        }
		      });
	 }
	 
	 else
	 {
		 if (navigator.geolocation) {
				navigator.geolocation.getCurrentPosition(function(position) {
					if (position) {
						 var currentPos = new google.maps.LatLng(position.coords.latitude, position.coords.longitude);
						// window.alert(currentPos);
						directionsService.route({
					        origin: currentPos,
					        destination: destPosition,
					        travelMode: google.maps.TravelMode.DRIVING
					      }, function(response, status) {
					        if (status === google.maps.DirectionsStatus.OK) {
					        	//window.alert('Directions request failed due to ' + status);
					          directionsDisplay.setDirections(response);
					        } else {
					          window.alert('Directions request failed due to ' + status);
					        }
					      });
						
					    //map.setCenter(new google.maps.LatLng(position.coords.latitude, position.coords.longitude));
					}			
				}, null, { timeout : 2000 });
			} 
	 }
	 
	 
	
}

function handleSearch(e) {

    if (e.keyCode == 13) { locateAddress(); } //enter
    if (e.keyCode == 27) { hideMain(); } //escape
}

function handleCurLocation(e)
{
	if (e.keyCode == 13) { locateCurAddress(); } //enter
	if (e.keyCode == 27) { hideMain(); } //escape
}


function locateAddress() {
	geocoder = new google.maps.Geocoder();
	geocoder.geocode({
		'address' : document.getElementById("address").value
	}, function(results, status) {
		if (status == google.maps.GeocoderStatus.OK) {
			var msg = '';
			if (results.length > 1) {
				for (i = 0; i < results.length; i++) {
					msg += '&nbsp;&nbsp;&nbsp;' + (i + 1).toString()
							+ '.&nbsp;&nbsp;&nbsp; '
							+ '<a href="javascript:gotoLoc'
							+ results[i].geometry.location.toString() + '">'
							+ results[i].formatted_address + '</a><br/>';
				}
				// Make the floating window visible
				document.getElementById("main-locPanel").innerHTML = msg;
				document.getElementById("window-locPanel").style.visibility = 'visible';
				document.getElementById("window-locPanel").style.height = $("#main-locPanel")
						.outerHeight()
						+ $("#top-locPanel").outerHeight() + 2;
			} else {
				map.setCenter(results[0].geometry.location);
				map.setZoom(15);
			}
		}
	});
}

function locateEventAddress(addr) {
	geocoder = new google.maps.Geocoder();
	geocoder.geocode({
		'address' : addr
	}, function(results, status) {
		if (status == google.maps.GeocoderStatus.OK) {
			var msg = '';
			
			if (results.length > 1) {
				for (i = 0; i < results.length; i++) {
					msg += '&nbsp;&nbsp;&nbsp;' + (i + 1).toString()
							+ '.&nbsp;&nbsp;&nbsp; '
							+ '<a href="javascript:gotoLoc'
							+ results[i].geometry.location.toString() + '">'
							+ results[i].formatted_address + '</a><br/>';
					eventAddress = null;
				}
				
			} else {
				eventAddress = results[0].geometry.location;
				
			}
		}
	});
}

function locateCurAddress() {
	geocoder = new google.maps.Geocoder();
	geocoder.geocode({
		'address' : document.getElementById("curAddress").value
	}, function(results, status) {
		if (status == google.maps.GeocoderStatus.OK) {
			var msg = '';
			if (results.length > 1) {
				for (i = 0; i < results.length; i++) {
					msg += '&nbsp;&nbsp;&nbsp;' + (i + 1).toString()
							+ '.&nbsp;&nbsp;&nbsp; '
							+ '<a href="javascript:gotoLoc'
							+ results[i].geometry.location.toString() + '">'
							+ results[i].formatted_address + '</a><br/>';
					curUserLocaion = null;
				}
				
			} else {
				curUserLocaion = results[0].geometry.location;
				
			}
		}
	});
}

function hideMain() {
  document.getElementById("window-locPanel").style.visibility='hidden';
}

function gotoLoc(lat, lon) {
   hideMain();
   map.setCenter(new google.maps.LatLng(lat, lon));
   map.setZoom(15);
}
