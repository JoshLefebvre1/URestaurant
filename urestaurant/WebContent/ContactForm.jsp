<%@page import="connections.DatabaseConnectionFactory"%>
<%@page import="utilities.Contact"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!-- File name: ContactForm.jsp
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
<% utilities.Contact contact = new utilities.Contact(); %>
<%
if (request.getParameter("id") != null && request.getParameter("id") != "") {
	java.sql.ResultSet resultSet = null;
	java.sql.Connection connection = null;
	java.sql.PreparedStatement selectStmt = null;
	try {
		connection = DatabaseConnectionFactory.getConnectionFactory().getConnection();
		final String selectSQL = "SELECT * FROM contacts "
							   + "LEFT JOIN emails ON emails.contact_id = contacts.id "
							   + "LEFT JOIN phone_numbers ON phone_numbers.contact_id = contacts.id "
							   + "WHERE contacts.id = ? ORDER BY contacts.id, emails.contact_type_id";
		selectStmt = connection.prepareStatement(selectSQL, java.sql.Statement.RETURN_GENERATED_KEYS);
		selectStmt.setInt(1, java.lang.Integer.parseInt(request.getParameter("id")));
		resultSet = selectStmt.executeQuery();
		for(int i = 0, id = 0; resultSet.next(); ){
			id = resultSet.getInt("contacts.id");
			if (id != i) {
				i = id;
				contact.setId(id);
				contact.setFirstName(resultSet.getString("firstname"));
				contact.setLastName(resultSet.getString("lastname"));
				if (resultSet.getInt("photo_id") > 0) contact.setPhotoId(resultSet.getInt("photo_id"));
				if (resultSet.getInt("user_link_id") > 0) contact.setUserLinkId(resultSet.getInt("user_link_id"));
			}
			if (resultSet.getInt("emails.contact_type_id") != 6)
				contact.getEmails().add(resultSet.getString("email"));
			if (resultSet.getInt("phone_numbers.contact_type_id") != 6)
				contact.getPhoneNumbers().add(resultSet.getString("phone_number"));
		}
	} catch (java.sql.SQLException ex) {
		ex.printStackTrace();
	} finally {
		if (resultSet != null) resultSet.close();
		if (connection != null) connection.close();
		if (selectStmt != null) selectStmt.close();
	}
}
%>

<!-- Add contact form -->
<form id="addContactForm" action="addcontact" method="post">
	<input type="hidden" name="id" value="<%=contact.getId() %>" />
	<div id="ctc-firstname-div" class="row sgn-cntrl">
		<div class="row">
			<div class="col-md-4">
				<label class="label label-default sgn-labl">
					<fmt:message key="firstName" />
				</label>
			</div>
		</div>
		<div class="row">
			<div class="col-md-12">
				<input name="firstname" class="form-control" value="<%=(contact.getFirstName() != null ? contact.getFirstName() : "") %>" 
					type="text" data-placement="top" data-toggle="popover" data-trigger="focus" />
			</div>
		</div>
	</div>
	<div id="ctc-lastname-div" class="row sgn-cntrl">
		<div class="row">
			<div class="col-md-4">
				<label class="label label-default sgn-labl">
					<fmt:message key="lastName" />
				</label>
			</div>
		</div>
		<div class="row">
			<div class="col-md-12">
				<input name="lastname" class="form-control" value="<%=(contact.getLastName() != null ? contact.getLastName() : "") %>"
					type="text" data-placement="top" data-toggle="popover" data-trigger="focus" />
			</div>
		</div>
	</div>
	<div id="contactPhone" class="row sgn-cntrl">
<% int count = 0; %>
<%
if (contact.getPhoneNumbers().size() > 0) {
	for (java.lang.String phone : contact.getPhoneNumbers()) {
		if (phone != null) {
%>
		<div class="row">
			<div class="col-md-4">
				<label class="label label-default sgn-labl">
					<fmt:message key="phoneNumber" />
				</label>
			</div>
		</div>
		<div class="row">
			<div class="col-md-12">
				<input name="phone<%=count++ %>" class="form-control ctc-phone" value="<%=phone %>"
					type="tel" data-placement="top" data-toggle="popover" data-trigger="focus"  />
			</div>
		</div>
<%
		}
	} 
}%>
		<div class="row">
			<div class="col-md-4">
				<label class="label label-default sgn-labl">
					<fmt:message key="phoneNumber" />
				</label>
			</div>
		</div>
		<div class="row">
			<div class="col-md-12">
				<input name="phone<%=count %>" class="form-control ctc-phone" type="tel" data-placement="top" data-toggle="popover" data-trigger="focus"  />
			</div>
		</div>
	</div>
	<div id="contactEmail" class="row sgn-cntrl">
		<label id="sv-lbl" class="hide" data-lbl="<fmt:message key="email" />"></label>
<%
count = 0;
if (contact.getEmails().size() > 0) {
	for (java.lang.String email : contact.getEmails()) {
%>
		<%
		if (count == 0) {
		%>
		<div class="prm-email">
			<div class="row">
				<div class="col-md-4">
					<label class="label label-default prm-em-lbl">
						<fmt:message key="primaryEmail" />
					</label>
		<%
		} else {
		%>
		<div class="email">
			<div class="row">
				<div class="col-md-4">
					<label class="label label-default ctc-em-lbl">
						<fmt:message key="email" />
					</label>
		<%}%>
				</div>
			</div>
			<div class="row">
				<div class="col-md-12">
					<input name="email<%=count++ %>" class="form-control ctc-email" value="<%=email %>"
						type="email" data-placement="top" data-toggle="popover" data-trigger="focus"  />
				</div>
			</div>
		</div>	
<%	}%>
		<div class="email">
			<div class="row">
				<div class="col-md-4">
					<label class="label label-default ctc-em-lbl">
						<fmt:message key="email" />
					</label>
				</div>
			</div>
			<div class="row">
				<div class="col-md-12">
					<input name="email<%=count++ %>" class="form-control ctc-email" type="email" data-placement="top" data-toggle="popover" data-trigger="focus"  />
				</div>
			</div>
		</div>
<%} else {%>
		<div class="email">
			<div class="row">
				<div class="col-md-4">
					<label class="label label-default prm-em-lbl">
						<fmt:message key="primaryEmail" />
					</label>
				</div>
			</div>
			<div class="row">
				<div class="col-md-12">
					<input name="email<%=count++ %>" class="form-control ctc-email" type="email" data-placement="top" data-toggle="popover" data-trigger="focus"  />
				</div>
			</div>
		</div>
<%}%>
	</div>	
	
	<div class="row">
		<div class="col-md-12">
			<input id="usr-lnk-lbl" type="hidden" value="<fmt:message key="urestaurantAccount" />" />
			<input name="usr-lnk" type="hidden" value="<%=contact.getUserLinkId() %>" />
<%if (contact.getUserLinkId() > 0) {%>
			<div class="user-links list-group" style="margin: 20px 10px 10px 10px;">
				<h5><fmt:message key="urestaurantAccount" />:</h5>
				<div class="list-group-item" data-id="<%=contact.getUserLinkId() %>">
					<div class="userItem">
						<img class="img-circle" width="18" src="getimage?img=<%=contact.getPhotoId() %>" />
						<h4 class="list-group-item-heading"><%=contact.getFirstName() %> <%=contact.getLastName() %></h4>
					</div>
				</div>	
			</div>
<%} else {%>
			<div class="user-links list-group hide" style="margin: 20px 10px 10px 10px;">
								
			</div>
<%}%>
		</div>
	</div>
	
	<div class="modal-footer sgn-cntrl" style="margin-top: 25px;">
		<button id="add-btn" class="btn btn-info" type="submit"
			style="margin-right: 10px;">
			<fmt:message key="add" /><!-- Label -->
		</button>
		<button id="addContact-cancel" class="btn btn-danger" type="reset" data-dismiss="modal">
			<fmt:message key="cancel" /><!-- Label -->
		</button>
	</div>
</form>
	
</fmt:bundle>
<script src="js/jquery.maskedinput.min.js"></script>
<script>
	$(function() {
		$(".ctc-phone").mask("(999) 999-9999", { placeholder :" " });
		
		var emailLabel = $("#sv-lbl").attr("data-lbl"); // get label
		var user_link = $("input[name=\"usr-lnk\"]").val();
		
		/*******************************************************************************
		* Add more email inputs
		*******************************************************************************/	    	
    	$("#addContactForm").on("blur", ".ctc-email", function(event) {
    		var ctc = $(".ctc-email").last();
    		if (this !== ctc[0] && !($(this).val())) {
    			$(this).parents(".email").hide();
    		}
    	});
		
    	$("#addContactForm").on("submit", function(event) {
    		event.preventDefault();
    		console.log(event);
    		$("input.required").popover("destroy");
			$(".required").removeClass("required");
			$.ajax({
				method: "post",
				dataType: "json",
				url: "addcontact",
				data: $("#addContactForm").serialize(),
			}).done(function(data) {
				console.log(data);
				var isvalid = data["isvalid"];
				// firstname validation
				if (!data["contact"]["firstname"].isvalid) {
					$("input[name=\"firstname\"]").addClass("required");
					var datacontent = "<p>" + data["contact"]["firstname"]["message"] + "</p>"; 
					$("input[name=\"firstname\"]").attr("data-content", datacontent);
				}
				// lastname validation
				if (data["contact"]["lastname"] && !data["contact"]["lastname"].isvalid) {
					$("input[name=\"lastname\"]").addClass("required");
					var datacontent = "<p>" + data["user"][key]["message"] + "</p>"; 
					$("input[name=\"lastname\"]").attr("data-content", datacontent);
				}
				for (var i = 0; data["contact"]["email" + i]; ++i) {
					if (data["contact"]["email" + i] && !data["contact"]["email" + i].isvalid) {
						$("input[name=\"email" + i + "\"]").addClass("required");
						var datacontent = "<p>" + data["contact"]["email" + i]["message"] + "</p>";     						
						$("input[name=\"email" + i + "\"]").attr("data-content", datacontent);
					}
				}
				for (var i = 0; data["contact"]["phone" + i]; ++i) {
					if (data["contact"]["phone" + i] && !data["contact"]["phone" + i].isvalid) {
						$("input[name=\"phone" + i + "\"]").addClass("required");
						var datacontent = "<p>" + data["contact"]["phone" + i]["message"] + "</p>";     						
						$("input[name=\"phone" + i + "\"]").attr("data-content", datacontent);
					}
				}
				if (isvalid) {
					//$("#add-contact-modal").modal({show:false, backdrop:false});
					$("#add-contact-modal").modal("hide");
					getAllContacts(); // this function is declared in contacts.jsp; it is global so should be available here
				} else {
					$("input.required").popover({
						"html": true
					});
					$("input.required").first().trigger("focus");
				}
			}).error(function(data) {
				console.log(data);
				alert("21Oops! Sorry we cannot process the request at this time.");
			});
    	});
    	
    	var re = /\S+@\S+\.\S+/;
    	$("#addContactForm").on("keyup", ".ctc-email", function(event) {
    		getUserLink($(this).val());
    		
    		var ctc = $(".ctc-email").last();
    		if (this === ctc[0]) {
    			//console.log(emailLabel);
    			var html = "<div class=\"email\"><div class=\"row\"><div class=\"col-md-4\"><label class=\"label label-default sgn-labl\">" + emailLabel + "</label></div></div>";
    			html += "<div class=\"row\"><div class=\"col-md-12\"><input name=\"email" + $(".ctc-email").length;
    			html += "\" class=\"form-control ctc-email\" type=\"text\" data-placement=\"top\" data-toggle=\"popover\" data-trigger=\"focus\" /></div></div></div>"
    			$("#contactEmail").append(html);
    		}
    	});
    	$("#addContactForm").on("click", ".ctc-email", function(event) {
    		getUserLink($(this).val());
    	});
    	
    	$("#addContactForm").on("click", ".list-group-item", function(event) {
    		var id = $(this).attr("data-id");
    		$("input[name=\"usr-lnk\"]").val(id);	    		
    	});
    	
    	var getUserLink = function(val) {
    		// if value is a valid email check for user account
    		if ((!user_link || user_link == 0) && re.test(val)) {
    			console.log(true);
    			$(".user-links").html("");
        		$.ajax({
    				method: "post",
    				dataType: "json",
    				url: "userlink",
    				data: { email : val },
    			}).done(function(data) {
    				//console.log(data["users"]);
    				var html_string = "<h5>" + $("#usr-lnk-lbl").val() + ":</h5>";
    				for (var user in data["users"]) {
    					html_string += "<div class=\"list-group-item\" data-id=\"" + data["users"][user]["id"] + "\"><div class=\"userItem\">";
    					html_string += "<img class=\"img-circle\" width=\"18\" src=\"/urestaurant/getimage?img=" + data["users"][user]["photo_id"] + "\" />";
    					html_string += "<h4 class=\"list-group-item-heading\">" + data["users"][user]["firstname"] + " " + data["users"][user]["lastname"] + "</h4></div>";
    					html_string += "<p class=\"list-group-item-text\">" + data["users"][user]["username"] + "</p></div>";
    				}
    				$(".user-links").html(html_string);
    				if (data["users"].length > 0) $(".user-links").removeClass("hide");
    				else $(".user-links").addClass("hide");
    			}).error(function(data) {
    				console.log(data);
    				alert("22Oops! Sorry we cannot process the request at this time.");
    			});
    		}
    	};
	});
</script>