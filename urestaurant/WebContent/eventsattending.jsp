<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!-- File name: attendingevents.html
JSP Author: Christopher Elliott
Date: 01/02/2015 (DD/MM/YYYY)
Public home page to welcome new/existing users. -->
<html>
<%if(request.getSession(false) == null || request.getSession(false).getAttribute("isAuthenticated") == null) {%>
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
		<div class="container-fluid main-content">
			<div class="row" id="eventsAttendingList">
			</div>
		</div>
		<!-- End Main Content -->

		<!-- Start Scripts -->
		<!-- <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script> -->
		<script src="js/jquery-1.12.0.js"></script>
		<script src="js/bootstrap.min.js"></script>
		<script>
			$(function() {
				/* $.ajax({
					method : "get",
					dataType : "html",
					url : "/urestaurant/partials/BrowseEvents.jsp",
				}).done(function(data) {
					$(".container-fluid").html(data);
				}); */
				
				/*$("#browse-events").on("click", function(event) {
					event.preventDefault();
					$.ajax({
						method : "get",
						dataType : "html",
						url : "/urestaurant/partials/BrowseEvents.jsp",
					}).done(function(data) {
						$(".container-fluid").html(data);
					});
				});*/

				$("#friends-link").on("click", function(event) {
					event.preventDefault();
					$.ajax({
						method : "get",
						dataType : "html",
						url : "partials/Friends.jsp",
					}).done(function(data) {
						$(".container-fluid").html(data);
					});
				});
				
			});
			
			/*******************************************************************************
			* Public events ajax request
			*******************************************************************************/
			$(function() {
				$.ajax({
					method: "get",
					dataType: "json",
					url: "/urestaurant/get_events_attending",
				}).done(function(data) {
						var statusHTML = '<div class="col-md-12 text-center"><h2><fmt:message key="eventsAttending" /></h2></div>';
						//Placeholders
						var randInt = Math.floor(Math.random()*10)+1;
						var placeholderImage = '<a href="images/ev'+randInt+'.jpg" title="Event '+randInt+'"><img src="images/ev'+randInt+'.jpg" width="534" height="800"></a>';
				        // var placeholderDescription = ;
				        //for each object in the array
				        $.each(data["events"], function(index, event){ //pass index and value (event object)
				        	statusHTML += '<div class="col-md-3"><div class="thumbnail">';
				        	statusHTML += placeholderImage;
				        	statusHTML += '<div class="caption">';
				        	statusHTML += '<h4>' + event.name + '</h4>';
				        	statusHTML += '<h4>' + event.startDate + '</h4>';
				        	statusHTML += '<div class="btn-toolbar">';
				        	statusHTML += '<div class="btn-group btn-group-sm">';
				        	statusHTML += '<a href="#" class="btn btn-primary" role="button"><i class="fa fa-eye"></i> See this event</a>';  
				        	statusHTML += '<a href="#" class="btn btn-success" role="button"><i class="fa fa-share"></i> Join this event </a>';
				        	statusHTML += '</div></div></div></div></div>'; 
				        });
				        $("#eventsAttendingList").html(statusHTML);
					}).error(function(data) {
						alert("12Oops! Sorry we cannot process the request at this time.");
					});
			}); //end function
		</script>
	<!-- End Scripts -->
</body>
</fmt:bundle>
</html>
