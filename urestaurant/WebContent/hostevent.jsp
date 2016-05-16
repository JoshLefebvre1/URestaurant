<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!-- File name: dashboard.html
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
		<h1>
			<fmt:message key="organizeAnEvent" /><!-- Label -->
		</h1>
		<form id="event-frm" action="createEvent" method="post" enctype="multipart/form-data">
			<div class="form-group">
				<label>
					<fmt:message key="eventName" /><!-- Label -->
				</label> 
				<input id="evt-name" name="name" type="text" class="form-control" placeholder="<fmt:message key="eventNamePlaceholder" />" data-toggle="popover" data-placement="top" data-trigger="focus" />
			</div>

			<div class="form-group">
				<label>
					<fmt:message key="location" /><!-- Label -->
				</label> 
				<input id="evt-location" name="location" type="text" class="form-control" placeholder="<fmt:message key="addressPlaceholder" />" data-toggle="popover" data-placement="top" data-trigger="focus">
			</div>
			<div class="row form-group">
				<div class="col-xs-12 col-sm-6 col-md-3 col-lg-3">
				    <label>
						<fmt:message key="starts" /><!-- Label -->
					</label> 
				    <input id="evt-startdate" name="startdate" type="text" class="form-control date" data-toggle="popover" data-placement="top" data-trigger="focus" />
				</div>
				<div class="col-xs-12 col-sm-6 col-md-3 col-lg-3">
					<label>
						<fmt:message key="time" /><!-- Label -->
					</label> 
					<input id="evt-starttime" name="starttime" type="text" class="form-control time" data-toggle="popover" data-placement="top" data-trigger="focus" />
				</div>
				<div class="col-xs-12 col-sm-6 col-md-3 col-lg-3">
				    <label>
						<fmt:message key="ends" /><!-- Label -->
					</label> 
				    <input id="evt-enddate" name="enddate" type='text' class="form-control date" data-toggle="popover" data-placement="top" data-trigger="focus" />
				</div>
				<div class="col-xs-12 col-sm-6 col-md-3 col-lg-3">
					<label>
						<fmt:message key="time" /><!-- Label -->
					</label> 
					<input id="evt-endtime" name="endtime" type="text" class="form-control time" data-toggle="popover" data-placement="top" data-trigger="focus" />
				</div>
			</div>

			<div class="form-group">
				<label>
					<fmt:message key="description" /><!-- Label -->
				</label>
				<textarea id="evt-description" name="description" class="form-control" rows="3" placeholder="<fmt:message key="descriptionPlaceholder" />" data-toggle="popover" data-placement="top" data-trigger="focus"></textarea>
			</div>
			
			<div class="form-group">
				<label>
					<fmt:message key="uploadGraphic" /><!-- Label -->
				</label>
				<input id="evt-graphic" name="graphic" type="file" accept="image/gif,image/jpeg,image/png" />
			</div>
			
			<!--<div class="form-group">
				<label>
					<fmt:message key="uploadAGraphic" />
				</label> 
				<input id="evt-file" class="box__file" name="file" type="file" accept="image/gif,image/jpeg,image/png" />
				<div class="form-group box">
					 <input id="evt-file" type="file" accept="image/gif,image/jpeg,image/png" /> 
					<div class="box__input">
						<input id="evt-file" class="box__file" name="file" type="file" accept="image/gif,image/jpeg,image/png" />
					    <label for="evt-file">
					    	<strong><fmt:message key="chooseAFile" />&nbsp</strong>
					    	<span class="box__dragndrop"><fmt:message key="orDragItHere" /></span>.
					    </label>
					    <button class="box__button" type="submit">
					    	<fmt:message key="upload" />
					    </button>
				  	</div>
				  	<div class="box__uploading"><fmt:message key="uploading" />&hellip;</div>
				  	<div class="box__success"><fmt:message key="done" />!</div>
				  	<div class="box__error"><fmt:message key="error" />!<span></span>.</div>
				</div>
			</div>-->

			<div id="evt-ispublic" class="form-group" data-toggle="popover" data-placement="top" data-trigger="hover">
				<label>
					<fmt:message key="eventSettings" /><!-- Label -->
				</label>
				<div class="radio">
					<label> 
						<input id="radioOption1" type="radio" name="ispublic" value="true" checked />
						<fmt:message key="publicEvent" /><!-- Label -->
					</label>
				</div>
				<div class="radio">
					<label>
						<input id="radioOption2" type="radio" name="ispublic" value="false" />
						<fmt:message key="privateEvent" /><!-- Label -->
					</label>
				</div>
			</div>

			<div class="modal-footer sgn-cntrl">
				<button class="btn btn-info" type="submit" style="margin-right: 10px;">
					<fmt:message key="create" /><!-- Label -->
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
	<script src="js/bootstrap-datetimepicker.min.js"></script>
	<script>
	
		/* file drag n drop stuff reference: https://css-tricks.com/drag-and-drop-file-uploading/ 
		var file_input = $('.box');
		var droppedFile = false;
	    var isAdvancedUpload = function() {
		  	var div = document.createElement('div');
		  	return (('draggable' in div) || ('ondragstart' in div && 'ondrop' in div)) && 'FormData' in window && 'FileReader' in window;
		}();
		var $input    = file_input.find('input[type="file"]'),
	    	$label    = file_input.find('label'),
	    	showFiles = function(file) {
	      		$label.text(file.name);
	    	};*/
		
		$(function() {
			tinymce.init({ 
				selector:'textarea',
				setup: function (editor) {
			        editor.on('change', function () {
			            editor.save();
			        });
			    }
			});
			
			// start date datepicker init
		    $("#evt-startdate").datetimepicker({
		    	format: "YYYY-MM-DD"
		    });
		 	// end date datepicker init
		    $("#evt-enddate").datetimepicker({
		    	useCurrent: false,
		    	format: "YYYY-MM-DD"
		    });
		 	// datepicker events
		    $("#evt-startdate").on("dp.change", function (e) {
		    	$(this).removeClass("required");
	            $("#evt-enddate").data("DateTimePicker").minDate(e.date);
	        });
	        $("#evt-enddate").on("dp.change", function (e) {
	        	$(this).removeClass("required");
	            $("#evt-startdate").data("DateTimePicker").maxDate(e.date);
	        });   
		 	// time timepicker init
		    $(".time").datetimepicker({
		    	format: "LT"
		    });
		    $(".time").on("dp.change", function (e) {
	        	$(this).removeClass("required");
	        });
		    
		    /* file drag n drop stuff reference: https://css-tricks.com/drag-and-drop-file-uploading/ 
	    	if (isAdvancedUpload) {
	    		file_input.addClass('has-advanced-upload');
	    	}
	    	if (isAdvancedUpload) {	
			  	file_input.on('drag dragstart dragend dragover dragenter dragleave drop', function(e) {
				    e.preventDefault();
				    e.stopPropagation();
			  	}).on('dragover dragenter', function() {
			  		file_input.addClass('is-dragover');
			  	}).on('dragleave dragend drop', function() {
			  		file_input.removeClass('is-dragover');
			  	}).on('drop', function(e) {
			    	droppedFile = e.originalEvent.dataTransfer.file;
			  	});
    		}*/
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
	        
	        //tinyMCE.triggerSave();
			
			$.ajax({
				type: "post",
				url: "createvent",
				data: formData,
				cache: false,
			    contentType: false,
			    processData: false,			
			}).done(function(data) {
				console.log(data);
				if (data["redirect"]) window.location.href = "index.jsp";
				var isvalid = data["isvalid"];
				if (isvalid) window.location.href = "dashboard.jsp";
				for(var key in data["event"]) {
					var datacontent = "";
					if (!data["event"][key].isvalid) {
						$("#evt-" + key).addClass("required");
						datacontent += "<p>" + data["event"][key]["message"] + "</p>";
						//if (key !== "startdate" && key !== "starttime" && key !== "enddate" && key !== "enddate")
						$("#evt-" + key).attr("data-content", datacontent);
					}
				}
				if (!isvalid) {
					$("input.required").popover({
						"html": true
					});
					$("input.required").first().trigger("focus");
				}
			}).error(function(data) {
				console.log(data);
				alert("13Oops! Sorry we cannot process the request at this time.");
			});
		});
	</script>
    <script>
      function initMap() {
		var neBounds = new google.maps.LatLng(47.072109, -78.222656)
		var swBounds = new google.maps.LatLng(44.623831, -80.683594)
		var lanLonBounds = new google.maps.LatLngBounds(); 
		lanLonBounds.extend(neBounds);
		lanLonBounds.extend(swBounds);
		
        var input = document.getElementById('evt-location');
        var autocomplete = new google.maps.places.Autocomplete(input, {bounds: lanLonBounds});
        autocomplete.addListener('place_changed', function() {
          var place = autocomplete.getPlace();
          if (!place.geometry) {
            console.log("autocomplete's returned place contains no geometry");
            return;
          }
        });
      }//end initMap()
    </script>
	<script src="https://maps.googleapis.com/maps/api/js?libraries=places&callback=initMap" async defer></script>
	<!-- End Scripts -->
</body>
</fmt:bundle>
</html>
