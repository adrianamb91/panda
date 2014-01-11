package com.timesheetapplication.service;

import com.timesheetapplication.dao.EntityManagerHolder;
import com.timesheetapplication.dao.impl.ClientDaoImpl;
import com.timesheetapplication.model.Client;

public class ClientService {

	private ClientDaoImpl clientDao = new ClientDaoImpl(EntityManagerHolder.getInstance().getEntityManager());
	
	public Client findClientByName(String name) {
		return clientDao.findByName(name);
	}
	
	public void saveOrUpdate(Client c) {
		clientDao.saveOrUpdate(c);
	}
	
}