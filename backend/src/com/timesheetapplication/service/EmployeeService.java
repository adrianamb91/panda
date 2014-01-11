package com.timesheetapplication.service;

import java.util.List;

import com.timesheetapplication.dao.EntityManagerHolder;
import com.timesheetapplication.dao.impl.EmployeeDaoImpl;
import com.timesheetapplication.enums.Job;
import com.timesheetapplication.model.Employee;

public class EmployeeService {

	private EmployeeDaoImpl employeeDao = new EmployeeDaoImpl(
			EntityManagerHolder.getInstance().getEntityManager());

	/**
	 * @return : 1 -- normal user 2 -- admin -1 -- user not found -2 -- wrong
	 *         password
	 */
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
		return -2;
	}

	public Employee findEmployeeByUsername(String username) {
		return employeeDao.findEmployeeByUsername(username);
	}

	public void saveOrUpdate(Employee e) {
		employeeDao.saveOrUpdate(e);
	}

	public List<Employee> loadAllEmployees() {
		return employeeDao.loadAll();
	}
	
	public Employee findEmployeeByFirstAndLastName(String name) {
		return employeeDao.findEmployeeByFirstAndLastName(name);
	}
	
	public void removeEmployeeByUsername(String username) {
		Employee e = findEmployeeByUsername(username);
		if (e != null) {
			employeeDao.remove(e);
		}
	}
}
