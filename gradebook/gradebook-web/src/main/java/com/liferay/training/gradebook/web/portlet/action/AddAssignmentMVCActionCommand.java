package com.liferay.training.gradebook.web.portlet.action;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.training.gradebook.exception.AssignmentValidationException;
import com.liferay.training.gradebook.model.Assignment;
import com.liferay.training.gradebook.service.AssignmentService;
import com.liferay.training.gradebook.web.constants.GradebookPortletKeys;
import com.liferay.training.gradebook.web.constants.MVCCommandNames;

import java.util.Date;
import java.util.Locale;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * 
 * @author hgrahul
 * This Class is Action Class For Creating a New Assignment
 */
@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + GradebookPortletKeys.PORTLET_NAME,
		"mvc.command.name=" + MVCCommandNames.ADD_ASSIGNMENT
	},
	service = MVCActionCommand.class
)
public class AddAssignmentMVCActionCommand extends BaseMVCActionCommand{
	@Override
	protected void doProcessAction(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {
		// Need To Get The Group or Site Id For Adding A New Assignment
		ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);
		long groupId = themeDisplay.getScopeGroupId();
		
		ServiceContext serviceContext = ServiceContextFactory.getInstance(Assignment.class.getName(), actionRequest);
		
		// Getting The Form Information
		Map<Locale, String> titleMap = LocalizationUtil.getLocalizationMap(actionRequest, "title");
		Map<Locale, String> descriptionMap = LocalizationUtil.getLocalizationMap(actionRequest, "description");
		Date dueDate = ParamUtil.getDate(actionRequest, "dueDate", DateFormatFactoryUtil.getDate(actionRequest.getLocale()));
		
		try {
			// Call For A Service To Add This New Assignment Information
			assignmentService.addAssignment(groupId, titleMap, descriptionMap, dueDate, serviceContext);
			
			// Redirection Happens
			sendRedirect(actionRequest, actionResponse);
		}
		catch (AssignmentValidationException ave) {
			ave.printStackTrace();
			actionResponse.setRenderParameter("mvcRenderCommandName", MVCCommandNames.EDIT_ASSIGNMENT);
		}
		catch(PortalException pe) {
			pe.printStackTrace();
			actionResponse.setRenderParameter("mvcRenderCommandName", MVCCommandNames.EDIT_ASSIGNMENT);
		}
	}
	
	@Reference
	private AssignmentService assignmentService;
}
