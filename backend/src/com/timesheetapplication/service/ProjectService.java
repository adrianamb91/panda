package com.timesheetapplication.service;

import java.util.List;

import com.timesheetapplication.dao.EntityManagerHolder;
import com.timesheetapplication.dao.ProjectDaoImpl;
import com.timesheetapplication.dao.impl.EmployeeDaoImpl;
import com.timesheetapplication.model.Employee;
import com.timesheetapplication.model.Project;

public class ProjectService {

	private ProjectDaoImpl projectDao = new ProjectDaoImpl(EntityManagerHolder
			.getInstance().getEntityManager());

	public List<Project> getProjectsForEmployee(Employee e) {
		return projectDao.findProjectsForEmployee(e);
	}

	public void saveOrUpdate(Project p) {
		projectDao.saveOrUpdate(p);
	}
	
	public Project findProjectByName(String name) {
		return projectDao.findProjectByName(name);
	}
}