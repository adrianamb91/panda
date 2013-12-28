package com.timesheetapplication.dao.impl;

import javax.persistence.EntityManager;

import com.timesheetapplication.dao.ActivityDao;
import com.timesheetapplication.model.Activity;

public class ActivityDaoImpl extends GenericDaoImpl<Activity> implements ActivityDao {

	public ActivityDaoImpl(EntityManager em) {
		super(em);
		this.em = em;
	}

}
