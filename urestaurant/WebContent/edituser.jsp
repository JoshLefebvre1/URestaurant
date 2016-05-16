<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!-- File name: dashboard.html
JSP Author: Josh Lefebvre
Date: 01/02/2015 (DD/MM/YYYY)
Allows user to edit their profile -->

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
	<link href="css/bootstrap-datetimepicker.min.css" rel="stylesheet">
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
	<div class="container main-content">
		
		<h1><fmt:message key="editProfile" /></h1>
		
		<form id="event-frm" action="EditProfileServlet" method="post" enctype="multipart/form-data">
			
			<div class="form-group">
				<label><fmt:message key="firstName" /></label> 
				<input id="firstname" name="firstname" type="text" class="form-control"  value="<%=(request.getAttribute("firstname"))%>" data-toggle="popover" data-placement="top" data-trigger="focus" />
			</div>

			<div class="form-group">
				<label><fmt:message key="lastName" /></label> 
				<input id="lastname" name="lastname" type="text" class="form-control" value="<%=(request.getAttribute("lastname"))%>" data-toggle="popover" data-placement="top" data-trigger="focus" />
			</div>
			
			<div class="form-group">
				<label><fmt:message key="primaryEmail" /></label> 
				<input id="email" name="email" type="text" class="form-control" value="<%=(request.getAttribute("email"))%>" data-toggle="popover" data-placement="top" data-trigger="focus" />
			</div>
			
			<div class="form-group">
				<label><fmt:message key="phoneNumber"/></label> 
				<input id="phone" type="tel" name="phone" type="text" class="form-control ctc-phone" value="<%=(request.getAttribute("phonenumber"))%>" data-toggle="popover" data-placement="top" data-trigger="focus" />
			</div>
			
			<div class="form-group">
				<label><fmt:message key="profileImage" /></label>
				<input id="evt-graphic" name="graphic" type="file" accept="image/gif,image/jpeg,image/png" />
			</div>
			

			<div class="modal-footer sgn-cntrl">
				<button class="btn btn-info" type="submit" style="margin-right: 10px;">
					<fmt:message key="edit" /><!-- Label -->
				</button>
				<a id="sgn-cancel" class="btn btn-danger" href="dashboard.jsp">
					<fmt:message key="cancel" /><!-- Label -->
				</a>
			</div>
		</form>
	</div>
	<!-- End Main Content -->

	<!-- Start Scripts -->
	<!-- <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script> -->
	<script src="js/jquery-1.12.0.js"></script>
	<script src="js/bootstrap.min.js"></script>
	<script src="js/moment.min.js"></script>
	<script src="js/tinymce.min.js"></script>
	<script src="js/jquery.maskedinput.min.js"></script>
	
	<script>
		$(function() {
		
			$(".ctc-phone").mask("(999) 999-9999", { placeholder :" " });
		    
		});
	    
	    	
		$("input").on("change", function(event) {
			$(this).removeClass("required");
		});
		
		$("#event-frm").submit(function(event) {
			event.preventDefault();
			
			/*if (file_input.hasClass('is-uploading')) return false;
			file_input.addClass('is-uploading').removeClass('is-error');*/
			
			var formData = new FormData($(this)[0]);
	        formData.append('file', $('input[type=file]').file);

			
			$.ajax({
				type: "post",
				url: "EditProfileServlet",
				data: formData,
				cache: false,
			    contentType: false,
			    processData: false,			
			}).done(function(data) 
			{
				console.log(data);
				var isvalid = data["isvalid"];
				if (isvalid) 
					window.location.href = "dashboard.jsp";
				// firstname validation
				if (!data["contact"]["firstname"].isvalid) 
				{
					$("input[name=\"firstname\"]").addClass("required");
					var datacontent = "<p>" + data["contact"]["firstname"]["message"] + "</p>"; 
					$("input[name=\"firstname\"]").attr("data-content", datacontent);
				}
				// lastname validation
				if (data["contact"]["lastname"] && !data["contact"]["lastname"].isvalid) 
				{
					$("input[name=\"lastname\"]").addClass("required");
					var datacontent = "<p>" + data["user"]["lastname"]["message"] + "</p>"; 
					$("input[name=\"lastname\"]").attr("data-content", datacontent);
				}
				// email validation
				if (!data["contact"]["email"].isvalid) 
				{
					$("input[name=\"email\"]").addClass("required");
					var datacontent = "<p>" + data["contact"]["email"]["message"] + "</p>"; 
					$("input[name=\"email\"]").attr("data-content", datacontent);
				}
				// phone validation
				if (data["contact"]["phone"] && !data["contact"]["phone"].isvalid) 
				{
					$("input[name=\"phone\"]").addClass("required");
					var datacontent = "<p>" + data["user"][key]["message"] + "</p>"; 
					$("input[name=\"phone\"]").attr("data-content", datacontent);
				}

			}).fail(function(data) 
				{
					console.log(data);
					alert("15Oops! Sorry we cannot process the request at this time.");
				});
		});
	</script>
	<!-- End Scripts -->
</body>
</fmt:bundle>
</html>