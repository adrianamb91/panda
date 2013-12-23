package com.timesheetapplication.dao.impl;

import java.util.List;

import com.timesheetapplication.dao.GenericDao;
import com.timesheetapplication.model.AbstractEntity;
import com.timesheetapplication.model.Employee;
import com.timesheetapplication.model.Project;

public interface ProjectDao extends GenericDao<Project> {

	@Override
	public List<Project> loadAll();

	public List<Project> findProjectsForEmployee(Employee e);
	
	public Project findProjectByName(String name);

}
