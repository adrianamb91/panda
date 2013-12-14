package com.timesheetapplication.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.timesheetapplication.dao.EmployeeDao;
import com.timesheetapplication.model.Employee;

/**
 * 
 * @author alexandru.dinca2110@gmail.com
 *
 */
public class EmployeeDaoImpl extends GenericDaoImpl<Employee> implements
		EmployeeDao {

	public EmployeeDaoImpl(EntityManager em) {
		super(em);
		this.em = em;
	}
	
	@Override
	public Employee findEmployeeByUsername(String username) {
		Query query = em.createQuery("select e from Employee e where e.username = :username");
		query.setParameter("username", username);
		
		if (query.getResultList() != null && query.getResultList().size() > 0) {
			return (Employee) query.getResultList().get(0);
		}
		return null;
	}

}
