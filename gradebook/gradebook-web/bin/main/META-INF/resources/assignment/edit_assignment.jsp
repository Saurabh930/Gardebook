<%@ include file="../init.jsp"%>

<%-- Generating An Action URL For Adding Purpose And Editing Purpose --%>
<c:choose>
	<c:when test="${not empty assignment}">
		<portlet:actionURL var="assignmentActionURL" name="<%= MVCCommandNames.EDIT_ASSIGNMENT %>">
			<portlet:param name="redirect" value="${param.redirect}"/>
		</portlet:actionURL>
		<c:set var="editTitle" value="edit-assignment"/>
	</c:when>
	<c:otherwise>
		<portlet:actionURL var="assignmentActionURL" name="<%= MVCCommandNames.ADD_ASSIGNMENT %>">
			<portlet:param name="redirect" value="${param.redirect}"/>
		</portlet:actionURL>
		<c:set var="editTitle" value="add-assignment"/>
	</c:otherwise>
</c:choose>

<div class="container-fluid-1280 edit-assignment">
	<h1>
		<liferay-ui:message key="${editTitle}"/>
	</h1>
	
	<%-- Auto Entity Field --%>
	<aui:model-context bean="${assignment}" model="${assignmentClass}"/>
	
	<aui:form action="${assignmentActionURL}" name="fm">
		<aui:input name="assignmentId" field="assignmentId" type="hidden"/>
		<aui:fieldset-group markupView="lexicon">
			<aui:fieldset>
				<aui:input name="title"/>
				<aui:input name="description"/>
				<aui:input name="dueDate"/>
			</aui:fieldset>
		</aui:fieldset-group>	
		<aui:button-row>
			<aui:button cssClass="btn btn-primary" type="submit" />
			<aui:button cssClass="btn btn-secondary" onClick="${param.redirect}" type="cancel" />
		</aui:button-row>
	</aui:form>
</div>