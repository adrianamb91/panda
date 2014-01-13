package com.timesheetapplication.service;

import java.util.List;

import com.timesheetapplication.dao.EntityManagerHolder;
import com.timesheetapplication.dao.impl.DivisionDaoImpl;
import com.timesheetapplication.model.Division;
import com.timesheetapplication.model.Employee;
import com.timesheetapplication.utils.TSMUtil;

public class DivisionService {

	private DivisionDaoImpl divisionDao = new DivisionDaoImpl(EntityManagerHolder.getInstance().getEntityManager());
	
	private DepartmentService departmentService = new DepartmentService();

	public void saveOrUpdate(Division d) {
		divisionDao.saveOrUpdate(d);
	}

	public List<Division> loadAll() {
		return divisionDao.loadAll();
	}

	public Division findDivisionByName(String name) {
		return divisionDao.findByName(name);
	}

	public void remove(String name) {
		Division d = findDivisionByName(name);
		if (d != null) {
			
			
			//departmentService.removeDepartmentsFromDivision(d);
			// TODO : DELETE ALL DEPARTMENTS AND EMPLOYEES FROM DIVISION D
			divisionDao.remove(d);
		}
	}

	public Boolean replaceDivisionName(String oldname, String newname) {
		if (TSMUtil.isNotEmptyOrNull(oldname) && TSMUtil.isNotEmptyOrNull(newname)) {
			Division d = divisionDao.findByName(oldname);
			if (d == null) {
				return false;
			}
			d.setName(newname);
			divisionDao.saveOrUpdate(d);
			return true;
		}
		return false;
	}

	public Division findDivisionByManager(Employee loggedInUser) {
		// TODO Auto-generated method stub
		return divisionDao.findByManager(loggedInUser);
	}

}
