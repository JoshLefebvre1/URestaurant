<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<!-- File name: create-event.jsp
JSP Author: Josh Lefebvre
Date: 01/02/2015 (DD/MM/YYYY)
Page to create events. -->

<html>
<%if(request.getSession(false) == null || request.getSession(false).getAttribute("isAuthenticated") == null) {%>
	<c:redirect url="index.jsp" />
<%}%>
<c:choose>
	<c:when test="${empty param.locale} && ${empty locale}">
		<fmt:setLocale value="en_CA" />
		<c:set var="locale" scope="session" value="en_CA" />
	</c:when>
	<c:when test="${!(empty param.locale)}">
		<fmt:setLocale value="${param.locale}" />
		<c:set var="locale" scope="session" value="${param.locale}" />
	</c:when>
	<c:when test="${!(empty locale)}">
		<fmt:setLocale value="${locale}" />
	</c:when>
</c:choose>

<fmt:bundle basename="labels"/>

<head>

	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title>Urestaurant</title>
	<meta name="description"
		content="The best event hosting app in Web Programming 2016">
	<meta name="author" content="Web Project 2016">
	
	<!-- Start Bootstrap CSS -->
	<link href="css/bootstrap.min.css" rel="stylesheet">
	<link href="css/font-awesome.min.css" rel="stylesheet">
	<link href="css/themes/darkly.css" rel="stylesheet">
	<!-- End Bootstrap CSS -->
	
	<!-- Start custom CSS -->
	<link href="css/custom.css" rel="stylesheet">
	<link href="css/sidebar.css" rel="stylesheet">
	<!-- End Custom CSS -->
	
	<!-- HTML5 shiv and Respond.js for IE8 support of HTML5 elements and media queries -->
	<!--[if lt IE 9]>
	      <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
	      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
	    <![endif]-->
	
	<!-- Start Inline CSS (overrides all of the above styles) -->
	<style type="text/css">
	.jumbotron, .page {
		padding-top: 140px;
		padding-bottom: 140px;
		margin-bottom: 0px;
	}
	</style>
	<!-- End Inline CSS -->

</head>


<body style="padding-top: 50px;">
	
	<div id="wrapper">
		
		
		<!-- Start Sidebar -->
		<nav class="sidebar-wrapper">
			<ul class="sidebar-nav">
				<li class="sidebar-brand"><a href="#" class="visible-sm visible-md visible-lg">Events</a></li>
				<li><a href="#"><span class="visible-sm visible-md visible-lg">Browse Events</span><i class="glyphicon glyphicon-home visible-xs"></i></a></li>
				<li><a href="#"><span class="visible-sm visible-md visible-lg">Host an Event</span><i class="glyphicon glyphicon-globe visible-xs"></i></a></li>
				<li><a href="#"><span class="visible-sm visible-md visible-lg">Settings</span><i class="fa fa-cog visible-xs"></i></a></li>
				<li><a href="#"><span class="visible-sm visible-md visible-lg">Logout</span><i class="fa fa-sign-out visible-xs"></i></a></li>
			</ul>
		</nav>
		<!-- End Sidebar -->

		<!-- Page Content -->
        <div id="page-content-wrapper">
            <div class="container">
                <div class="row">
	                <h1> Host a URestaurant Event</h1>
	                <form>
	                	
						  <div class="form-group">
						    <label for="InputEvent">Event Name</label>
						    <input type="text" class="form-control" id="eventName" placeholder="Enter an Event Name">
						  </div>
						  
						  <div class="form-group">
						    <label for="InputLocation">Location</label>
						    <input type="text" class="form-control" id="address" placeholder="Enter an address">
						  </div>
						  
						  <div class="form-group">
						    <label for="InputDate">Event Date</label>
						    <input type="date" class="form-control" id="date" placeholder="Enter an event date DD/MM/YYYY">
						  </div>
						  
						 <div class="form-group">
						    <label for="InputTime">Event Time</label>
						    <input type="time" class="form-control" id="time" placeholder="Enter an event time hh:mm:AM/PM">
						  </div>
						  
						   <div class="form-group">
						    <label for="InputLocation">Description</label>
						    <textarea class="form-control" rows="3" placeholder="Enter an event description..."></textarea>
						  </div>

						  <div class="form-group">
						    <label for="InputPicture">Event Picture</label>
						    <input type="file" id="exampleInputFile">
						  </div>
						  
						  <label for="InputStatus">Event Status</label>
						  
						  <div class="radio">
							  <label>
							    <input type="radio" name="optionsRadios" id="optionsRadios1" value="option1" checked>
							    Event is Public
							  </label>
						   </div>
						   <div class="radio">
							  <label>
							    <input type="radio" name="optionsRadios" id="optionsRadios2" value="option2">
							    Event is Private
							  </label>
							</div>
							<br/>
							<button class="btn btn-default" type="submit">Create Event</button>
					</form>

                </div>
            </div>
        	<!-- Start Bottom Nav -->
			<div id="bottomnav" class="footer"
				style="padding-top: 40px; padding-bottom: 40px;">
				<div class="container">
					<div class="row">
						<div class="col-md-12 text-center">
							<fmt:message key="copyright" />
							&nbsp;|&nbsp;
							<a href="#">
								<fmt:message key="terms" />
							</a>
							&nbsp;|&nbsp;
							<a href="#">
								<fmt:message key="privacy" />
							</a>
						</div>
					</div>
				</div>
			</div>
			<!-- End Bottom Nav -->
        </div>
        <!--END CONTENT -->
        

	
	</div>





	<!-- Start Scripts -->
	<!-- <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script> -->
	<script src="js/jquery-1.12.0.js"></script>
	<script src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.7.2/jquery-ui.min.js"></script>
	<script src="js/bootstrap.min.js"></script>
	<script src="js/sidebar_menu.js"></script>
	<!-- End Scripts -->
</body>
</html>