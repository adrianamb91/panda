package com.timesheetapplication.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.timesheetapplication.dao.DepartmentDao;
import com.timesheetapplication.model.Department;

public class DepartmentDaoImpl extends GenericDaoImpl<Department> implements
		DepartmentDao {

	public DepartmentDaoImpl(EntityManager em) {
		super(em);
		this.em = em;
	}

	public List<Department> loadAll() {
		Query q = em.createQuery("Select d from Department d order by d.name");
		return q.getResultList();
	}

	public Department findByName(String name) {
		Query q = em
				.createQuery("Select d from Department d where d.name = :name");
		q.setParameter("name", name);

		List<Department> results = q.getResultList();
		if (results != null && results.size() > 0) {
			return results.get(0);
		}
		return null;
	}

	public Department findByManager(Long id) {
		// TODO Auto-generated method stub
		Query q = em.createQuery("Select d from Department d where d.manager.id = :id");
		q.setParameter("id", id);
		
		List<Department> results = q.getResultList();
		if (results != null && results.size() > 0) {
			return results.get(0);
		}
		return null;
	}
}
