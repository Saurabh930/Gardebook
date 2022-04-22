package com.liferay.training.gradebook.web.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.BaseManagementToolbarDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.ViewTypeItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.ViewTypeItemList;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayActionResponse;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.training.gradebook.web.constants.GradebookPortletKeys;
import com.liferay.training.gradebook.web.constants.MVCCommandNames;

import java.util.List;

import javax.portlet.PortletException;
import javax.portlet.PortletURL;
import javax.servlet.http.HttpServletRequest;

/**
 * 
 * @author hgrahul
 * This Class Passes Contextual Information To The User Interface To The Management Toolbar
 * 1. How Add Works
 * 2. Search Function
 * 3. Clear All Result
 * 4. Displaying Filtering/Ordering Condition
 * 5. Display Style
 */
public class AssignmentsManagementToolbarDisplayContext extends BaseManagementToolbarDisplayContext{
	
	public AssignmentsManagementToolbarDisplayContext(LiferayPortletRequest liferayPortletRequest, LiferayPortletResponse liferayPortletResponse, HttpServletRequest httpServletRequest) {
		super(httpServletRequest, liferayPortletRequest, liferayPortletResponse);
		portalPreferences = PortletPreferencesFactoryUtil.getPortalPreferences(liferayPortletRequest);
		themeDisplay = (ThemeDisplay) httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY);
	}
	
	/**
	 * Return The Creation Menu For Gradebook Assignment
	 */
	@Override
	public CreationMenu getCreationMenu() {
		// Create A Custom Rendered Creation Menu
		return new CreationMenu() {
			{
				addDropdownItem(
					dropdownItem -> {
						dropdownItem.setHref(liferayPortletResponse.createRenderURL(), "mvcRenderCommandName", MVCCommandNames.EDIT_ASSIGNMENT, "redirect", currentURLObj.toString());
						dropdownItem.setLabel(LanguageUtil.get(request, "add-assignment"));
					}
				);
			}
		};
	}
	
	/**
	 * For Clearing The Result Show Last Result Instead Of An Empty One
	 */
	@Override
	public String getClearResultsURL() {
		return getSearchActionURL();
	}
	
	/**
	 * Return The Assignment List Display Style
	 * Current Selection Is Stored In Portal Preferences
	 */
	@Override
	public String getDisplayStyle() {
		// Get The Display Style
		String displayStyle = ParamUtil.getString(request, "displayStyle");
		
		if(Validator.isNull(displayStyle)) {
			displayStyle = portalPreferences.getValue(GradebookPortletKeys.PORTLET_NAME, "assignment-display-style", "descriptive");
		}
		else {
			portalPreferences.setValue(GradebookPortletKeys.PORTLET_NAME, "assignment-display-style", displayStyle);
		}
		
		return displayStyle;
	}
	
	/**
	 * Return The Sort Order Column
	 */
	@Override
	protected String getOrderByCol() {
		return ParamUtil.getString(request, "orderByCol","title");
	}
	
	/**
	 * Return Sort Order Type
	 */
	@Override
	protected String getOrderByType() {
		return ParamUtil.getString(request, "orderByType","asc");
	}
	
	/**
	 * Here It Returns Action URL For The Search
	 */
	@Override
	public String getSearchActionURL() {
		// We Have Created An Empty Render URL
		PortletURL searchURL = liferayPortletResponse.createRenderURL();
		
		// Setting Up The Parameter For The Created URL
		// 1. Whete Or What To Render
		// 2. Navigations
		// 3. Ordering Stuff
		searchURL.setProperty("mvcRenderCommandName", MVCCommandNames.VIEW_ASSIGNMENTS);
		
		String navigation = ParamUtil.getString(request, "entries");
		searchURL.setParameter("navigation", navigation);
		searchURL.setParameter("orderByCol", getOrderByCol());
		searchURL.setParameter("orderByType", getOrderByType());
		
		return searchURL.toString();
	}
	
	/**
	 * Return The View Style(Card|Icon|Table)
	 */
	@Override
	public List<ViewTypeItem> getViewTypeItems() {
		// Create a Another Portlet URL For Rendering Appropriate Views
		PortletURL portletURL = liferayPortletResponse.createRenderURL();
		
		portletURL.setParameter("mvcRenderCommandName", MVCCommandNames.VIEW_ASSIGNMENTS);
		
		int delta = ParamUtil.getInteger(request, SearchContainer.DEFAULT_DELTA_PARAM);
		
		if(delta > 0) {
			portletURL.setParameter("delta", String.valueOf(delta));
		}
		
		portletURL.setParameter("orderByCol", getOrderByCol());
		portletURL.setParameter("orderByType", getOrderByType());
		
		int cur = ParamUtil.getInteger(request, SearchContainer.DEFAULT_CUR_PARAM);
		
		if(cur > 0) {
			portletURL.setParameter("cur", String.valueOf(cur));
		}
		
		return new ViewTypeItemList(portletURL, getDisplayStyle()) {
			{
				addCardViewTypeItem();
				addListViewTypeItem();
				addTableViewTypeItem();
			}
		};
	}
	
	/**
	 * Return The Options For Sorting/Filtering Column Menu
	 */
	@Override
	protected List<DropdownItem> getOrderByDropdownItems() {
		return new DropdownItemList() {
			{
				add(
					dropdownItem -> {
						dropdownItem.setActive("title".equals(getOrderByCol()));
						dropdownItem.setHref(_getCurrentSortingURL(), "orderByCol", "title");
						dropdownItem.setLabel(LanguageUtil.get(request, "title"));
					}
				);
				add(
					dropdownItem -> {
						dropdownItem.setActive("createDate".equals(getOrderByCol()));
						dropdownItem.setHref(_getCurrentSortingURL(), "orderByCol", "createDate");
						dropdownItem.setLabel(LanguageUtil.get(request, "create-date"));
					}
				);
			}
		};
	}
	
	/**
	 * Return Current Sorting URL
	 */
	private PortletURL _getCurrentSortingURL() throws PortletException {
		PortletURL sortingURL = PortletURLUtil.clone(currentURLObj, liferayPortletResponse);
		
		sortingURL.setParameter("mvcRenderCommandName", MVCCommandNames.VIEW_ASSIGNMENTS);
		
		// Reset Your Existing Page
		sortingURL.setParameter(SearchContainer.DEFAULT_CUR_PARAM, "0");
		
		String keywords = ParamUtil.getString(request, "keywords");

		if (Validator.isNotNull(keywords)) {
			sortingURL.setParameter("keywords", keywords);
		}
		
		return sortingURL;
	}
	
	private final PortalPreferences portalPreferences;
	private final ThemeDisplay themeDisplay;
}
