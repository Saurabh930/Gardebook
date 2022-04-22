package com.liferay.training.gradebook.web.portlet.action;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.training.gradebook.model.Assignment;
import com.liferay.training.gradebook.service.AssignmentService;
import com.liferay.training.gradebook.web.constants.GradebookPortletKeys;
import com.liferay.training.gradebook.web.constants.MVCCommandNames;

import java.text.DateFormat;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * 
 * @author hgrahul
 * This Class Represent Showing Single Assignment View (Also Listing Multiple Submission If Any)
 */
@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + GradebookPortletKeys.PORTLET_NAME,
		"mvc.command.name=" + MVCCommandNames.VIEW_ASSIGNMENT
	},
	service = MVCRenderCommand.class
)
public class ViewSingleAssignmentMVCRenderCommand implements MVCRenderCommand{
	@Override
	public String render(RenderRequest renderRequest, RenderResponse renderResponse) throws PortletException {
		// Need To Get Assignment Id From Page Request
		long assignmentId = ParamUtil.getLong(renderRequest, "assignmentId", 0);
		
		try {
			// Call For The Assignment Service For Information Retrival
			Assignment assignment = assignmentService.getAssignment(assignmentId);
			
			// Need A Date Format To Show Date Related Information In A Different Style
			DateFormat dateFormat = DateFormatFactoryUtil.getSimpleDateFormat("EEEEE, MMMMM dd, yyyy", renderRequest.getLocale());
			
			// Setting Up The Request With Assignment Related Information
			renderRequest.setAttribute("assignment", assignment);
			renderRequest.setAttribute("dueDate", dateFormat.format(assignment.getDueDate()));
			renderRequest.setAttribute("createDate", dateFormat.format(assignment.getCreateDate()));
			
			// Want To Have A Back Button
			ThemeDisplay themeDisplay = (ThemeDisplay) renderRequest.getAttribute(WebKeys.THEME_DISPLAY);
			PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();
			
			String redirect = renderRequest.getParameter("redirect");
			
			portletDisplay.setShowBackIcon(true);
			portletDisplay.setURLBack(redirect);
			
			return "/assignment/view_assignment.jsp";
		}
		catch(PortalException pe) {
			throw new PortletException(pe);
		}
	} 
	
	@Reference
	private AssignmentService assignmentService;
}
