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
					<div class="row" id="publicEventList"></div>
				</div>
			</div>
			<!-- End Section -->
			
			<!-- Start Section -->
			<div id="thumbnails" class="jumbotron">
				<div class="container-fluid">
					<div class="row">
						<div class="col-md-12 text-center">
							<h2>Events you are attending or are invited to</h2>
						</div>
						<div class="col-md-3">
							<div class="thumbnail">
								<a href="images/ev1.jpg"
									title="Event 1"><img
									src="images/ev1.jpg" width="534" height="800"></a>
								<div class="caption">
									<h4>Event 1</h4>
									<h4>Wed, 24 Feb 8:30pm</h4>
									<p>Lorem ipsum dolor sit amet, consetetur sadipscing elitr,
										sed diam nonumy..</p>
									<div class="btn-toolbar">
										<div class="btn-group btn-group-sm">
											<a href="#" class="btn btn-primary" role="button"><i
												class="fa fa-eye"></i> See this event</a> <a href="#"
												class="btn btn-success" role="button"><i
												class="fa fa-share"></i> Share </a>
										</div>
									</div>
								</div>
							</div>
						</div>
						<div class="col-md-3">
							<div class="thumbnail">
								<a href="images/ev2.jpg"
									title="Event 2"><img
									src="images/ev2.jpg" width="534" height="800"></a>
								<div class="caption">
									<h4>Event 2</h4>
									<h4>Wed, 24 Feb 8:30pm</h4>
									<p>Lorem ipsum dolor sit amet, consetetur sadipscing elitr,
										sed diam nonumy..</p>
									<div class="btn-toolbar">
										<div class="btn-group btn-group-sm">
											<a href="#" class="btn btn-primary" role="button"><i
												class="fa fa-eye"></i> See this event</a> <a href="#"
												class="btn btn-success" role="button"><i
												class="fa fa-share"></i> Share </a>
										</div>
									</div>
								</div>
							</div>
						</div>
						<div class="col-md-3">
							<div class="thumbnail">
								<a href="images/ev3.jpg"
									title="Event 3"><img
									src="images/ev3.jpg" width="534" height="800"></a>
								<div class="caption">
									<h4>Event 3</h4>
									<h4>Wed, 24 Feb 8:30pm</h4>
									<p>Lorem ipsum dolor sit amet, consetetur sadipscing elitr,
										sed diam nonumy..</p>
									<div class="btn-toolbar">
										<div class="btn-group btn-group-sm">
											<a href="#" class="btn btn-primary" role="button"><i
												class="fa fa-eye"></i> See this event</a> <a href="#"
												class="btn btn-success" role="button"><i
												class="fa fa-share"></i> Share </a>
										</div>
									</div>
								</div>
							</div>
						</div>
						<div class="col-md-3">
							<div class="thumbnail">
								<a href="images/ev4.jpg"
									title="Event 4"><img
									src="images/ev4.jpg" width="534" height="800"></a>
								<div class="caption">
									<h4>Event 4</h4>
									<h4>Wed, 24 Feb 8:30pm</h4>
									<p>Lorem ipsum dolor sit amet, consetetur sadipscing elitr,
										sed diam nonumy..</p>
									<div class="btn-toolbar">
										<div class="btn-group btn-group-sm">
											<a href="#" class="btn btn-primary" role="button"><i
												class="fa fa-eye"></i> See this event</a> <a href="#"
												class="btn btn-success" role="button"><i
												class="fa fa-share"></i> Share </a>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div><!-- End row -->
				</div>
			</div>
			<!-- End Section -->
			
			<!-- Start Section -->
			<div id="thumbnails" class="jumbotron">
				<div class="container-fluid">
					<div class="row">
						<div class="col-md-12 text-center">
							<h2>Public events</h2>
						</div>
						<div class="col-md-3">
							<div class="thumbnail">
								<a href="images/ev1.jpg"
									title="Event 1"><img
									src="images/ev1.jpg" width="534" height="800"></a>
								<div class="caption">
									<h4>Event 1</h4>
									<h4>Wed, 24 Feb 8:30pm</h4>
									<p>Lorem ipsum dolor sit amet, consetetur sadipscing elitr,
										sed diam nonumy..</p>
									<div class="btn-toolbar">
										<div class="btn-group btn-group-sm">
											<a href="#" class="btn btn-primary" role="button"><i
												class="fa fa-eye"></i> See this event</a> <a href="#"
												class="btn btn-success" role="button"><i
												class="fa fa-share"></i> Share </a>
										</div>
									</div>
								</div>
							</div>
						</div>
						<div class="col-md-3">
							<div class="thumbnail">
								<a href="images/ev2.jpg"
									title="Event 2"><img
									src="images/ev2.jpg" width="534" height="800"></a>
								<div class="caption">
									<h4>Event 2</h4>
									<h4>Wed, 24 Feb 8:30pm</h4>
									<p>Lorem ipsum dolor sit amet, consetetur sadipscing elitr,
										sed diam nonumy..</p>
									<div class="btn-toolbar">
										<div class="btn-group btn-group-sm">
											<a href="#" class="btn btn-primary" role="button"><i
												class="fa fa-eye"></i> See this event</a> <a href="#"
												class="btn btn-success" role="button"><i
												class="fa fa-share"></i> Share </a>
										</div>
									</div>
								</div>
							</div>
						</div>
						<div class="col-md-3">
							<div class="thumbnail">
								<a href="images/ev3.jpg"
									title="Event 3"><img
									src="images/ev3.jpg" width="534" height="800"></a>
								<div class="caption">
									<h4>Event 3</h4>
									<h4>Wed, 24 Feb 8:30pm</h4>
									<p>Lorem ipsum dolor sit amet, consetetur sadipscing elitr,
										sed diam nonumy..</p>
									<div class="btn-toolbar">
										<div class="btn-group btn-group-sm">
											<a href="#" class="btn btn-primary" role="button"><i
												class="fa fa-eye"></i> See this event</a> <a href="#"
												class="btn btn-success" role="button"><i
												class="fa fa-share"></i> Share </a>
										</div>
									</div>
								</div>
							</div>
						</div>
						<div class="col-md-3">
							<div class="thumbnail">
								<a href="images/ev4.jpg"
									title="Event 4"><img
									src="images/ev4.jpg" width="534" height="800"></a>
								<div class="caption">
									<h4>Event 4</h4>
									<h4>Wed, 24 Feb 8:30pm</h4>
									<p>Lorem ipsum dolor sit amet, consetetur sadipscing elitr,
										sed diam nonumy..</p>
									<div class="btn-toolbar">
										<div class="btn-group btn-group-sm">
											<a href="#" class="btn btn-primary" role="button"><i
												class="fa fa-eye"></i> See this event</a> <a href="#"
												class="btn btn-success" role="button"><i
												class="fa fa-share"></i> Share </a>
										</div>
									</div>
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