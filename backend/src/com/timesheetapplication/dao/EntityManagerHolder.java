package com.timesheetapplication.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class EntityManagerHolder {

	private static EntityManagerHolder INSTANCE = new EntityManagerHolder();
	private EntityManagerFactory emf;
	private EntityManager entityManager;

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public static EntityManagerHolder getInstance() {
		return INSTANCE;
	}

	private EntityManagerHolder() {
		super();
		emf = Persistence
				.createEntityManagerFactory("tsm_persistence");
		entityManager = emf.createEntityManager();
	}

}