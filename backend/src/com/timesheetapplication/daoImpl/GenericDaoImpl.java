package com.timesheetapplication.daoImpl;

import java.util.Map;

import javax.persistence.Cache;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.metamodel.Metamodel;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.timesheetapplication.dao.GenericDao;
import com.timesheetapplication.model.AbstractEntity;

@Repository (value = "genericDao")
@Transactional
public class GenericDaoImpl<E extends AbstractEntity> implements GenericDao<E> {

	@PersistenceContext (unitName = "isipersistenceunit")
	EntityManagerFactory emf;

	@Override
	public void saveOrUpdate(AbstractEntity e) {
		EntityManager em = emf.createEntityManager();
		
		if (em == null) {
			System.out.println("empty em");
			return;
		}
		
		if (e.getId() == null) {
			em.persist(e);
		} else {
			em.merge(e);
		}
	}
}
