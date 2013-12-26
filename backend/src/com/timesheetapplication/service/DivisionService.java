package com.timesheetapplication.service;

import java.util.List;

import com.timesheetapplication.dao.EntityManagerHolder;
import com.timesheetapplication.dao.impl.DivisionDaoImpl;
import com.timesheetapplication.model.Division;

public class DivisionService {
	
	private DivisionDaoImpl divisionDao = new DivisionDaoImpl(EntityManagerHolder.getInstance().getEntityManager());
	
	public void saveOrUpdate(Division d) {
		divisionDao.saveOrUpdate(d);
	}

	public List<Division> loadAll() {
		return divisionDao.loadAll();
	}
	
	public Division findDivisionByName(String name) {
		return divisionDao.findByName(name);
	}
	
}
