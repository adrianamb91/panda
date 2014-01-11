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
	
	@Override
	public List<Employee> loadAll() {
		return this.em.createQuery("Select e from Employee e").getResultList();
	}

	public Employee findEmployeeByFirstAndLastName(String name) {
		// TODO Auto-generated method stub
		Query query = em.createQuery("select e from Employee e where e.firstName||' '||e.lastName = :name");
		query.setParameter("name", name);
		
		List<Employee> results = query.getResultList();
		if (results != null && results.size() > 0) {
			return results.get(0);
		}
		return null;
	}

}
