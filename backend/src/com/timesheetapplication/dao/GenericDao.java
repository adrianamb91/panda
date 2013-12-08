package com.timesheetapplication.dao;

import com.timesheetapplication.model.AbstractEntity;

/**
 * 
 * @author Alexandru Dinca (alexandru.dinca2110@gmail.com)
 */
public interface GenericDao<E extends AbstractEntity> {

	public void saveOrUpdate(AbstractEntity e);

}
