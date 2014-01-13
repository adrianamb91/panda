package com.timesheetapplication.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.timesheetapplication.dao.DivisionDao;
import com.timesheetapplication.model.Activity;
import com.timesheetapplication.model.Division;
import com.timesheetapplication.model.Employee;

public class DivisionDaoImpl extends GenericDaoImpl<Division> implements
		DivisionDao {

	public DivisionDaoImpl(EntityManager em) {
		super(em);
		this.em = em;
	}
	
	public List<Division> loadAll() {
		return this.em.createQuery("Select d from Division d order by d.name").getResultList();
	}

	public Division findByName(String name) {
		Query q = em.createQuery("Select d from Division d where d.name = :name");
		q.setParameter("name", name);
		
		List<Division> results = q.getResultList();
		
		if (results != null && results.size() > 0) {
			return results.get(0);
		}
		return null;
	}

	public Division findByManager(Employee loggedInUser) {
		Query q = em.createQuery("Select d from Division d where d.manager.id = :id");
		q.setParameter("id", loggedInUser.getId());
		
		List<Division> results = q.getResultList();
		if (results != null && results.size() > 0) {
			return results.get(0);
		}
		return null;
	}

}
