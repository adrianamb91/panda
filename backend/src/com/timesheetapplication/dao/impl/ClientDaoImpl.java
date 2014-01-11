package com.timesheetapplication.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.timesheetapplication.dao.ClientDao;
import com.timesheetapplication.model.Client;

public class ClientDaoImpl extends GenericDaoImpl<Client> implements ClientDao {

	public ClientDaoImpl(EntityManager em) {
		super(em);
		this.em = em;
	}
	
	public Client findByName(String name) {
		Query q = em.createQuery("Select c from Client c where c.name = :name");
		q.setParameter("name", name);
		
		List<Client> results = q.getResultList();
		if (results != null && results.size() > 1) {
			return results.get(0);
		}
		return null;
	}

}
