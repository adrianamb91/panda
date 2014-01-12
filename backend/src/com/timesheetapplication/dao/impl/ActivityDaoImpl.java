package com.timesheetapplication.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.timesheetapplication.dao.ActivityDao;
import com.timesheetapplication.model.Activity;
import com.timesheetapplication.model.DailyTimeSheet;
import com.timesheetapplication.model.Project;

public class ActivityDaoImpl extends GenericDaoImpl<Activity> implements ActivityDao {

	public ActivityDaoImpl(EntityManager em) {
		super(em);
		this.em = em;
	}

	public Activity findByDateDurationDescAndProject(Date date, Float duration, String description, Project p) {

		Query q = em
				.createQuery("Select a from Activity a where a.timesheet.date = :date and a.duration = :duration and a.description = :description and a.project.id = :project_id");
		q.setParameter("date", date).setParameter("duration", duration).setParameter("description", description)
				.setParameter("project_id", p.getId());

		ArrayList<Activity> results = (ArrayList<Activity>) q.getResultList();

		if (results != null && results.size() > 0) {
			return results.get(0);
		}
		return null;
	}
	
	public void removeByDateDurationDescAndProject(Activity a) {
		em.getTransaction().begin();
		em.remove(a);
		em.getTransaction().commit();
	}

	public List<Activity> findActivitiesByDTS(DailyTimeSheet crtDTS) {
		Query q = em.createQuery("Select a from Activity a where a.timesheet.id = :id").setParameter("id", crtDTS.getId());
		return q.getResultList();
	}

}
