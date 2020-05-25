package org.mardep.ssrs.dao;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.mardep.ssrs.dao.PredicateCriteria.PredicateType;
import org.mardep.ssrs.domain.AbstractPersistentEntity;
import org.mardep.ssrs.domain.sr.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractJpaDao<T extends AbstractPersistentEntity<K>, K extends Serializable> implements IBaseDao<T,K> {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	protected Map<String, Class<?>> publicFields = new HashMap<>();
	protected List<PredicateCriteria> criteriaList = new ArrayList<>();

	public AbstractJpaDao() {
		Method[] methods = getEntityClass().getMethods();
		for (Method m : methods) {
			boolean returnVoid = Void.TYPE.equals(m.getReturnType());
			if (m.getName().matches("set[A-Z].*") &&
					returnVoid && m.getParameterCount() == 1) {
				Class<?> param = m.getParameterTypes()[0];
				String name = m.getName().substring(3);
				name = name.substring(0,1).toLowerCase() + name.substring(1);
				publicFields.put(name, param);
				if (String.class.equals(param)) {
					criteriaList.add(new PredicateCriteria(name, PredicateType.LIKE_IGNORE_CASE));
				} else if (Integer.TYPE.equals(param) ||
						Long.TYPE.equals(param) ||
						Boolean.TYPE.equals(param) ||
						Integer.class.equals(param) ||
						Long.class.equals(param) ||
						Boolean.class.equals(param) ||
						BigDecimal.class.equals(param)
						) {
					criteriaList.add(new PredicateCriteria(name, PredicateType.EQUAL));
				} else if (Date.class.equals(param)) {
					criteriaList.add(new PredicateCriteria(name, PredicateType.GREATER_OR_EQUAL));
					criteriaList.add(new PredicateCriteria(name, PredicateType.LESS_OR_EQUAL));
					criteriaList.add(new PredicateCriteria(name + "From", name, PredicateType.GREATER_OR_EQUAL));
					criteriaList.add(new PredicateCriteria(name + "To", name, PredicateType.LESS_OR_EQUAL));
				}
			}
		}
	}

	@PersistenceContext
	protected EntityManager em;
	private List<Method> historyGetter;
	private List<Method> historySetters;
	private Method keyMethod;
	private String historyInsertSql;
	private String historyTableName;
	private String columnNames;
	private String historySelectSql;
	private String keyColumn;

	public T findById(K id) {
		if (logger.isDebugEnabled()) {
			logger.debug("#findById start");
		}
		return em.find(getEntityClass(), id);
	}

	public T save(T entity) {
		return em.merge(entity);
	}

	public int delete(Class<T> clazz,  K id) {
		T o = em.find(getEntityClass(), id);
		em.remove(o);
		return 1;
	}

	public int delete(T o) {
//		T o = em.find(getEntityClass(), id);
		em.remove(o);
//		TODO
		return 1;
	}

	public List<T> findAll() {
		if (logger.isDebugEnabled()) {
			logger.debug("#findAll start");
		}
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(getEntityClass());
		Root<T> rootEntry = criteriaQuery.from(getEntityClass());
		criteriaQuery.select(rootEntry);
		TypedQuery<T> typedQuery = em.createQuery(criteriaQuery);
		List<T> result = typedQuery.getResultList();
		if (logger.isDebugEnabled()) {
			logger.debug("#findAll end, result:{}", result != null ? result.size() : 0);
		}
		return result;
	}

	public T findLatestBySeafarerId(String seafarerId){
		return null;
	}

	public List<T> findByCriteria(T entity){
		List<T> rs = new ArrayList<T>();
		Map<String, Criteria> criteria = new HashMap<String, Criteria>();

		getCriteria(entity, "", criteria);
		findByPaging(criteria, null, null, rs);
		return rs;
	}

	private boolean hasField(Class<?> clazz, String fieldName) {
		try {
			clazz.getDeclaredField(fieldName);
			return true;
		} catch (NoSuchFieldException e) {
			if(clazz.equals(Object.class)) {
				return false;
			} else {
				return hasField(clazz.getSuperclass(), fieldName);
			}
		}
	}

	private void getCriteria(Object entity, String path, Map<String, Criteria> criteria) {
		Class<T> entityType = getEntityClass();
		Class<?> clazz = entity.getClass();
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(clazz, Object.class);
			PropertyDescriptor[] props = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor prop : props) {
				String fieldName = prop.getName();
				if (!hasField(entityType, fieldName)
						&& !hasField(clazz, fieldName)) {
					logger.trace("No field: {}", fieldName);
					continue;
				}

				Method getter = prop.getReadMethod();
				logger.trace("name: {}, getter: {}", fieldName, getter.getName());

				try {
					Object value = getter.invoke(entity);
					if (value != null) {
						if (value instanceof AbstractPersistentEntity<?>) {
							if (path.contains(fieldName + "/")) {
								// avoid cycle
								continue;
							}
							String newPath = path.equals("")
									? fieldName + "/"
									: path + fieldName + "/";
							getCriteria(value, newPath, criteria);
						} else {
							logger.debug("criteria: ({}{}, {})", path, fieldName, value);
							criteria.put(fieldName, new Criteria(path + fieldName, value));
						}
					}
				} catch (Exception ex) {
					logger.error("error calling #{}", getter.getName(), ex);
				}
			}
		} catch (IntrospectionException ex) {
			logger.error("error in #findByCriteria", ex);
		}
	}

	public Long findByPaging(Map<String, Criteria> map, final SortByCriteria sortByCriteria, PagingCriteria pagingCriteria, List<T> resultList){
		return this.findByPaging(map, sortByCriteria, pagingCriteria, resultList, false);
	}

	public Long findByPaging(Map<String, Criteria> map, final SortByCriteria sortByCriteria, PagingCriteria pagingCriteria, List<T> resultList, boolean checkCount){
		if(logger.isDebugEnabled()){
			logger.debug("#findByPaging start:{}", pagingCriteria);
		}
		Class<T> clazz = getEntityClass();
		CriteriaBuilder cb = em.getCriteriaBuilder();

		CriteriaQuery<T> queryList = cb.createQuery(clazz);
		Root<T> listRoot = queryList.from(clazz);

		CriteriaQuery<Long> queryCount = cb.createQuery(Long.class);
		Root<T> countRoot = queryCount.from(clazz);


		Expression<Long> expression = cb.count(countRoot);
		boolean distinct = sortByCriteria!=null?sortByCriteria.isDistinct():false;
		queryCount.select(expression).distinct(distinct);


		List<Predicate> countPredicateList = new ArrayList<Predicate>();
		List<Predicate> listPredicateList = new ArrayList<Predicate>();

		List<PredicateCriteria> predicateCriteriaList = resolvePredicateCriteriaList(cb, listRoot);
		if(predicateCriteriaList!=null){
//			TODO
//			if(AbstractFreezablePersistentEntity.class.isAssignableFrom(getEntityClass())){
//				predicateCriteriaList.add(new PredicateCriteria("entityEnabled", PredicateType.EQUAL));
//
//			}
			resolveConditionList(predicateCriteriaList, map, cb, countPredicateList, countRoot, listPredicateList, listRoot);
		}else{
			resolveConditionList(map, cb, countPredicateList, countRoot, listPredicateList, listRoot);
		}


		queryCount.where(countPredicateList.toArray(new Predicate[countPredicateList.size()]));
		queryList.where(listPredicateList.toArray(new Predicate[listPredicateList.size()]));


		Long count = em.createQuery(queryCount).getSingleResult();
		logger.debug("findByPaging, count:{}", count);


		if(count>0){
			queryList.select(listRoot).distinct(distinct);;
			resolveOrderBy(sortByCriteria, cb, queryList, listRoot);

			TypedQuery<T> typedQuery = em.createQuery(queryList);
			if(pagingCriteria!=null && !pagingCriteria.isFetchAll()){
				typedQuery.setFirstResult((int)pagingCriteria.getStartRow());
				typedQuery.setMaxResults((int)pagingCriteria.getMaxResult());
			}
			List<T> queryResultList = typedQuery.getResultList();

			resultList.addAll(queryResultList);
		}
		if(logger.isDebugEnabled()){
			logger.debug("#findByPaging end, Count-{}, list-{}", new Object[]{count, resultList.size()});
		}
		return count;
	}

	public Long count(Map<String, Criteria> map){
		Class<T> clazz = getEntityClass();
		CriteriaBuilder cb = em.getCriteriaBuilder();

		CriteriaQuery<T> queryList = cb.createQuery(clazz);
		Root<T> listRoot = queryList.from(clazz);

		CriteriaQuery<Long> queryCount = cb.createQuery(Long.class);
		Root<T> countRoot = queryCount.from(clazz);

		Expression<Long> expression = cb.count(countRoot);
		queryCount.select(expression);

		List<Predicate> countPredicateList = new ArrayList<Predicate>();
		List<Predicate> listPredicateList = new ArrayList<Predicate>();

		List<PredicateCriteria> predicateCriteriaList = resolvePredicateCriteriaList(cb, listRoot);
		if(predicateCriteriaList!=null){
//			TODO
//			if(AbstractFreezablePersistentEntity.class.isAssignableFrom(getEntityClass())){
//				predicateCriteriaList.add(new PredicateCriteria("entityEnabled", PredicateType.EQUAL));
//			}
			resolveConditionList(predicateCriteriaList, map, cb, countPredicateList, countRoot, listPredicateList, listRoot);
		}else{
			resolveConditionList(map, cb, countPredicateList, countRoot, listPredicateList, listRoot);
		}

		queryCount.where(countPredicateList.toArray(new Predicate[countPredicateList.size()]));
		Long count = em.createQuery(queryCount).getSingleResult();
		return count;
	}

	protected List<PredicateCriteria> resolvePredicateCriteriaList(final CriteriaBuilder cb, final Root<T> listRoot){
		return criteriaList;
	}

	protected void resolveConditionList(List<PredicateCriteria> list, final Map<String, Criteria> map, final CriteriaBuilder cb, final List<Predicate> countPredicateList, final Root<T> countRoot, final List<Predicate> listPredicateList, final Root<T> listRoot){
		for (String key : map.keySet()) {
			Criteria value = map.get(key);
			Operator operator = value.getOperator();
			Class type = value.getValue() != null ? value.getValue().getClass() : null;
//			if (Date.class.equals(type)) {
//				// Date range
//				if (Operator.GREATER_OR_EQUAL.equals(operator) && key.endsWith("From")) {
//					key = key.substring(0, key.length() - 4);
//				} else if (Operator.LESS_OR_EQUAL.equals(operator) && key.endsWith("To")) {
//					key = key.substring(0, key.length() - 2);
//				}
//			}
			for(PredicateCriteria criteria : list){
				if (key.equals(criteria.getKey())) {
					String path = criteria.getPath();

					logger.debug("resolveConditionList - {}", value);
					String[] keyArray = path.split("/");
					if(PredicateType.LIKE.equals(criteria.getPredicateType())){
						String pattern = "%"+value.getValue()+"%";
						pattern = pattern.replaceAll("\\[", "[[]");
						listPredicateList.add(cb.like(getPath(listRoot, keyArray).as(String.class), pattern));
						countPredicateList.add(cb.like(getPath(countRoot, keyArray).as(String.class), pattern));
					}else if(PredicateType.LIKE_IGNORE_CASE.equals(criteria.getPredicateType())){
						if (value.getValue() != null && !(value.getValue() instanceof Collection)) {
							String pattern = "%"+value.getValue()+"%";
							pattern = pattern.replaceAll("\\[", "[[]");
							pattern = pattern.toUpperCase();
							listPredicateList.add(cb.like(cb.upper(getPath(listRoot, keyArray).as(String.class)), pattern));
							countPredicateList.add(cb.like(cb.upper(getPath(countRoot, keyArray).as(String.class)), pattern));
						}
					}else if(PredicateType.EQUAL.equals(criteria.getPredicateType())){
						if (value.getValue() == null) {
							listPredicateList.add(cb.isNull(getPath(listRoot, keyArray)));
							countPredicateList.add(cb.isNull(getPath(countRoot, keyArray)));
						} else {
							listPredicateList.add(cb.equal(getPath(listRoot, keyArray), value.getValue()));
							countPredicateList.add(cb.equal(getPath(countRoot, keyArray), value.getValue()));
						}
					}else if(PredicateType.STARTSWITH.equals(criteria.getPredicateType())){
						String pattern = value.getValue()+"%";
						pattern = pattern.replaceAll("\\[", "[[]");
						listPredicateList.add(cb.like(getPath(listRoot, keyArray).as(String.class), pattern));
						countPredicateList.add(cb.like(getPath(countRoot, keyArray).as(String.class), pattern));
					}else if(Operator.GREATER_OR_EQUAL.equals(operator) && PredicateType.GREATER_OR_EQUAL.equals(criteria.getPredicateType())){
						Object obj = value.getValue();
						if (obj instanceof Date) {
							listPredicateList.add(cb.greaterThanOrEqualTo(getPath(listRoot, keyArray), (Date)value.getValue()));
							countPredicateList.add(cb.greaterThanOrEqualTo(getPath(countRoot, keyArray), (Date)value.getValue()));
						} else {
							listPredicateList.add(cb.greaterThanOrEqualTo(getPath(listRoot, keyArray), (String)value.getValue()));
							countPredicateList.add(cb.greaterThanOrEqualTo(getPath(countRoot, keyArray), (String)value.getValue()));
						}
					}else if(Operator.LESS_OR_EQUAL.equals(operator) && PredicateType.LESS_OR_EQUAL.equals(criteria.getPredicateType())){
						Object obj = value.getValue();
						if (obj instanceof Date) {
							listPredicateList.add(cb.lessThanOrEqualTo(getPath(listRoot, keyArray), (Date)value.getValue()));
							countPredicateList.add(cb.lessThanOrEqualTo(getPath(countRoot, keyArray), (Date)value.getValue()));
						} else {
							listPredicateList.add(cb.lessThanOrEqualTo(getPath(listRoot, keyArray), (String)value.getValue()));
							countPredicateList.add(cb.lessThanOrEqualTo(getPath(countRoot, keyArray), (String)value.getValue()));
						}
					}else if(PredicateType.IN.equals(criteria.getPredicateType())){
						Object listObject = value.getValue();
						if(listObject instanceof Collection){
							Collection c = (Collection)listObject;
							if (!c.isEmpty()) {
								In<Collection> inPath = cb.in(getPath(listRoot, keyArray));
								In<Collection> inCount = cb.in(getPath(countRoot, keyArray));
								listPredicateList.add(inPath.value(c));
								countPredicateList.add(inCount.value(c));
							} else {
								listPredicateList.add(cb.and(cb.isNull(getPath(listRoot, keyArray)), cb.isNotNull(getPath(listRoot,keyArray))));
								countPredicateList.add(cb.and(cb.isNull(getPath(countRoot, keyArray)), cb.isNotNull(getPath(countRoot,keyArray))));
							}
						}
					}else if(PredicateType.NOT_IN_OR_NULL.equals(criteria.getPredicateType())){
						Object listObject = value.getValue();
						if(listObject instanceof Collection){
							Collection c = (Collection)listObject;
							Predicate countAnd = cb.or(cb.not(cb.in(getPath(countRoot, keyArray)).value(c)), cb.isNull(getPath(countRoot, keyArray)));
							Predicate listAnd = cb.or(cb.not(cb.in(getPath(listRoot, keyArray)).value(c)), cb.isNull(getPath(listRoot, keyArray)));
							countPredicateList.add(countAnd);
							listPredicateList.add(listAnd);
						}
					}else if(PredicateType.FIELDS_EQUAL.equals(criteria.getPredicateType())){
						String[] keys = criteria.getFieldsArray();
						listPredicateList.add(cb.equal(listRoot.get(keys[0]), listRoot.get(keys[1])));
						countPredicateList.add(cb.equal(countRoot.get(keys[0]), countRoot.get(keys[1])));
					}else if(PredicateType.FIELDS_NOT_EQUAL.equals(criteria.getPredicateType())){
						String[] keys = criteria.getFieldsArray();
						listPredicateList.add(cb.notEqual(listRoot.get(keys[0]), listRoot.get(keys[1])));
						countPredicateList.add(cb.notEqual(countRoot.get(keys[0]), countRoot.get(keys[1])));
					}else if(PredicateType.FIELDS_LESS_THAN.equals(criteria.getPredicateType())){
						String[] keys = criteria.getFieldsArray();
						Path<BigDecimal> list1= listRoot.get(keys[0]);
						Path<BigDecimal> list2= listRoot.get(keys[1]);
						Path<BigDecimal> count1= countRoot.get(keys[0]);
						Path<BigDecimal> count2= countRoot.get(keys[1]);
						listPredicateList.add(cb.lessThan(list1, list2));
						countPredicateList.add(cb.lessThan(count1, count2));
					}else if(PredicateType.FIELDS_LESS_THAN_OR_NULL.equals(criteria.getPredicateType())){
						String[] keys = criteria.getFieldsArray();
						Path<BigDecimal> list1= listRoot.get(keys[0]);
						Path<BigDecimal> list2= listRoot.get(keys[1]);
						Path<BigDecimal> count1= countRoot.get(keys[0]);
						Path<BigDecimal> count2= countRoot.get(keys[1]);
						listPredicateList.add(cb.or(cb.lessThan(list1, list2),cb.isNull(list1)));
						countPredicateList.add(cb.or(cb.lessThan(count1, count2),cb.isNull(count1)));
					}else if(PredicateType.IS_NULL.equals(criteria.getPredicateType())){
						if (Boolean.FALSE.equals(value.getValue())) {
							Predicate countAnd = cb.not(cb.isNull(getPath(countRoot, keyArray)));
							Predicate listAnd = cb.not(cb.isNull(getPath(listRoot, keyArray)));
							countPredicateList.add(countAnd);
							listPredicateList.add(listAnd);
						} else {
							Predicate countAnd = cb.isNull(getPath(countRoot, keyArray));
							Predicate listAnd = cb.isNull(getPath(listRoot, keyArray));
							countPredicateList.add(countAnd);
							listPredicateList.add(listAnd);
						}
					} else if (PredicateType.LIKE_OR.equals(criteria.getPredicateType())) {
						String[] keys = criteria.getFieldsArray();
						String pattern = "%" + value.getValue() + "%";
						pattern = pattern.replaceAll("\\[", "[[]");
						Predicate listOr = null;
						Predicate countOr = null;
						if (keys != null && keys.length > 0) {
							listOr = cb.like(getPath(listRoot, keyArray).get(keys[0]).as(String.class), pattern);
							countOr = cb.like(getPath(countRoot, keyArray).get(keys[0]).as(String.class), pattern);
						}
						if (keys != null && keys.length > 1) {
							for (int i = 1; i < keys.length; i++) {
								Predicate listPredicate = cb.like(getPath(listRoot, keyArray).get(keys[i]).as(String.class), pattern);
								Predicate countPredicate = cb.like(getPath(countRoot, keyArray).get(keys[i]).as(String.class), pattern);
								listOr = cb.or(listOr, listPredicate);
								countOr = cb.or(countOr, countPredicate);
							}
						}
						listPredicateList.add(listOr);
						countPredicateList.add(countOr);
					} else if (PredicateType.EQUAL_OR.equals(criteria.getPredicateType())) {
						String[] keys = criteria.getFieldsArray();
						String pattern = value.getValue() + "";
						Predicate listOr = null;
						Predicate countOr = null;
						if (keys != null && keys.length > 0) {
							listOr = cb.like(getPath(listRoot, keyArray).get(keys[0]).as(String.class), pattern);
							countOr = cb.like(getPath(countRoot, keyArray).get(keys[0]).as(String.class), pattern);
						}
						if (keys != null && keys.length > 1) {
							for (int i = 1; i < keys.length; i++) {
								Predicate listPredicate = cb.like(getPath(listRoot, keyArray).get(keys[i]).as(String.class), pattern);
								Predicate countPredicate = cb.like(getPath(countRoot, keyArray).get(keys[i]).as(String.class), pattern);
								listOr = cb.or(listOr, listPredicate);
								countOr = cb.or(countOr, countPredicate);
							}
						}
						listPredicateList.add(listOr);
						countPredicateList.add(countOr);
					}
				}
			}
		}
	}

	protected Path getPath(final Root<T> listRoot, String[] keyArray) {
		Path path = listRoot;
		for (String key : keyArray) {
			path = path.get(key);
		}
		return path;
	}

	protected void resolveConditionList(final Map<String, Criteria> map, final CriteriaBuilder cb, final List<Predicate> countPredicateList, final Root<T> countRoot, final List<Predicate> listPredicateList, final Root<T> listRoot){

	}

	protected void resolveOrderBy(final SortByCriteria sortByCriteria, final CriteriaBuilder cb, final CriteriaQuery<?> queryList, final Root<T> listRoot){
		if(sortByCriteria!=null){
			List<String> list = sortByCriteria.getSortByFieldsList();
			List<Order> orderList = new ArrayList<Order>();
			for(String field:list){
				String key = field.startsWith("-")?field.substring(1):field;
				String fieldPath = orderFieldMap(key);
				List<Order> orders = new ArrayList<Order>();
				if(fieldPath==null){
					orders.add(cb.asc(listRoot.get(key)));
				}else{
					String[] fieldArray = fieldPath.split(",");
					for (String fieldName : fieldArray){
						if(!fieldName.contains("/")){
							orders.add(cb.asc(listRoot.get(fieldPath)));
						}else if(fieldName.contains("/")){
							String[] keyArray = fieldName.split("/");
							if (keyArray.length == 2) {
								orders.add(cb.asc(listRoot.get(keyArray[0]).get(keyArray[1])));
							} else if (keyArray.length == 3) {
								orders.add(cb.asc(listRoot.get(keyArray[0]).get(keyArray[1]).get(keyArray[2])));
							} else if (keyArray.length == 4) {
								orders.add(cb.asc(listRoot.get(keyArray[0]).get(keyArray[1]).get(keyArray[2]).get(keyArray[3])));
							}
						}
					}
				}
				for (Order o: orders){
					o = field.startsWith("-") ? o.reverse():o;
					orderList.add(o);
				}
//				CriteriaQuery query = (field.startsWith("-")) ? queryList.orderBy(descOrder):queryList.orderBy(descOrder.reverse());
//				break;
			}
			queryList.orderBy(orderList);
		}
	}

	protected String orderFieldMap(String key){
		return null;
	}

	@SuppressWarnings("unchecked")
	protected Class<T> getEntityClass() {
		Class<T> entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		if (historyGetter == null) {
			synchronized (this.getClass()) {
				if (historyGetter == null) {
					Table table = entityClass.getAnnotation(Table.class);
					StringBuilder sql = new StringBuilder();
					historyGetter = new ArrayList<>();
					historySetters = new ArrayList<>();

					if (table != null) {
						sql.append("insert into ");
						historyTableName = table.name() + "_HIST";
						sql.append(historyTableName);
						sql.append(" (TX_ID");
						columnNames = "TX_ID";
						boolean broken = prepare(entityClass, sql);
						Class<? super T> superclass = entityClass.getSuperclass();
						while (!broken && !Object.class.equals(superclass)) {
							broken = prepare(superclass, sql);
							superclass = superclass.getSuperclass();
						}
						if (!broken) {
							sql.append(") values (?");
							for ( int i = 0; i < historyGetter.size(); i++) {
								sql.append(", ?");
							}
							sql.append(")");
							historyInsertSql = sql.toString();
							historySelectSql = "select " + columnNames
							+ " from " + historyTableName + " hist  "
							+ "where TX_ID = :txId "
							+ "and " + "hist." + keyColumn + " = :applNo";
						}
					}
				}
			}
		}

		return entityClass;
	}

	private boolean prepare(Class<?> entityClass, StringBuilder sql) {
		boolean broken = false;
		Field[] fields = entityClass.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Column annotation = fields[i].getAnnotation(Column.class);
			String name;
			if (annotation != null) {
				name = annotation.name();
				sql.append(", ").append(name);
				columnNames += ", " + name;

				String getterName = "get" + fields[i].getName().substring(0, 1).toUpperCase() + fields[i].getName().substring(1);
				try {
					historyGetter.add(entityClass.getDeclaredMethod(getterName));
				} catch (NoSuchMethodException e) {
					getterName = "is" + fields[i].getName().substring(0, 1).toUpperCase() + fields[i].getName().substring(1);
					try {
						historyGetter.add(entityClass.getDeclaredMethod(getterName));
					} catch (NoSuchMethodException e1) {
						logger.info("getter method not available for the field " + fields[i].getName());
					} catch (SecurityException e1) {
						logger.info("getter method not available for the field " + fields[i].getName());
					}
					broken = true;
					break;
				} catch (SecurityException e) {
					logger.info("getter method not available for the field " + fields[i].getName());
					broken = true;
					break;
				}
				String setterName = "set" + fields[i].getName().substring(0, 1).toUpperCase() + fields[i].getName().substring(1);
				try {
					Method setter = entityClass.getDeclaredMethod(setterName, fields[i].getType());
					historySetters.add(setter);
					if (fields[i].getName().matches("^.*[aA]pplNo$")) {
						keyMethod = setter;
						keyColumn = annotation.name();
					}
				} catch (NoSuchMethodException e) {
					logger.info("setter method not available for the field " + fields[i].getName());
					broken = true;
					break;
				} catch (SecurityException e) {
					logger.info("setter method not available for the field " + fields[i].getName());
					broken = true;
					break;
				}
			}
		}
		return broken;
	}

	@Override
	public void saveHistory(String applNo, Transaction tx) {
		Class<T> entityClass = getEntityClass();
		if (historyInsertSql != null && keyMethod != null) {
			Query historyInsert = em.createNativeQuery(historyInsertSql);
			try {
				T criteria = entityClass.newInstance();
				keyMethod.invoke(criteria, applNo);
				List<T> list = findByCriteria(criteria);
				for (T t : list) {
					historyInsert.setParameter(1, tx.getId());
					for (int i = 0; i < historyGetter.size(); i++) {
						logger.trace("{} {}", i+2, historyGetter.get(i));
						historyInsert.setParameter(i + 2, historyGetter.get(i).invoke(t));
					}
					historyInsert.executeUpdate();
				}
			} catch (InstantiationException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void deleteHistory(String applNo, Transaction tx) {
		if (historyTableName != null) {
			logger.info("delete history for " + historyInsertSql + " where tx_id = " + tx.getId());
			Query delete = em.createNativeQuery("delete from " + historyTableName + " where tx_id = :txId");
			delete.setParameter("txId", tx.getId());
			delete.executeUpdate();
		}
	}

	public List<T> findHistory(String applNo, Date date) {
		String txIdSql = "select AT_SER_NUM from TRANSACTIONS " +
				"where RM_APPL_NO = :applNo and convert(varchar, DATE_CHANGE, 23) + ' ' + " +
				"left(right('0000'+hour_change, 4),2)+':'+ right('0000'+hour_change, 2) = " +
				"( " +
				"   select max(time_change) from ( " +
				"      select AT_SER_NUM, convert " +
				"      (datetime2, " +
				"         convert(varchar,DATE_CHANGE,23) + ' ' + " +
				"         left(right('0000'+hour_change, 4),2)+':'+ right('0000'+hour_change, 2) " +
				"      ) time_change " +
				"      from TRANSACTIONS " +
				"      where RM_APPL_NO = :applNo " +
				"      and convert " +
				"      (datetime2, " +
				"         convert(varchar,DATE_CHANGE,23) + ' ' + " +
				"         left(right('0000'+hour_change, 4),2)+':'+ right('0000'+hour_change, 2) " +
				"      ) <= :date) tx) order by AT_SER_NUM desc ";
		Query txQuery = em.createNativeQuery(txIdSql);
		txQuery.setParameter("date", date);
		txQuery.setParameter("applNo", applNo);
		List txList = txQuery.getResultList();
		if (txList.size() == 0) {
			return new ArrayList<>();
		}
		BigInteger txId = (BigInteger) txList.get(0);

		Class<T> entityClass = getEntityClass();
		if (historySelectSql != null) {
			List<T> list = new ArrayList<>();
			Query query = em.createNativeQuery(historySelectSql);
			query.setParameter("txId", txId);
			query.setParameter("applNo", applNo);
			List resultList = query.getResultList();
			for (Object row : resultList) {
				try {
					T t = entityClass.newInstance();
					Object[] array = (Object[]) row;
					for (int i = 1; i < array.length; i++) {
						Method method = historySetters.get(i - 1);
						try {
							Object value = array[i];
							Class<?> type = method.getParameters()[0].getType();
							if (Boolean.class.equals(type)) {
								if (value instanceof String) {
									if (((String) value).matches("\\d?\\d*")) {
										value = !"0".equals(value);
									}
								}
							} else if (value instanceof Number) {
								if (Long.class.equals(type)) {
									value = ((Number) array[i]).longValue();
								} else if (Boolean.class.equals(type)) {
									value = ((Number) array[i]).intValue() != 0;
								}
							}
							method.invoke(t, value);
						} catch (IllegalArgumentException e) {
							logger.warn("ignore setter {}", method);
						}
					}
					list.add(t);
				} catch (InstantiationException e) {
					throw new RuntimeException(e);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				} catch (InvocationTargetException e) {
					throw new RuntimeException(e);
				}
			}
			return list;
		}
		return null;
	}
}
