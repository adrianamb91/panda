package com.timesheetapplication.dao;

import java.util.List;

import com.timesheetapplication.model.Employee;

public interface EmployeeDao extends GenericDao<Employee> {
	
	public Employee findEmployeeByUsername(String username);
	
	@Override
	public List<Employee> loadAll();

}
