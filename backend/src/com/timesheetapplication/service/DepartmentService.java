package com.timesheetapplication.service;

import java.util.List;

import com.timesheetapplication.dao.EntityManagerHolder;
import com.timesheetapplication.dao.impl.DepartmentDaoImpl;
import com.timesheetapplication.model.Department;

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
}