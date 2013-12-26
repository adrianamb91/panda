package com.timesheetapplication.service;

import java.util.Date;

import com.timesheetapplication.dao.EntityManagerHolder;
import com.timesheetapplication.dao.impl.DailyTimeSheetDaoImpl;
import com.timesheetapplication.model.DailyTimeSheet;
import com.timesheetapplication.model.Employee;

public class DailyTimesheetService {
	
	private DailyTimeSheetDaoImpl dtimesheetDao = new DailyTimeSheetDaoImpl(EntityManagerHolder.getInstance().getEntityManager());
	
	public void saveOrUpdateEvent(DailyTimeSheet t) {
		dtimesheetDao.saveOrUpdate(t);
	}
	
	public DailyTimeSheet findDTSbyDateAndUser(Date d, Employee e) {
		return dtimesheetDao.getDailyTimeSheetByDateAndUser(d, e);
	}

}
