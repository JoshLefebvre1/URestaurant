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
<div class="panel panel-default col-centered" style="margin-top: 125px">
	<div class="panel-body">
		<!-- Start Section -->
		<div id="thumbnails" class="jumbotron">
			<div class="container-fluid">
				<div class="row">
					<div class="col-md-12 col-xs-12 text-center">
						<h2><fmt:message key="friends" /></h2>
					</div>
					<div class="col-md-3 col-xs-12">
						<div class="thumbnail" >
							<a href="#" data-toggle="modal"><img class="img-circle" src="images/avatar01.jpg" alt="Friend1"></a>
							<div class="caption text-center" >
								<h3>Friend1</h3>
								<p><a href="#" data-toggle="modal" class="btn btn-primary" role="button">Unfriend</a></p>
							</div>
						</div>
					</div>
					<div class="col-md-3 col-xs-12">
						<div class="thumbnail">
							<a href="#" data-toggle="modal">
							<img class="img-circle" src="images/avatar01.jpg" alt="Friend2"></a>
							<div class="caption text-center">
								<h3>Friend2</h3>
								<p><a href="#" data-toggle="modal" class="btn btn-primary" role="button">Unfriend</a></p>
							</div>
						</div>
					</div>
					<div class="col-md-3 col-xs-12">
						<div class="thumbnail">
							<a href="#" data-toggle="modal">
							<img class="img-circle" src="images/avatar01.jpg" alt="Friend3"></a>
							<div class="caption text-center">
								<h3>Friend3</h3>
								<p><a href="#" data-toggle="modal" class="btn btn-primary" role="button">Unfriend</a></p>
							</div>
						</div>
					</div>
					<div class="col-md-3 col-xs-10">
						<div class="thumbnail">
							<a href="#" data-toggle="modal">
							<img class="img-circle" src="images/avatar01.jpg" alt="Friend4"></a>
							<div class="caption text-center">
								<h3>Friend4</h3>
								<p><a href="#" data-toggle="modal" class="btn btn-primary" role="button">Unfriend</a></p>
							</div>
						</div>
					</div>
				</div><!-- End row -->
			</div>
		</div>
		<!-- End Section -->
	</div>
</div>
<!-- Start Scripts -->
<script>
	
</script>
<!-- End Scripts -->
</fmt:bundle>