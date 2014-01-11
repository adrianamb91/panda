package com.timesheetapplication.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import com.timesheetapplication.dao.GenericDao;
import com.timesheetapplication.model.AbstractEntity;
import com.timesheetapplication.model.Activity;

@Transactional
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
		if (!em.getTransaction().isActive()) {
			em.getTransaction().begin();
		}
		if (e.getId() == null) {
			em.persist(e);
		} else {
			em.merge(e);
		}
		em.getTransaction().commit();

	}

	@Override
	public List<E> loadAll() {
		System.err.println("GenericDaoImp.loadAll() shouldn't have been called!!!");
		return null;
	}

	public void remove(AbstractEntity e) {
		em.getTransaction().begin();
		em.remove(e);
		em.getTransaction().commit();
	}

}
