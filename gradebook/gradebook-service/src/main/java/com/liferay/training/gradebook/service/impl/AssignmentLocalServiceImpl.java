/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.training.gradebook.service.impl;

import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.Disjunction;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.training.gradebook.model.Assignment;
import com.liferay.training.gradebook.service.base.AssignmentLocalServiceBaseImpl;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * The implementation of the assignment local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the <code>com.liferay.training.gradebook.service.AssignmentLocalService</code> interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security checks based on the propagated JAAS credentials because this service can only be accessed from within the same VM.
 * </p>
 *
 * @author hgrahul
 * @see AssignmentLocalServiceBaseImpl
 */
@Component(
	property = "model.class.name=com.liferay.training.gradebook.model.Assignment",
	service = AopService.class
)
public class AssignmentLocalServiceImpl extends AssignmentLocalServiceBaseImpl {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never reference this class directly. Use <code>com.liferay.training.gradebook.service.AssignmentLocalService</code> via injection or a <code>org.osgi.util.tracker.ServiceTracker</code> or use <code>com.liferay.training.gradebook.service.AssignmentLocalServiceUtil</code>.
	 */
	public Assignment addAssignment(long groupId, Map<Locale, String> titleMap, Map<Locale, String> descriptionMap, Date dueDate, ServiceContext serviceContext) throws PortalException {
		// Get Site and User Information
		Group group = groupLocalService.getGroup(groupId);
		long userId = serviceContext.getUserId();
		User user = userLocalService.getUser(userId);
		
		// Need To Generate A New Primary For An Assignment
		long assignmentId = counterLocalService.increment(Assignment.class.getName());
		
		// Using The Generated Primary Key. We Will Create A New Assignment
		Assignment assignment = createAssignment(assignmentId);
		
		// Populate The Assignment Information To The Newly Created Instance
		assignment.setCompanyId(group.getCompanyId());
		assignment.setCreateDate(serviceContext.getCreateDate(new Date()));
		assignment.setDueDate(dueDate);
		assignment.setDescriptionMap(descriptionMap);
		assignment.setGroupId(groupId);
		assignment.setModifiedDate(serviceContext.getModifiedDate(new Date()));
		assignment.setTitleMap(titleMap);
		assignment.setUserId(userId);
		assignment.setUserName(user.getScreenName());
		
		// Persisting Updated Assignment Instance To Database
		assignment = super.addAssignment(assignment);
		
		// Now Return It.
		return assignment;
	}
	
	public Assignment updateAssignment(long assignmentId, Map<Locale, String> titleMap, Map<Locale, String> descriptionMap, Date dueDate, ServiceContext serviceContext) throws PortalException {
		// Get The Assignment Instance From The Assignment Id
		Assignment assignment = getAssignment(assignmentId);
		
		// Set Updated Field and Modified Date
		assignment.setModifiedDate(new Date());
		assignment.setTitleMap(titleMap);
		assignment.setDescriptionMap(descriptionMap);
		assignment.setDueDate(dueDate);
		
		// Persisting Updated Assignment Instance To Database
		assignment = super.updateAssignment(assignment);
		
		// Return It.
		return assignment;
	}
	
	public List<Assignment> getAssignmentsByGroupId(long groupId) {
		return assignmentPersistence.findByGroupId(groupId);
	}
	
	public List<Assignment> getAssignmentsByGroupId(long groupId, int start, int end) {
		return assignmentPersistence.findByGroupId(groupId, start, end);
	}
	
	public List<Assignment> getAssignmentsByGroupId(long groupId, int start, int end, OrderByComparator<Assignment> orderByComparator) {
		return assignmentPersistence.findByGroupId(groupId, start, end, orderByComparator);
	}
	
	private DynamicQuery getKeywordSearchDynamicQuery(long groupId, String keywords) {
		DynamicQuery dynamicQuery = dynamicQuery().add(RestrictionsFactoryUtil.eq("groupId", groupId));
		
		if(Validator.isNotNull(keywords)) {
			Disjunction disjunction = RestrictionsFactoryUtil.disjunction();
			
			disjunction.add(RestrictionsFactoryUtil.like("title", "%" + keywords + "%"));
			disjunction.add(RestrictionsFactoryUtil.like("description", "%" + keywords+ "%"));
			
			dynamicQuery.add(disjunction);
		}
		
		return dynamicQuery;
	}
	
	public List<Assignment> getAssignmentsByKeywords(long groupId, String keywords, int start, int end, OrderByComparator<Assignment> orderByComparator) {
		return assignmentLocalService.dynamicQuery(getKeywordSearchDynamicQuery(groupId, keywords), start, end, orderByComparator);
	}
	
	public long getAssignmentsCountByKeywords(long groupId, String keywords) {
		return assignmentLocalService.dynamicQueryCount(getKeywordSearchDynamicQuery(groupId, keywords));
	}
	
	@Override
	public Assignment addAssignment(Assignment assignment) {
		throw new UnsupportedOperationException("Not Supported");
	}
	
	@Override
	public Assignment updateAssignment(Assignment assignment) {
		throw new UnsupportedOperationException("Not Supported");
	}
}