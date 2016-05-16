<!DOCTYPE html>
<%@page import="connections.DatabaseConnectionFactory"%>
<%@page import="utilities.Contact"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!-- File name: dashboard.jsp
JSP Author: Christopher Elliott
Date: 01/02/2015 (DD/MM/YYYY)
Public home page to welcome new/existing users. -->
<html>
<%if(session.getAttribute("isAuthenticated") == null || session.getAttribute("isAuthenticated") == "") {%>
	<c:redirect url="index.jsp" />
<%}%>
<c:choose>
	<c:when test="${!(empty param.locale)}">
		<fmt:setLocale value="${param.locale}" />
		<c:set var="locale" scope="session" value="${param.locale}" />
	</c:when>
	<c:when test="${!(empty locale)}">
		<fmt:setLocale value="${locale}" />
	</c:when>
</c:choose>
<fmt:bundle basename="labels">
<head>
    <style>
        #outerDiv {
            border: 2px solid #a1a1a1;
            padding: 10px 40px;
            background: #dddddd;
            width: 300px;
            border-radius: 25px;
        }
    </style>

<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="The best event hosting app in Web Programming 2016">
<meta name="author" content="Web Project 2016">
<title>Urestaurant</title>
<!-- Start Bootstrap CSS -->
<link href="css/bootstrap.min.css" rel="stylesheet">
<link href="css/font-awesome.min.css" rel="stylesheet">
<link href="css/themes/darkly.css" rel="stylesheet">
<!-- End Bootstrap CSS -->

<!-- Start custom CSS -->
<link href="css/custom.css" rel="stylesheet">
<!-- End Custom CSS -->

<!-- HTML5 shiv and Respond.js for IE8 support of HTML5 elements and media queries -->
<!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->

<!-- Start Inline CSS (overrides all of the above styles) -->
<!-- End Inline CSS -->
</head>
<body onload="initMap()">

	<!-- Start Navigation Bar -->
	<%@include file="WEB-INF/partials/NavigationBar.jsp"%>
	<!-- End Navigation Bar -->
		
		<!--<img src="images/369.gif" style="margin-top: 200px;" />-->
		<!-- Start Main Content -->
		<div class="container-fluid main-content">
			<div id="srch-evts-cntner" >
				<div class="row section-title">
					<div class="col-md-12 text-center">
						<h4 class="background">
							<span><fmt:message key="searchEvents" />&nbsp<span class="caret"></span></span>
						</h4>
					</div>
				</div>
				<div class="row" id="searchEventsMap">
					<div id="mapPanelTop">
						<table style="width: 100%">
							<tr>
								<td id="location">
									<div id="mapLabelTop"><fmt:message key="findEventsNearYou" />&nbsp;&nbsp;&nbsp;&nbsp;<input id="address" type="text" size="40" 
       onkeydown="handleSearch(event)" value="" ></div> 
       
      				
      							</td>
      							
      							<td id="location">
      							 <div id="mapLabelTop2"><fmt:message key="setYourLocation" />&nbsp;&nbsp;&nbsp;&nbsp;<input id="curAddress" type="text" size="40" 
       				onkeydown="handleCurLocation(event)" value="e.g. Summerhays" ></div> 
      							</td>

							</tr>
						</table>
					</div>
					<div id="map_canvas"></div>
					<div class="mapBottomPanel"><fmt:message key="zoomKeys" /></div>
						<div id='window-locPanel'>
						   <div id='top-locPanel'><fmt:message key="multipleLocations" />&nbsp;&nbsp;&nbsp;&nbsp;<a style="color: white; text-align: right; margin-left: 25px;" href="javascript:hideMain()"><b>X</b></a></div>
						   <div id='main-locPanel'  onkeydown='handleSearch(event)'></div>
						</div>
				</div>
			</div>
			<div id="hstng-evts-cntner">
				<div class="row section-title">
					<div class="col-md-12 text-center">
						<h4 class="background">
							<span><fmt:message key="hostingEvents" />&nbsp<span class="caret"></span></span>
						</h4>
					</div>
				</div>
				<span id="hosting-loader" class="loader hide">
					<img src="images/loader.gif" style="margin: auto" />
				</span>
				<div class="row" id="hostingEventList"></div>
			</div>
			<div id="attnd-evts-cntner">
				<div class="row section-title">
					<div class="col-md-12 text-center">
						<h4 class="background">
							<span><fmt:message key="attendingEvents" />&nbsp<span class="caret"></span></span>
						</h4>
					</div>
				</div>
				<span id="attending-loader" class="loader hide">
					<img src="images/loader.gif" style="margin: auto" />
				</span>
				<div class="row" id="attendingEventList"></div>
			</div>
			<div id="pblc-evts-cntner">
				<div class="row section-title">
					<div class="col-md-12 text-center">
						<h4 class="background">
							<span><fmt:message key="publicEvents" />&nbsp<span class="caret"></span></span>
						</h4>
					</div>
				</div>
				<span id="public-loader" class="loader hide">
					<img src="images/loader.gif" style="margin: auto" />
				</span>
				<div class="row" id="publicEventList"></div>
			</div>
		</div>
		
		<!-- Invite Modal -->
		<div id="invite-modal" class="modal fade" tabindex="-1">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal">&times;</button>
       					<h4 class="modal-title"><fmt:message key="contacts" /></h4>
					</div>
					<div class="modal-body">
						<span id="invites-loader" class="loader hide">
							<img src="images/loader.gif" style="margin: auto" />
						</span>
						<div id="inviteModalBody"></div>
					</div>
					<div class="modal-footer sgn-cntrl">
						<span id="submit-invites-loader" class="loader hide">
							<img src="images/loader.gif" style="height: 50px; margin-right: 15px;" />
						</span>
						<button id="invite-button" type="button" class="btn btn-primary"><fmt:message key="invite" /></button>
						<button type="button" class="btn btn-danger" data-dismiss="modal"><fmt:message key="cancel" /></button>					
					</div>
				</div>
				<!-- End Add Group form -->
			</div>
		</div>
		<!-- End Invite Modal -->
	
		
		
		<!-- End Main Content -->

		<!-- Start Scripts -->
		<!-- <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script> -->
		<script src="js/jquery-1.12.0.js"></script>
		<script src="js/bootstrap.min.js"></script>
		<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>
		<script>
		
			jQuery.fn.visible = function() {
			    return this.css('visibility', 'visible');
			};
	
			jQuery.fn.invisible = function() {
			    return this.css('visibility', 'hidden');
			};
	
			jQuery.fn.visibilityToggle = function() {
			    return this.css('visibility', function(i, visibility) {
			        return (visibility == 'visible') ? 'hidden' : 'visible';
			    });
			};
		
			$(function() {
				$( "#window-locPanel" ).draggable({containment: "parent", zIndex:999});
				//document.getElementById("searchEventsMap").style.visibility = "hidden";
				$('#searchEventsMap').hide();
 
				getHostedEvents();
				getAttendingEvents();
				getPublicEvents();
				
				$("#inviteModalBody").on("click", ".list-group-item", function(event) {
					$(this).addClass("selected");
				});
				
				$("#inviteModalBody").on("click", ".list-group-item.selected", function(event) {
					$(this).removeClass("selected");
				});
				
				var event_id = null;
				
				$("#invite-button").on("click",  function(event) {
					$("#submit-invites-loader").removeClass("hide");
					var x = $("#inviteModalBody").children(".list-group-item.selected");
					var selected_contacts = [];
					$("#inviteModalBody").children(".list-group-item.selected").each(function() {
						selected_contacts.push($(this).attr("data-id"));
					});
					console.log(selected_contacts);
					if (selected_contacts.length > 0) {
						$.ajax({
							method: "post",
							dataType: "json",
							url: "/urestaurant/sendinvites",
							data: { event : event_id , contacts : selected_contacts },
						}).done(function(data) {
							console.log(data);
							$("#submit-invites-loader").addClass("hide");
							alert("Invites sent!");
							$("#invite-modal").modal("hide");
						}).error(function(data) {
							console.log(data);
							$("#submit-invites-loader").addClass("hide");
							alert("1Oops! Sorry we cannot process the request at this time.");
						});
					} else {
						alert("Woops, No cantacts have been selected.");
					}
				});
				
				$("#hostingEventList").on("click", ".invite", function(event) {
					event.preventDefault();
					$("#invites-loader").removeClass("hide");
					event_id = $(this).closest("div.thumbnail").attr("data-evt");
					console.log($(this).closest("div.thumbnail").attr("data-evt"));
					$.ajax({
						method: "get",
						dataType: "html",
						url: "/urestaurant/allcontacts"
					}).done(function(data) {
						console.log(data);
						$("#invites-loader").addClass("hide");
						//$("#invite-modal > .modal-body").html(data);
						$("#inviteModalBody").html(data);
					}).error(function(data) {
						console.log(data);
						$("#invites-loader").addClass("hide");
						alert("2Oops! Sorry we cannot process the request at this time.");
					});
				});
				
				/* RSVP dropdown */
				$("#publicEventList").on("click", ".status", function(event) {
					event.preventDefault();
					console.log($(this).closest("div.thumbnail").attr("data-evt"));
					$.ajax({
						method: "post",
						dataType: "json",
						url: "/urestaurant/rsvp",
						data: { event : $(this).closest("div.thumbnail").attr("data-evt"), event_status : $(this).attr("data-sts") },
					}).done(function(data) {
						console.log(data);
						getHostedEvents();
						getAttendingEvents();
						getPublicEvents();
						getPublicMapEvents();
					}).error(function(data) {
						console.log(data);
						alert("3Oops! Sorry we cannot process the request at this time.");
					});
				});
				
				$("#attendingEventList").on("click", ".status", function(event) {
					event.preventDefault();
					console.log($(this).closest("div.thumbnail").attr("data-evt"));
					$.ajax({
						method: "post",
						dataType: "json",
						url: "/urestaurant/rsvp",
						data: { event : $(this).closest("div.thumbnail").attr("data-evt"), event_status : $(this).attr("data-sts") },
					}).done(function(data) {
						getHostedEvents();
						getAttendingEvents();
						getPublicEvents();
						getPublicMapEvents();
					}).error(function(data) {
						console.log(data);
						alert("4Oops! Sorry we cannot process the request at this time.");
					});
				});
				
				$(".section-title").on("click", function(event) {
					$(this).siblings("div").toggle(500);
					google.maps.event.trigger(map, "resize");
				});
			});
			/*******************************************************************************
			* Organized events ajax request
			*******************************************************************************/
			var getHostedEvents = function() {
				$("#hosting-loader").removeClass("hide");
				$.ajax({
					method: "get",
					dataType: "html",
					url: "/urestaurant/gethostedevents",
				}).done(function(data) {
					//console.log(data);
					$("#hosting-loader").addClass("hide");
			        $("#hostingEventList").html(data);
			        if (data) {
			        	$("#hstng-evts-cntner").removeClass("hide");
			        	$("#hostingEventList").html(data);
			        } else $("#hstng-evts-cntner").addClass("hide");
				}).error(function(data) {
					console.log(data);
					$("#hosting-loader").addClass("hide");
					alert("5Oops! Sorry we cannot process the request at this time.");
				});
			}; // end function
			/*******************************************************************************
			* Attending events ajax request
			*******************************************************************************/
			var getAttendingEvents = function() {
				$(".status").html("RSVP <span class=\"caret\"></span>");
				$("#attending-loader").removeClass("hide");
				$.ajax({
					method: "get",
					dataType: "html",
					url: "/urestaurant/getattendingevents"
				}).done(function(data) {
					//console.log(data);
					$("#attending-loader").addClass("hide");
			        if (data) {
			        	$("#attnd-evts-cntner").removeClass("hide");
			        	$("#attendingEventList").html(data);
			        } else $("#attnd-evts-cntner").addClass("hide");
			        $(".sts-1").removeClass("btn-primary");
			        $(".sts-1").addClass("btn-success");
			        $(".sts-1").html("Going <span class=\"caret\"></span>");
			        $(".sts-2").removeClass("btn-primary");
			        $(".sts-2").addClass("btn-info");
			        $(".sts-2").html("Interested <span class=\"caret\"></span>");
			        $(".sts-3").removeClass("btn-primary");
			        $(".sts-3").addClass("btn-danger");
			        $(".sts-3").html("Not Going <span class=\"caret\"></span>");
			        $(".sts-4").removeClass("btn-primary");
			        $(".sts-4").addClass("btn-warning");
			        $(".sts-4").html("Invited <span class=\"caret\"></span>"); 
				}).error(function(data) {
					console.log(data);
					$("#attending-loader").addClass("hide");
					alert("6Oops! Sorry we cannot process the request at this time.");
				});
			}; // end function
			/*******************************************************************************
			* Public events ajax request
			*******************************************************************************/
			var getPublicEvents = function() {
				$("#public-loader").removeClass("hide");
				//$("#publicEventList").html("<img src=\"/images/369.gif\" />");
				$.ajax({
					method: "get",
					dataType: "html",
					url: "/urestaurant/getevents"
				}).done(function(data) {
					//console.log(data);
					$("#public-loader").addClass("hide");
					if (data) {
			        	$("#pblc-evts-cntner").removeClass("hide");
			        	$("#publicEventList").html(data);
			        } else $("#pblc-evts-cntner").addClass("hide");
				}).error(function(data) {
					console.log(data);
					$("#public-loader").addClass("hide");
					alert("7Oops! Sorry we cannot process the request at this time.");
				});
			}; // end function
	
		</script>
		<script>
			function initAutocomplete(){
				var neBounds = new google.maps.LatLng(47.072109, -78.222656)
				var swBounds = new google.maps.LatLng(44.623831, -80.683594)
				var lanLonBounds = new google.maps.LatLngBounds(); 
				lanLonBounds.extend(neBounds);
				lanLonBounds.extend(swBounds);
				
				var input = document.getElementById('address', {bounds: lanLonBounds});
				var input2 = document.getElementById('curAddress', {bounds: lanLonBounds});
				var autocomplete = new google.maps.places.Autocomplete(input, {bounds: lanLonBounds});
			    autocomplete.addListener('place_changed', function() {
			      var place = autocomplete.getPlace();
			       // if (!place.geometry) {
			        //console.log("autocomplete's returned place contains no geometry");
			      //  return;
			     // }
			    });
			    var autocomplete2 = new google.maps.places.Autocomplete(input2, {bounds: lanLonBounds});
			    autocomplete2.addListener('place_changed', function() {
			      var place = autocomplete2.getPlace();
			       // if (!place.geometry) {
			        //console.log("autocomplete's returned place contains no geometry");
			      //  return;
			     // }
			    });
			}
		</script>
		<script type="text/javascript" src="http://maps.google.com/maps/api/js?libraries=geometry&language=en&libraries=places&callback=initAutocomplete"></script>
		<script type="text/javascript" src="js/maps.js"></script>
		
	<!-- End Scripts -->
</body>
</fmt:bundle>
</html>
