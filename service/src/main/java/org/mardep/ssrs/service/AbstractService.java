package org.mardep.ssrs.service;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.mardep.ssrs.dao.Criteria;
import org.mardep.ssrs.dao.IBaseDao;
import org.mardep.ssrs.dao.PagingCriteria;
import org.mardep.ssrs.dao.SortByCriteria;
import org.mardep.ssrs.domain.AbstractPersistentEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author Leo.LIANG
 *
 */
public abstract class AbstractService implements IBaseService {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@SuppressWarnings("rawtypes")
	protected Map<Class, IBaseDao> classDaoMap = new HashMap<Class, IBaseDao>();

	@Autowired
	ApplicationContext appContext;

	@SuppressWarnings("rawtypes")
	@PostConstruct
	public void init() {
		Map<String, IBaseDao> map = appContext.getBeansOfType(IBaseDao.class);
		for (IBaseDao baseDao : map.values()) {
			classDaoMap.put(getEntityClass(baseDao.getClass()), baseDao);
		}
	}

	/**
	 *
	 *
	 * @param requiredType
	 * @param entity
	 * @return
	 * @throws RuntimeException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <T> List<T> findByCriteria(AbstractPersistentEntity entity){
		return getDao(entity.getClass()).findByCriteria(entity);
	}

	@SuppressWarnings({ "unchecked" })
	public <T> T findById(Class<T> requiredType, Serializable id){
		return (T) getDao(requiredType).findById(id);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Transactional(value=TxType.REQUIRED)
	public <T> T save(AbstractPersistentEntity entity){
		return (T) getDao(entity.getClass()).save(entity);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Transactional(value=TxType.REQUIRED)
	public int delete(Class requiredType, Serializable id){
		return getDao(requiredType).delete(requiredType, id);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public int delete(AbstractPersistentEntity entity){
		return getDao(entity.getClass()).delete(entity);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <T> Long count(Map<String, Criteria> map, Class<T> requiredType){
		IBaseDao dao = getDao(requiredType);
		return dao.count(map);
	}

	public <T> Long findByPaging(Map<String, Criteria> map, List<T> resultList, Class<T> requiredType){
		return findByPaging(map, null, null, resultList, requiredType);
	}
	public <T> Long findByPaging(Map<String, Criteria> map, SortByCriteria sortByCriteria, List<T> resultList, Class<T> requiredType){
		return findByPaging(map, sortByCriteria, null, resultList, requiredType);
	}
	public <T> Long findByPaging(Map<String, Criteria> map, PagingCriteria pagingCriteria, List<T> resultList, Class<T> requiredType){
		return findByPaging(map, null, pagingCriteria, resultList, requiredType);
	}

	@SuppressWarnings({ "unchecked" })
	public <T> Long findByPaging(Map<String, Criteria> map, SortByCriteria sortByCriteria, PagingCriteria pagingCriteria, List<T> resultList, Class<T> requiredType){
		if(logger.isTraceEnabled()){
			logger.trace("#findByPaging start");
		}
		return getDao(requiredType).findByPaging(map, sortByCriteria, pagingCriteria, resultList);
	}


	@SuppressWarnings("rawtypes")
	protected <T> IBaseDao getDao(Class<T> clazz){
		return classDaoMap.get(clazz);
	}

	@SuppressWarnings("rawtypes")
	protected Class getEntityClass(Class clazz) {
		return (Class) ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments()[0];
	}

}
