package com.timesheetapplication.dao.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import javax.persistence.Query;

import com.timesheetapplication.dao.MonthlyTimeSheetDao;
import com.timesheetapplication.model.Employee;
import com.timesheetapplication.model.MonthlyTimesheet;
import com.timesheetapplication.utils.TSMUtil;

public class MonthlyTimeSheetDaoImpl extends GenericDaoImpl<MonthlyTimesheet> implements MonthlyTimeSheetDao {

	public MonthlyTimeSheetDaoImpl(EntityManager em) {
		super(em);
		this.em = em;
	}

	@Override
	public MonthlyTimesheet findByDateAndUser(Date as, Employee e) {
		Query q = em.createQuery("Select m from MonthlyTimesheet m where m.date = :date and m.owner.id = :id");
		q.setParameter("date", as);
		q.setParameter("id", e.getId());

		List<MonthlyTimesheet> results = q.getResultList();
		if (results != null && results.size() > 0) {
			return results.get(0);
		}
		return null;
	}

}
