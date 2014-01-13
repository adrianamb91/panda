package com.timesheetapplication.service;

import java.util.List;

import com.timesheetapplication.dao.EntityManagerHolder;
import com.timesheetapplication.dao.impl.DepartmentDaoImpl;
import com.timesheetapplication.model.Department;
import com.timesheetapplication.model.Division;
import com.timesheetapplication.model.Employee;

public class DepartmentService {

	private DepartmentDaoImpl departmentDao = new DepartmentDaoImpl(
			EntityManagerHolder.getInstance().getEntityManager());

	public void saveOrUpdate(Department d) {
		departmentDao.saveOrUpdate(d);
	}
	
	public List<Department> loadAll() {
		return departmentDao.loadAll();
	}
	
	public Department findDepartmentByName(String name) {
		return departmentDao.findByName(name);
	}
	
	public void remove(String name) {
		Department d = findDepartmentByName(name);
		if (d != null) {
			departmentDao.remove(d);
		}
	}
	
	public Department findDepartmentByManager(Employee e) {
		return departmentDao.findByManager(e.getId());
	}

	public List<Department> loadAllFromDivision(Division d) {
		// TODO Auto-generated method stub
		return departmentDao.loadAllFromDivision(d);
	}

}
