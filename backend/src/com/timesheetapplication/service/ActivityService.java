package com.timesheetapplication.service;

import java.util.Date;
import java.util.List;

import com.timesheetapplication.dao.EntityManagerHolder;
import com.timesheetapplication.dao.impl.ActivityDaoImpl;
import com.timesheetapplication.dao.impl.DailyTimeSheetDaoImpl;
import com.timesheetapplication.model.Activity;
import com.timesheetapplication.model.DailyTimeSheet;
import com.timesheetapplication.model.Project;

public class ActivityService {

	private ActivityDaoImpl activityDao = new ActivityDaoImpl(EntityManagerHolder.getInstance().getEntityManager());

	private DailyTimeSheetDaoImpl dailyDao = new DailyTimeSheetDaoImpl(EntityManagerHolder.getInstance().getEntityManager());

	public void saveOrUpdate(Activity a) {
		activityDao.saveOrUpdate(a);
	}

	public Activity findActivityByDateDurationDescAndProject(Date date, Float duration, String description, Project p) {
		return activityDao.findByDateDurationDescAndProject(date, duration, description, p);
	}

	/*
	 * When you simply remove an Activity, it gets deleted from the activity
	 * table but it the dailyTimesheet object associated with it still has a
	 * reference to it.
	 * 
	 * Hence, you are forced to manually cascade the delete action, by deleting
	 * from Object's arraylist
	 */
	public void deleteActivityByDateDurationDescAndProject(Date d, Float duration, String description, Project p) {
		Activity a = activityDao.findByDateDurationDescAndProject(d, duration, description, p);
		DailyTimeSheet dts = a.getTimesheet();
		dts.getActivities().remove(a);
		dailyDao.saveOrUpdate(dts);

		activityDao.removeByDateDurationDescAndProject(a);
	}

	public List<Activity> findActivitiesByDTS(DailyTimeSheet crtDTS) {
		return activityDao.findActivitiesByDTS(crtDTS);
	}

}
