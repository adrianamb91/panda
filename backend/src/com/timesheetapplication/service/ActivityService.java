package com.timesheetapplication.service;

import com.timesheetapplication.dao.EntityManagerHolder;
import com.timesheetapplication.dao.impl.ActivityDaoImpl;
import com.timesheetapplication.model.Activity;

public class ActivityService {
	
	private ActivityDaoImpl activityDao = new ActivityDaoImpl(EntityManagerHolder.getInstance().getEntityManager());
	
	public void saveOrUpdate(Activity a) {
		activityDao.saveOrUpdate(a);
	}

}
