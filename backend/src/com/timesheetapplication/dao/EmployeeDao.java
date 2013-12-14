package com.timesheetapplication.dao;

import com.timesheetapplication.model.Employee;

public interface EmployeeDao extends GenericDao<Employee> {
	
	public Employee findEmployeeByUsername(String username);

}
