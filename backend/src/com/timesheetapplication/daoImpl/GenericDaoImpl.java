package com.timesheetapplication.daoImpl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.timesheetapplication.dao.GenericDao;
import com.timesheetapplication.model.AbstractEntity;

@Repository (value = "genericDao")
@Transactional
public class GenericDaoImpl<E extends AbstractEntity> implements GenericDao<E> {

	@PersistenceContext
	private EntityManager em;

	@Override
	public void saveOrUpdate(AbstractEntity e) {
		if (e.getId() == null) {
			em.persist(e);
		} else {
			em.merge(e);
		}
	}
}
