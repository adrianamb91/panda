package com.timesheetapplication.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.timesheetapplication.dao.ProjectDao;
import com.timesheetapplication.model.Employee;
import com.timesheetapplication.model.Project;

public class ProjectDaoImpl extends GenericDaoImpl<Project> implements
		ProjectDao {

	public ProjectDaoImpl(EntityManager em) {
		super(em);
		this.em = em;
	}

	/*
	 * returns all projects
	 */
	@Override
	public List<Project> loadAll() {
		return this.em.createQuery("Select p from Project p").getResultList();
	}

	/*
	 * returns all the projects an Employee is working on.
	 */
	@Override
	public List<Project> findProjectsForEmployee(Employee e) {
		if (e == null || e.getId() == null) {
			return null;
		}
		String query = "Select p from Project p join p.employees e where e.id = "
				+ e.getId();
		return em.createQuery(query).getResultList();
	}

	@Override
	public Project findProjectByName(String name) {
		if (name == null || name.length() == 0) {
			return null;
		}

		Query q = em
				.createQuery("Select p from Project p where p.name = :name");
		q.setParameter("name", name);
		List<Project> resultList = (List<Project>) q.getResultList();

		if (resultList != null && resultList.size() > 0) {
			return resultList.get(0);
		}
		return null;
	}

}
