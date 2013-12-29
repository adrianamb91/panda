package com.timesheetapplication.dao;

import java.util.Date;

import com.timesheetapplication.model.Employee;
import com.timesheetapplication.model.MonthlyTimesheet;

public interface MonthlyTimeSheetDao extends GenericDao<MonthlyTimesheet>{

	public MonthlyTimesheet findByDateAndUser(Date as, Employee e);

}
