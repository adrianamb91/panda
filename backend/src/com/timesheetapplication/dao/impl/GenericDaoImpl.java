package com.timesheetapplication.dao.impl;

import javax.persistence.EntityManager;

import com.timesheetapplication.dao.GenericDao;
import com.timesheetapplication.model.AbstractEntity;

public class GenericDaoImpl<E extends AbstractEntity> implements GenericDao<E> {

	protected EntityManager em;

	public GenericDaoImpl(EntityManager em) {
		this.em = em;
	}

	@Override
	public void saveOrUpdate(AbstractEntity e) {
		if (e == null) {
			throw new NullPointerException("Trying to persist a null object");
		}
		em.getTransaction().begin();
		if (e.getId() == null) {
			em.persist(e);
		} else {
			em.merge(e);
		}
		em.getTransaction().commit();
	}

}
