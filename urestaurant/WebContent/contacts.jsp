<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!-- File name: dashboard.jsp
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
		<div class="col-md-3 col-lg-3">
			<nav class="bs-docs-sidebar affix-top">
				<div class="panel panel-default col-centered">
					<div class="panel-body contactPanel">
						<h4>Manage Contacts</h4>
						<!--   <ul class="nav bs-docs-sidenav">
							<li>Test</li>
						</ul> -->
						<div class="list-group manageContactsDiv">
							<a href="#" class="list-group-item mcLink">
								<p>All contacts</p>
							</a> 
							<a href="#" class="list-group-item mcLink">
								<p>Groups</p>
							</a> 
							<a href="#" class="list-group-item mcLink">
								<p>Frequently contacted</p>
							</a>
							<div class="addContactAndGroupDiv">
								<a href="#add-contact-modal" data-toggle="modal" role="button" id="addContactBtn" 
								class="btn btn-danger btn-circle btn-xl center-block" data-toggle="tooltip" title="<fmt:message key="addAContact" />" 
								data-placement="top" data-original-title="<fmt:message key="addAContact" />">
									<i class="fa fa-user-plus"></i>
								</a>
								<a href="#add-group-modal" data-toggle="modal" role="button" id="addGroupBtn" 
								class="btn btn-success btn-circle btn-xl center-block" data-toggle="tooltip" title="<fmt:message key="addAGroup" />" 
								data-placement="top" data-original-title="<fmt:message key="addAGroup" />">
									<i class="fa fa-users"></i>
									<i class="fa fa-plus"></i>
								</a>
							</div>
						</div>
					</div>
				</div>
			</nav>
		</div>
			<div class="col-md-9 col-lg-9">
				<div class="panel panel-default col-centered contactPanel">
					<div class="panel-body contactPanel">
					<h4>All Contacts</h4>
						<div id="ctcs-list-gr" class="list-group">
						
						</div>
					</div>
				</div>
			</div>
			<!--
		<div id="contactsBtn">
			<span class="g-signin"
				data-scpoe="openid email"
				data-clientid="557860846155-t3v6kvb0muavu05pa8mvikhqfdfpat41.apps.googleusercontent.com"
				data-redirecturi="contacts.jsp"
				data-accesstype="offline"
				data-cookiepolicy="single_host_origin"
				data-callback="getContactsCallback"
				data-approvalprompt="force">
			</span>
		</div>
		<div id="result">
		
		</div>
		-->
		</div>
	<!-- End Main Content -->
	
	<!-- Add Contact Modal -->
	<div id="add-contact-modal" class="modal fade" tabindex="-1">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close glyphicon glyphicon-remove" data-dismiss="modal"></button>
					<h3 id="addContactHeader" style="margin-left: 15px;">
						<fmt:message key="addAContact" />
					</h3>
				</div>
				<div class="modal-body ctc-frm-div">

				</div>
			</div>
			<!-- End Add Contact form -->
		</div>
	</div>
	<!-- End Add Contact Modal -->
	
	<!-- Add Group Modal -->
	<div id="add-group-modal" class="modal fade" tabindex="-1">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close glyphicon glyphicon-remove" data-dismiss="modal"></button>
					<h3 id="addGroupHeader" style="margin-left: 15px;">
						<fmt:message key="addAGroup" />
					</h3>
					<!-- <h3 id="lgn-err" class="hide" style="margin-left: 15px;">
						<fmt:message key="loginMessage" />
					</h3> -->
				</div>
				<div class="modal-body">
					<!-- Login form -->
					<form id="addGroupForm" action="addgroup" method="post">
						<div id="groupName" class="row sgn-cntrl">
							<div class="row">
								<div class="col-md-4">
									<label class="label label-default sgn-labl">
										<fmt:message key="name" />
									</label>
								</div>
							</div>
							<div class="row">
								<div class="col-md-12">
									<input name="name" class="form-control" type="text" data-placement="top" data-toggle="popover" data-trigger="focus" />
								</div>
							</div>
						</div>
						<div class="row sgn-cntrl"
							style="background-color: #303030; margin-top: 15px; float: right;">
						</div>
						<div class="modal-footer sgn-cntrl">
							<button id="add-btn" class="btn btn-info" type="submit"
								style="margin-right: 10px;">
								<fmt:message key="add" /><!-- Label -->
							</button>
							<button id="addGroup-cancel" class="btn btn-danger" type="reset" data-dismiss="modal">
								<fmt:message key="cancel" /><!-- Label -->
							</button>
						</div>
					</form>
				</div>
			</div>
			<!-- End Add Group form -->
		</div>
	</div>
	<!-- End Add Group Modal -->
	
	<!-- Start Scripts -->
	<!-- <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script> -->
	<script src="js/jquery-1.12.0.js"></script>
	<script src="js/bootstrap.min.js"></script>
	<script src="//apis.google.com/js/platform.js?onload=start"></script>
	<script>
		$("#addContactBtn").tooltip();
		$("#addGroupBtn").tooltip();
		$(function(){
	    	//$("#add-contact-modal").modal({show:false, backdrop:false});
	    	
	    	getAllContacts();
	    	
	    	$("#addGroupForm").on("submit", function(event) {
	    		event.preventDefault();
	    		console.log(event);
	    	});
	    	
	    	$("#addContactBtn").on("click", function(event, id) {
	    		getContactForm(id);
	    	});
	    	
	    	$("#ctcs-list-gr").on("click", ".list-group-item", function(event) {
	    		var id = $(this).attr("data-id");
	    		$("#addContactBtn").trigger("click", id);	    		
	    	});
		});
		
		/*******************************************************************************
		* All contacts ajax request
		*******************************************************************************/
		var getAllContacts = function() {
			$.ajax({
				method: "get",
				dataType: "html",
				url: "allcontacts",
			}).done(function(data) {
				//console.log(data);
				if (data) $("#ctcs-list-gr").html(data);
			}).error(function(data) {
				console.log(data);
				alert("18Oops! Sorry we cannot process the request at this time.");
			});
		}; // end function
		
		/*******************************************************************************
		* Contact form ajax request
		*******************************************************************************/
		var getContactForm = function(id) {
			console.log(id);
			$.ajax({
				method: "get",
				dataType: "html",
				url: "ContactForm.jsp" + (id ? "?id=" + id : ""),
			}).done(function(data) {
				//console.log(data);
				if (data) $(".ctc-frm-div").html(data);
			}).error(function(data) {
				console.log(data);
				alert("19Oops! Sorry we cannot process the request at this time.");
			});
		}; // end function
	</script>
	<!-- End Scripts -->
</body>
</fmt:bundle>
</html>