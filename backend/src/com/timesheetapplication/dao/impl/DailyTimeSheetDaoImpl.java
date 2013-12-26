package com.timesheetapplication.dao.impl;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.timesheetapplication.dao.DailyTimeSheetDao;
import com.timesheetapplication.model.DailyTimeSheet;
import com.timesheetapplication.model.Employee;

public class DailyTimeSheetDaoImpl extends GenericDaoImpl<DailyTimeSheet>
		implements DailyTimeSheetDao {

	public DailyTimeSheetDaoImpl(EntityManager em) {
		super(em);
		this.em = em;
	}

	@Override
	public DailyTimeSheet getDailyTimeSheetByDate(Date d) {
		System.out.println("BEWARE, THIS IS THE DUMMY METHOD!");

		return null;
	}

	@Override
	public DailyTimeSheet getDailyTimeSheetByDateAndUser(Date d, Employee u) {
		if (d == null || u == null || u.getId() == null) {
			return null;
		}

		Query q = em
				.createQuery("Select d from DailyTimeSheet d where d.date = :date and d.owner.id = :id");
		q.setParameter("date", d);
		q.setParameter("id", u.getId());

		if (q.getResultList().size() > 0) {
			return (DailyTimeSheet) q.getResultList().get(0);
		}
		return null;
	}
}
