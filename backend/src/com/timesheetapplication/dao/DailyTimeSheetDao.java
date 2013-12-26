package com.timesheetapplication.dao;

import java.util.Date;

import com.timesheetapplication.model.DailyTimeSheet;
import com.timesheetapplication.model.Employee;

public interface DailyTimeSheetDao extends GenericDao<DailyTimeSheet>{

	public DailyTimeSheet getDailyTimeSheetByDate(Date d);
	
	public DailyTimeSheet getDailyTimeSheetByDateAndUser(Date d, Employee u);
	
}
