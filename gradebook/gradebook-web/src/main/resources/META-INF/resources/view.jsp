<%@ include file="init.jsp" %>

<%-- This is going to act as a main view for our portlet --%>
<div class="container-fluid-1280">
	<h1>
		<liferay-ui:message key="assignments"/>
	</h1>
	<%-- Defining Clay Management Toolbar and Connecting It With Assignment Managmenet Toolbar --%>
	<clay:management-toolbar
		disabled = "${assignmentCount eq 0}"
		displayContext = "${assignmentsManagementToolbarDisplayContext}"
		itemsTotal="${assignmentCount}"
		searchContainerId="assignmentEntries"
		selectable="false"
	/>
	
	<%-- Defining Search Container For The Above Mentioned Managment Toolbar --%>
	<liferay-ui:search-container id="assignmentEntries" emptyResultsMessage="no-assignments" iteratorURL="${portletURL}" total="${assignmentCount}">
		<%-- Providing The Result Set To The Search Container --%>
		<liferay-ui:search-container-results results="${assignments}"/>
		<liferay-ui:search-container-row className="com.liferay.training.gradebook.model.Assignment" modelVar="entry">
			<%-- Refering To External File Which Loads All Different Styling --%>
			<%@ include file="/assignment/entry_search_columns.jspf" %>
		</liferay-ui:search-container-row>
		<liferay-ui:search-iterator displayStyle="${assignmentsManagementToolbarDisplayContext.getDisplayStyle()}" markupView="lexicon"/>
	</liferay-ui:search-container>
</div>