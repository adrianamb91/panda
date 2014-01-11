package com.timesheetapplication.service;

import java.util.Date;
import java.util.List;

import com.timesheetapplication.dao.EntityManagerHolder;
import com.timesheetapplication.dao.MonthlyTimeSheetDao;
import com.timesheetapplication.dao.impl.MonthlyTimeSheetDaoImpl;
import com.timesheetapplication.model.Employee;
import com.timesheetapplication.model.MonthlyTimesheet;

public class MonthlyTimeSheetService {

	MonthlyTimeSheetDaoImpl mtimesheetDao = new MonthlyTimeSheetDaoImpl(EntityManagerHolder.getInstance().getEntityManager());

	public void saveOrUpdate(MonthlyTimesheet m) {
		mtimesheetDao.saveOrUpdate(m);
	}

	public MonthlyTimesheet findMTSByDateAndUser(Date as, Employee e) {

		return mtimesheetDao.findByDateAndUser(as, e);
	
	}

	public List<MonthlyTimesheet> findAllMTSforUser(Employee e) {
		return mtimesheetDao.findByUser(e);
	}
	
}
