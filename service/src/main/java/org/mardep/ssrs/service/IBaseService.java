package org.mardep.ssrs.service;

import java.io.Serializable;
import java.util.List;

import java.util.Map;

import org.mardep.ssrs.dao.Criteria;
import org.mardep.ssrs.dao.PagingCriteria;
import org.mardep.ssrs.dao.SortByCriteria;
import org.mardep.ssrs.domain.AbstractPersistentEntity;

/**
 * 
 * @author Leo.LIANG
 *
 */
public interface IBaseService {

	public <T> T save(AbstractPersistentEntity entity);
	public <T> T findById(Class<T> requiredType, Serializable id);
	public <T> List<T> findByCriteria(AbstractPersistentEntity entity);
//	public int delete(AbstractPersistentEntity entity);
	public int delete(Class clazz, Serializable id);
	public int delete(AbstractPersistentEntity entity);
	public <T> Long count(Map<String, Criteria> map, Class<T> requiredType);
	public <T> Long findByPaging(Map<String, Criteria> map, List<T> resultList, Class<T> requiredType);
	public <T> Long findByPaging(Map<String, Criteria> map, SortByCriteria sortByCriteria, List<T> resultList, Class<T> requiredType);
	public <T> Long findByPaging(Map<String, Criteria> map, PagingCriteria pagingCriteria, List<T> resultList, Class<T> requiredType);
	public <T> Long findByPaging(Map<String, Criteria> map, SortByCriteria sortByCriteria, PagingCriteria pagingCriteria, List<T> resultList, Class<T> requiredType);
	
}
