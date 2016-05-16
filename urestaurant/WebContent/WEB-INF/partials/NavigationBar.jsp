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
					<a class="navbar-brand" href="dashboard.jsp">Urestaurant</a>
				</div>
				<div id="topnav" class="collapse navbar-collapse">
					<ul class="nav navbar-nav">
						<li class="dropdown"><a href="#" class="dropdown-toggle"
							data-toggle="dropdown" role="button" aria-haspopup="true"
							aria-expanded="false"> <fmt:message key="language" />
								<!-- Label --> <span class="caret"></span>
						</a>
							<ul class="dropdown-menu">
								<c:url value="dashboard.jsp" var="engURL">
									<c:param name="locale" value="en_CA" />
								</c:url>
								<li><a href="${engURL}">English (Canada)</a></li>
								<c:url value="dashboard.jsp" var="freURL">
									<c:param name="locale" value="fr_CA" />
								</c:url>
								<li><a href="${freURL}">Français (Canada)</a></li>
							</ul></li>
						<li class="dropdown"><a href="#" class="dropdown-toggle"
							data-toggle="dropdown" role="button" aria-haspopup="true"
							aria-expanded="false"> <i class="fa fa-calendar"></i> <fmt:message key="events" />
								<!-- Label --> <span class="caret"></span>
						</a>
							<ul class="dropdown-menu">
								<li><a id="browse-events" href="dashboard.jsp"> <fmt:message
											key="browseEvents" />
										<!-- Label -->
								</a></li>
								<li><a id="host-event" href="hostevent.jsp"> <fmt:message
											key="hostAnEvent" />
										<!-- Label -->
								</a></li>
							</ul></li>
						<li><a id="friends-link" href="contacts.jsp" role="button"> <i
								class="fa fa-users"></i> <fmt:message key="contacts" />
								<!-- Label -->
						</a></li>
					</ul>
					<ul class="nav navbar-nav navbar-right">
						<li><a href="/urestaurant/PopulateEditUserServlet" data-toggle="modal" id="lgn-btn"
							role="button"> <i class="fa fa-user"></i> <%=request.getSession(false).getAttribute("username") %>
						</a></li>
						<li><a href="/urestaurant/logout" data-toggle="modal"
							id="sgn-btn" role="button"> <i class="fa fa-sign-out"></i> <fmt:message key="logout" />
								<!-- Label -->
						</a></li>
					</ul>
				</div>
			</div>
		</nav>
		<!-- End Navigation Bar -->