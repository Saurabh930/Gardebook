package com.liferay.training.gradebook.web.portlet.action;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.training.gradebook.exception.NoSuchAssignmentException;
import com.liferay.training.gradebook.model.Assignment;
import com.liferay.training.gradebook.service.AssignmentService;
import com.liferay.training.gradebook.web.constants.GradebookPortletKeys;
import com.liferay.training.gradebook.web.constants.MVCCommandNames;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * 
 * @author hgrahul
 * This Class Represent Rendering View For Editing and Adding Assignment
 * 
 */
@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + GradebookPortletKeys.PORTLET_NAME,
		"mvc.command.name=" + MVCCommandNames.EDIT_ASSIGNMENT,
		"mvc.command.name=" + MVCCommandNames.ADD_ASSIGNMENT
	},
	service = MVCRenderCommand.class
)
public class EditAssignmentMVCRenderCommand implements MVCRenderCommand{
	@Override
	public String render(RenderRequest renderRequest, RenderResponse renderResponse) throws PortletException {
		// Let's Have An Empty Assignment
		Assignment assignment = null;
		
		// We Will Retrieve AssignmentId If Available
		long assignmentId = ParamUtil.getLong(renderRequest, "assignmentId", 0);
		
		// Check The Purpose Editing Or Adding
		if(assignmentId > 0) {
			try {
				// Need To Call For Assignment Service To Get Assignment Information
				assignment = assignmentService.getAssignment(assignmentId);
			}
			catch (NoSuchAssignmentException nsae) {
				nsae.printStackTrace();
			}
			catch (PortalException pe) {
				pe.printStackTrace();
			}
		}

		// For Setting Up A Back Button
		ThemeDisplay themeDisplay = (ThemeDisplay) renderRequest.getAttribute(WebKeys.THEME_DISPLAY);
		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();
		
		String redirect = renderRequest.getParameter("redirect");
		
		portletDisplay.setShowBackIcon(true);
		portletDisplay.setURLBack(redirect);
		
		// Setting Request With Assignment Related Information
		renderRequest.setAttribute("assignment", assignment);
		renderRequest.setAttribute("assignmentClass", Assignment.class);
		
		return "/assignment/edit_assignment.jsp";
	}
	
	@Reference
	private AssignmentService assignmentService;
}
