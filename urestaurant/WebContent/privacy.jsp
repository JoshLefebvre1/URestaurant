<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!-- Privacy Policy. -->
<html>
<%if(request.getSession(false) != null && request.getSession(false).getAttribute("isAuthenticated") != null) {%>
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
	<c:otherwise>
		<fmt:setLocale value="en_CA" />
		<c:set var="locale" scope="session" value="en_CA" />
	</c:otherwise>
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
	<nav id="topnavigation" class="navbar navbar-inverse navbar-fixed-top">
		<div class="container">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle collapsed"
					data-toggle="collapse" data-target="#topnav" aria-expanded="false">
					<span class="sr-only">Toggle navigation</span> <span
						class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<a class="navbar-brand" href="index.jsp">Urestaurant</a>
			</div>
			<div id="topnav" class="collapse navbar-collapse">
				<ul class="nav navbar-nav">
					<li class="dropdown">
						<a href="#" class="dropdown-toggle"
							data-toggle="dropdown" role="button" aria-haspopup="true"
							aria-expanded="false">
							<fmt:message key="language" /><!-- Label -->
							<span class="caret"></span>
						</a>
						<ul class="dropdown-menu">
							<c:url value="privacy.jsp" var="engURL">
								<c:param name="locale" value="en_CA" />						
							</c:url>
							<li><a href="${engURL}">English (Canada)</a></li>
							<c:url value="privacy.jsp" var="freURL">
								<c:param name="locale" value="fr_CA" />						
							</c:url>
							<li><a href="${freURL}">Français (Canada)</a></li>
						</ul>
					</li>
				</ul>
				<ul class="nav navbar-nav navbar-right">
					<li>
						<a href="#lgn-modal" data-toggle="modal" id="lgn-btn" role="button">
							<i class="fa fa-sign-in"></i>
							<fmt:message key="login" /><!-- Label -->
						</a>
					</li>
					<li>
						<a href="#sgn-modal" data-toggle="modal" id="sgn-btn" role="button">
							<i class="fa fa-user-plus"></i>
							<fmt:message key="signUp" /><!-- Label -->
						</a>
					</li>
				</ul>
			</div>
		</div>
	</nav>
	<!-- End Navigation Bar -->

		<!-- Start Main Content -->
		<div class="container-fluid main-content">
			<div class="container">
				<div class="row">
					<div class="col-12-md ">
						<h3 class="text-center"><fmt:message key="privacyPolicy" /></h3>
						<p>1. Urestaurant</p>
						<p><fmt:message key="privacyPolicy1" /></p>
						<br/>
						<p>2. <fmt:message key="users" /></p>
						<p><fmt:message key="privacyPolicy2" /></p>
						<br/>
						<p>3. <fmt:message key="personalDataWeCollect" /></p>
						<p><fmt:message key="privacyPolicy3" /></p>
						<br/>
						<p>4. <fmt:message key="consent" /></p>
						<p><fmt:message key="privacyPolicy4" /></p>
						<br/>
						<p>5. <fmt:message key="howWeUsePersonalData" /></p>
						<p><fmt:message key="privacyPolicy5" /></p>
					</div>
				</div>
			</div>
		</div>
		<!-- End Main Content -->
		
		<!-- Login Modal -->
	<div id="lgn-modal" class="modal fade" tabindex="-1">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close glyphicon glyphicon-remove" data-dismiss="modal"></button>
					<h3 id="lgn-header" style="margin-left: 15px;">
						<fmt:message key="alreadySignedUp" /><!-- Label -->
					</h3>
					<h3 id="lgn-err" class="hide" style="margin-left: 15px;">
						<fmt:message key="loginMessage" /><!-- Label -->
					</h3>
				</div>
				<div class="modal-body">
					<!-- Login form -->
					<form id="login-frm" action="login" method="post">
						<div id="lgn-username" class="row sgn-cntrl">
							<div class="row">
								<div class="col-md-4">
									<label class="label label-default sgn-labl">
										<fmt:message key="username" /><!-- Label -->
									</label>
								</div>
							</div>
							<div class="row">
								<div class="col-md-12">
									<input name="username" class="form-control" type="text" data-placement="top" data-toggle="popover" data-trigger="focus" />
								</div>
							</div>
						</div>
						<div id="lgn-password" class="row sgn-cntrl">
							<div class="row">
								<div class="col-md-4">
									<label class="label label-default sgn-labl">
										<fmt:message key="password" /><!-- Label -->
									</label>
								</div>
							</div>
							<div class="row">
								<div class="col-md-12">
									<input name="password" class="form-control" type="password" />
								</div>
							</div>
						</div>


						<div class="row sgn-cntrl"
							style="background-color: #303030; margin-top: 15px; float: right;">
						</div>


						<div class="modal-footer sgn-cntrl">
							<button id="login-btn" class="btn btn-info" type="submit"
								style="margin-right: 10px;">
								<fmt:message key="login" /><!-- Label -->
							</button>
							<button id="lgn-cancel" class="btn btn-danger" type="reset" data-dismiss="modal">
								<fmt:message key="cancel" /><!-- Label -->
							</button>
						</div>
					</form>
				</div>
			</div>
			<!-- End Login form -->
		</div>
	</div>
	<!-- End Login Modal -->

	<!-- Sign Up Modal -->
	<div id="sgn-modal" class="modal fade" tabindex="-1">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close glyphicon glyphicon-remove"
						data-dismiss="modal"></button>
					<h3 class="modal-title text-center">
						<fmt:message key="welcome" /><!-- Label -->
					</h3>
				</div>
				<div class="modal-body">
					<!-- Sign in form -->
					<form action="signup" id="sign-in-frm"
						method="post">
						<div id="sgn-firstname" class="row sgn-cntrl">
							<div class="row">
								<div class="col-md-4">
									<label class="label label-default sgn-labl">
										<fmt:message key="firstName" /><!-- Label -->
									</label>
								</div>
							</div>
							<div class="row">
								<div class="col-md-12">
									<input type="text" name="firstname" class="form-control" data-toggle="popover" data-trigger="focus" />
								</div>
							</div>
						</div>
						<div id="sgn-lastname" class="row sgn-cntrl">
							<div class="row">
								<div class="col-md-4">
									<label class="label label-default sgn-labl">
										<fmt:message key="lastName" /><!-- Label -->
									</label>
								</div>
							</div>
							<div class="row">
								<div class="col-md-12">
									<input type="text" name="lastname" class="form-control"
										data-toggle="popover" data-trigger="focus" />
								</div>
							</div>
						</div>
						<div id="sgn-email" class="row sgn-cntrl">
							<div class="row">
								<div class="col-md-4">
									<label class="label label-default sgn-labl">
										<fmt:message key="email" /><!-- Label -->
									</label>
								</div>
							</div>
							<div class="row">
								<div class="col-md-12">
									<input name="email" type="text" class="form-control"
										data-toggle="popover" data-trigger="focus" />
								</div>
							</div>
						</div>
						<div id="sgn-username" class="row sgn-cntrl">
							<div class="row">
								<div class="col-md-4">
									<label class="label label-default sgn-labl">
										<fmt:message key="username" /><!-- Label -->
									</label>
								</div>
							</div>
							<div class="row">
								<div class="col-md-12">
									<input name="username" class="form-control" type="text"
										data-toggle="popover" data-trigger="focus" />
								</div>
							</div>
						</div>
						<div id="sgn-password" class="row sgn-cntrl">
							<div class="row">
								<div class="col-md-4">
									<label class="label label-default sgn-labl">
										<fmt:message key="password" /><!-- Label -->
									</label>
								</div>
							</div>
							<div class="row">
								<div class="col-md-12">
									<input name="password" class="form-control" type="password"
										data-toggle="popover" data-trigger="focus" />
								</div>
							</div>
						</div>
						<div id="sgn-confirm" class="row sgn-cntrl">
							<div class="row">
								<div class="col-md-4">
									<label class="label label-default sgn-labl">
										<fmt:message key="confirmPassword" /><!-- Label -->
									</label>
								</div>
							</div>
							<div class="row">
								<div class="col-md-12">
									<input name="confirm" class="form-control" type="password"
										data-toggle="popover" data-trigger="focus" />
								</div>
							</div>
						</div>
						<div class="modal-footer sgn-cntrl">
							<button class="btn btn-info" type="submit"
								style="margin-right: 10px;">
								<fmt:message key="signUp" /><!-- Label -->
							</button>
							<button id="sgn-cancel" class="btn btn-danger" type="reset"
								data-dismiss="modal">
								<fmt:message key="cancel" /><!-- Label -->
							</button>
						</div>
					</form>
				</div>
			</div>
			<!-- End sign in form -->
		</div>
	</div>
	<!-- End Sign Up Modal -->

		<!-- Start Scripts -->
	<!-- <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script> -->
	<script src="js/jquery-1.12.0.js"></script>
	<script src="js/bootstrap.min.js"></script>
	<script>
/*
 * File name: index.js
 * Author: Christopher Elliott and Jeremy Chen
 * Date: 01/02/2015 (DD/MM/YYYY)
 * Public home page to welcome new/existing users.
 */
	    $(function(){
	    	$("#sgn-modal").modal({show:false, backdrop:false});
	    	$("#lgn-modal").modal({show:false, backdrop:false});
	    	
    		$("#lgn-btn").on("click", function() {
    			$(".lgn-pnl").removeClass("hide");
    		});
    		
    		$("#sgn-btn").on("click", function() {
    			//$("#welcome-msg").animate({right:'1350px'}, 1000);
    			//$("#welcome-msg").fadeTo("slow", 0.01);
    			$(".sgn-pnl").removeClass("hide").fadeIn("1000").animate({bottom:'250px'}, 700);
    			//$(".sgn-pnl").removeClass("hide").fadeIn("1000");  			
    		});
    		
    		$("#sgn-cancel").on("click", function(event) {
    			$("input.required").popover("disable");
    			$(".required").removeClass("required");
    			//$(".sgn-pnl").addClass("hide");
    		});
    		
    		$("#lgn-cancel").on("click", function(event) {
    			$(".required").removeClass("required");
    			$("#lgn-err").addClass("hide");
    			$("#lgn-header").removeClass("hide");
    		});
    		/*******************************************************************************
    		* Login form ajax request
    		*******************************************************************************/
    		$("#login-frm").submit(function(event) {
    			event.preventDefault();
    			$("input.required").popover("destroy");
    			$(".required").removeClass("required");
    			$.ajax({
    				method: "post",
    				dataType: "json",
    				url: "login",
    				data: $("#login-frm").serialize(),
    			}).done(function(data) {
    				console.log(data);
    				var datacontent = "";
    				if (data["isvalid"]) window.location.href = "dashboard.jsp";
    				else {
    					$("#lgn-username input").addClass("required");
    					$("#lgn-password input").addClass("required");
    					$("input.required").first().trigger("focus");
    					$("#lgn-modal").find(".modal-header").addClass("required");
    					$("#lgn-header").addClass("hide");
    					$("#lgn-err").removeClass("hide");
    				}
    			}).error(function(data) {
    				console.log(data);
    				alert("Oops! Sorry we cannot process the request at this time.");
    			});
    		});
    		/*******************************************************************************
    		* Sign up form ajax request
    		*******************************************************************************/
    		$("#sign-in-frm").submit(function(event) {
    			event.preventDefault();
    			$("input.required").popover("destroy");
    			$(".required").removeClass("required");
    			$.ajax({
    				method: "post",
    				dataType: "json",
    				url: "signup",
    				data: $("#sign-in-frm").serialize(),
    			}).done(function(data) {
    				console.log(data);
    				var isvalid = data["isvalid"];
    				if (isvalid) window.location.href = "dashboard.jsp";
    				for(var key in data["user"]) {
    					var datacontent = "";
    					switch (key) {
    					case "email":
    					case "username":
    						if (!data["user"][key].isvalid || !data["user"][key].isunique) {
        						$("#sgn-" + key + " input").addClass("required");
        						datacontent += "<p>" + data["user"][key]["message"] + "</p>";     						
        						$("#sgn-" + key + " input").attr("data-content", datacontent);
    						}
    						break;
    					case "firstname":
    					case "lastname":
    					case "confirm":
    						if (!data["user"][key].isvalid) {
        						$("#sgn-" + key + " input").addClass("required");
        						datacontent += "<p>" + data["user"][key]["message"] + "</p>"; 
        						$("#sgn-" + key + " input").attr("data-content", datacontent);
    						}
    						break;
    					case "password":
	    					if (!data["user"][key].isvalid || !data["user"][key].isconfirmed) {
	    						$("#sgn-" + key + " input").addClass("required");
	    						datacontent += "<p>" + data["user"][key]["message"] + "</p>";  						
        						$("#sgn-" + key + " input").attr("data-content", datacontent);
							}
    						break;
    					}
    				}
    				if (!isvalid) {
    					$("input.required").popover({
    						"html": true
    					});
    					$("input.required").first().trigger("focus");
    				}
    			}).fail(function(data) {
    				console.log(data);
    				alert("Oops! Sorry we cannot process the request at this time.");
    			});
    		});
    	});
    </script>
	<!-- End Scripts -->
</body>
</fmt:bundle>
</html>
