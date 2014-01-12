package com.timesheetapplication.service;

import java.util.Date;
import java.util.List;

import com.timesheetapplication.dao.EntityManagerHolder;
import com.timesheetapplication.dao.impl.DailyTimeSheetDaoImpl;
import com.timesheetapplication.model.DailyTimeSheet;
import com.timesheetapplication.model.Employee;
import com.timesheetapplication.model.MonthlyTimesheet;

public class DailyTimesheetService {
	
	private DailyTimeSheetDaoImpl dtimesheetDao = new DailyTimeSheetDaoImpl(EntityManagerHolder.getInstance().getEntityManager());
	
	public void saveOrUpdateEvent(DailyTimeSheet t) {
		dtimesheetDao.saveOrUpdate(t);
	}
	
	public DailyTimeSheet findDTSbyDateAndUser(Date d, Employee e) {
		return dtimesheetDao.getDailyTimeSheetByDateAndUser(d, e);
	}

	public List<DailyTimeSheet> findAllDTFromMTS(MonthlyTimesheet mts) {
		return dtimesheetDao.findDTSbyMTS(mts);
	}

	public List<DailyTimeSheet> loadDTSinInterval(Date from, Date to) {
		return dtimesheetDao.loadFromInterval(from, to);
	}

}
