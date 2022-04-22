package com.liferay.training.gradebook.web.portlet.action;

import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.portlet.LiferayActionResponse;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.training.gradebook.model.Assignment;
import com.liferay.training.gradebook.service.AssignmentService;
import com.liferay.training.gradebook.web.constants.GradebookPortletKeys;
import com.liferay.training.gradebook.web.constants.MVCCommandNames;
import com.liferay.training.gradebook.web.display.context.AssignmentsManagementToolbarDisplayContext;

import java.util.List;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * 
 * @author hgrahul
 * This Class Represent Rendering Of Main View Of Gradebook Portlet
 */
@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + GradebookPortletKeys.PORTLET_NAME,
		"mvc.command.name=/",
		"mvc.command.name=" + MVCCommandNames.VIEW_ASSIGNMENTS
	},
	service = MVCRenderCommand.class
)
public class ViewAssignmentsMVCRenderCommand implements MVCRenderCommand{
	@Override
	public String render(RenderRequest renderRequest, RenderResponse renderResponse) throws PortletException {
		// Add Assignment Related Information Here To Request
		addAssignmentListAttribute(renderRequest);
		
		// Add Clay Management Toolbar Related Attributes
		addManagementToolbarAttribute(renderRequest, renderResponse);
		
		// Redirecting To The Main View For Gradebook Portlet
		return "/view.jsp";
	}
	
	/**
	 * Add Clay Management Toolbar Context Object To The Request
	 */
	private void addManagementToolbarAttribute(RenderRequest renderRequest, RenderResponse renderResponse) {
		LiferayPortletRequest liferayPortletRequest = portal.getLiferayPortletRequest(renderRequest);
		LiferayPortletResponse liferayPortletResponse = portal.getLiferayPortletResponse(renderResponse);
		HttpServletRequest httpServletRequest = portal.getHttpServletRequest(renderRequest);
		
		// Have To Create AssignmentsManagementToolbarDisplayContext Need To Be Created and We Have Pass Above Generated Information As A Part Of Contrustor
		AssignmentsManagementToolbarDisplayContext assignmentsManagementToolbarDisplayContext = new AssignmentsManagementToolbarDisplayContext(liferayPortletRequest, liferayPortletResponse, httpServletRequest);
		
		// Set The Instance Of It Back To The Render Request
		renderRequest.setAttribute("assignmentsManagementToolbarDisplayContext", assignmentsManagementToolbarDisplayContext);
	}
	
	/**
	 * Adds Assignment List Related Attributes To Request Which Will Be Used In Appropriate Views
	 */
	private void addAssignmentListAttribute(RenderRequest renderRequest) {
		// Resolving Start and End Of The Search
		int currentPage = ParamUtil.getInteger(renderRequest, SearchContainer.DEFAULT_CUR_PARAM, SearchContainer.DEFAULT_CUR); 
		int delta = ParamUtil.getInteger(renderRequest, SearchContainer.DEFAULT_DELTA_PARAM, SearchContainer.DEFAULT_DELTA);
		
		int start = ((currentPage > 0) ? (currentPage - 1) : 0) * delta;
		int end = start + delta;
		
		// Need To Have Information About The Sorting
		String orderByCol = ParamUtil.getString(renderRequest, "orderByCol", "title");
		String orderByType = ParamUtil.getString(renderRequest, "orderByType", "asc");
		
		// Currently We have no keywords as default
		String keywords =  ParamUtil.getString(renderRequest, "keywords");
		
		// Meed a Comparator Instance For Assignments
		OrderByComparator<Assignment> orderByComparator = OrderByComparatorFactoryUtil.create("Assignment", orderByCol, !("asc").equals(orderByType));
		
		// For Group Id We Need To Create A ThemeDisplay
		ThemeDisplay themeDisplay = (ThemeDisplay) renderRequest.getAttribute(WebKeys.THEME_DISPLAY);
		
		// Need To Call For Assignment Service To Retrieve List Of Assignment If Any
		List<Assignment> assignments = assignmentService.getAssignmentsByKeywords(themeDisplay.getScopeGroupId(), keywords, start, end, orderByComparator);
		long assignmentCount = assignmentService.getAssignmentsCountByKeywords(themeDisplay.getScopeGroupId(), keywords);
		
		// Need To Set It The Request.
		renderRequest.setAttribute("assignments", assignments);
		renderRequest.setAttribute("assignmentCount", assignmentCount);
	}
	
	@Reference
	private AssignmentService assignmentService;
	
	@Reference
	private Portal portal;
}
