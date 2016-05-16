<!DOCTYPE html>
<%@page import="utilities.Contact"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

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

<body>
	<!-- Start Navigation Bar -->
	<%@include file="WEB-INF/partials/NavigationBar.jsp"%>
	<!-- End Navigation Bar -->

	<!-- Start Main Content -->
	
	
    	<div class="row">
        	
        	<div class="col-xs-4">
	            <div class="row" id="searchEventsMap2">
					<div id="mapPanelTop2">
						<table style="width: 100%">
							<tr>
								<td id="location">
									<div id="mapLabelTop2"><fmt:message key="setYourLocation" />&nbsp;&nbsp;&nbsp;&nbsp;<input id="curAddress" type="text" size="20" 
       onkeydown="handleCurLocation(event)" value="" ></div> 
       
      							</td>

							</tr>
						</table>
					</div>
					<div id="map_canvas2"></div>
					<div class="mapBottomPanel"><fmt:message key="zoomKeys" /></div>
						<div id='window-locPanel'>
						   <div id='top-locPanel'><fmt:message key="multipleLocations" />&nbsp;&nbsp;&nbsp;&nbsp;<a style="color: white; text-align: right; margin-left: 25px;" href="javascript:hideMain()"><b>X</b></a></div>
						   <div id='main-locPanel'  onkeydown='handleSearch(event)'></div>
						</div>
				</div>
        	</div>
        	
        	<div class="my-container text-center col-xs-8">
            	
		
		<h4> <b> From </b>   <%=(request.getAttribute("startDate"))%> 
		 	<%=(request.getAttribute("startTime"))%>&nbsp <b> 	
		 	To </b>    <%=(request.getAttribute("endDate"))%> <%=(request.getAttribute("endTime"))%> </h4>
		 	
		<h5> Location: <%=(request.getAttribute("location"))%> </h5>
		<h1><%=(request.getAttribute("name"))%> </h1>
		<img src="getimage?img=<%=(request.getAttribute("docId"))%>" />
		
	
        	</div>
	    </div>
	
	
	
	
	
	
	
	
	<div class="text-center col-md-4 col-md-offset-4" > 
	
		 
		 
		 
		 
		   
	 
	 
	 <h4>
	 	<%=(request.getAttribute("description"))%>
	 
	 </h4>
	 
	 </div>
	 
	 
	 
	<div class="container main-content">
	
		 
		
			
	
	
	</div>
	<!-- End Main Content -->
	<!-- Start Scripts -->
	<script src="js/jquery-1.12.0.js"></script>
	<script src="js/bootstrap.min.js"></script>
	<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>
	<script type="text/javascript" src="http://maps.google.com/maps/api/js?libraries=geometry&language=en&libraries=places&callback=initAutocomplete"></script>
	<script type="text/javascript" src="js/maps.js"></script>
	
	<script>
	
		$(function() {
			initMap("<%=(request.getAttribute("location"))%>");
		});
		
		function initAutocomplete(){
			var neBounds = new google.maps.LatLng(47.072109, -78.222656)
			var swBounds = new google.maps.LatLng(44.623831, -80.683594)
			var lanLonBounds = new google.maps.LatLngBounds(); 
			lanLonBounds.extend(neBounds);
			lanLonBounds.extend(swBounds);
			
			var input = document.getElementById('curAddress', {bounds: lanLonBounds});
			var autocomplete = new google.maps.places.Autocomplete(input, {bounds: lanLonBounds});
		    autocomplete.addListener('place_changed', function() {
		      var place = autocomplete.getPlace();
		       // if (!place.geometry) {
		        //console.log("autocomplete's returned place contains no geometry");
		      //  return;
		     // }
		    });
		}
	</script>

	<!-- End Scripts -->

</body>
</fmt:bundle>
</html>