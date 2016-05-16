<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
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
<link rel="stylesheet" href="css/bootstrap-datetimepicker.min.css" />
<div class="panel panel-default col-sm-12 col-md-4 col-lg-4 col-centered" style="margin-top: 125px">
	<div class="panel-body">
		<form action="/urestaurant/createEvent" method="post">
			<h1>
				<fmt:message key="organizeAnEvent" /><!-- Label -->
			</h1>
			
			<div class="form-group">
				<label>
					<fmt:message key="eventName" /><!-- Label -->
				</label> 
				<input id="evt-nm" name="name" type="text" class="form-control" placeholder="Enter an Event Name" />
			</div>

			<div class="form-group">
				<label>
					<fmt:message key="location" /><!-- Label -->
				</label> 
				<input id="evt-loc" name="location" type="text" class="form-control" placeholder="Enter an address" />
			</div>
			
			<div class="row">
				<div class="form-group col-md-6 col-lg-6">
				    <label>
						<fmt:message key="starts" /><!-- Label -->
					</label> 
				    <input id="evt-strt-dt" name="startdate" type="text" class="form-control date" />
				</div>
				<div class="form-group col-md-6 col-lg-6">
					<label>
						<fmt:message key="time" /><!-- Label -->
					</label> 
					<input id="evt-strt-tm" name="starttime" type="text" class="form-control time" />
				</div>
			</div>
			
			<div class="row">
				<div class="form-group col-md-6 col-lg-6">
				    <label>
						<fmt:message key="ends" /><!-- Label -->
					</label> 
				    <input id="evt-end-dt" name="enddate" type='text' class="form-control date" />
				</div>
				<div class="form-group col-md-6 col-lg-6">
					<label>
						<fmt:message key="time" /><!-- Label -->
					</label> 
					<input id="evt-end-tm" name="endtime" type="text" class="form-control time" />
				</div>
			</div>

			<div class="form-group">
				<label>
					<fmt:message key="description" /><!-- Label -->
				</label>
				<textarea id="evt-desc" name="description" class="form-control" rows="3" placeholder="Enter an event description..."></textarea>
			</div>

			<!-- <div class="form-group">
				<label for="InputPicture">
					<fmt:message key="uploadAGraphic" />
				</label> 
				<input type="file" id="exampleInputFile">
			</div> -->

			<div class="form-group">
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
				<button id="sgn-cancel" class="btn btn-danger" type="reset" data-dismiss="modal">
					<fmt:message key="cancel" /><!-- Label -->
				</button>
			</div>
		</form>
	</div>
</div>
<!-- Start Scripts -->
<script type="text/javascript" src="js/moment.min.js"></script>
<script type="text/javascript" src="js/bootstrap-datetimepicker.min.js"></script>
<script>
	$(function () {
		// start date datepicker init
	    $("#evt-strt-dt").datetimepicker({
	    	defaultDate: new Date(),
	    	format: "YYYY-MM-DD"
	    });
	 	// end date datepicker init
	    $("#evt-end-dt").datetimepicker({
	    	useCurrent: false,
	    	format: "YYYY-MM-DD"
	    });
	 	// datepicker events
	    $("#evt-strt-dt").on("dp.change", function (e) {
            $("#evt-end-dt").data("DateTimePicker").minDate(e.date);
        });
        $("#evt-end-dt").on("dp.change", function (e) {
            $("#evt-strt-dt").data("DateTimePicker").maxDate(e.date);
        });
	 	// time timepicker init
	    $(".time").datetimepicker({
	    	format: "LT"
	    });
	});
</script>
<!-- End Scripts -->
</fmt:bundle>