package com.timesheetapplication.service;

import com.timesheetapplication.dao.EntityManagerHolder;
import com.timesheetapplication.dao.impl.EmployeeDaoImpl;
import com.timesheetapplication.enums.Job;
import com.timesheetapplication.model.Employee;

public class EmployeeService {

	private EmployeeDaoImpl employeeDao = new EmployeeDaoImpl(
			EntityManagerHolder.getInstance().getEntityManager());

	public Integer checkUsernameForAccess(String username, String password) {
		Employee emp = employeeDao.findEmployeeByUsername(username);
		if (emp == null) {
			return -1;
		}
		if (emp.getPassword().equals(password)) {
			if (emp.getJob().equals(Job.ADMIN.name())) {
				return 2;
			}
			return 1;
		}
		return -1;
	}
	
	public Employee findEmployeeByUsername(String username) {
		return employeeDao.findEmployeeByUsername(username);
	}
	
	public void saveOrUpdate(Employee e) {
		employeeDao.saveOrUpdate(e);
	}

}
