package org.mardep.ssrs.dao;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.mardep.ssrs.domain.AbstractPersistentEntity;
import org.mardep.ssrs.domain.sr.Transaction;

public interface IBaseDao<T extends AbstractPersistentEntity<K>, K extends Serializable> {

	public T findById(K id);
	public T save(T entity);

	public int delete(Class<T> clazz, K id);
	public int delete(T AbstractPersistentEntity);
	public List<T> findAll();

	public List<T> findByCriteria(T entity);
	public Long findByPaging(Map<String, Criteria> map, final SortByCriteria sortByCriteria, PagingCriteria pagingCriteria, List<T> resultList);
	public Long findByPaging(Map<String, Criteria> map, final SortByCriteria sortByCriteria, PagingCriteria pagingCriteria, List<T> resultList, boolean checkCount);
	public Long count(Map<String, Criteria> map);

	public T findLatestBySeafarerId(String seafarerId);
	/**
	 *
	 * @param applNo
	 * @param txDate
	 */
	void saveHistory(String applNo, Transaction tx);
	List<T> findHistory(String applNo, Date txDate);
	public void deleteHistory(String applNo, Transaction tx);
}
