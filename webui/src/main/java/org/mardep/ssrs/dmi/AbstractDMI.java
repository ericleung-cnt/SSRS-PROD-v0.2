package org.mardep.ssrs.dmi;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.OptimisticLockException;

import org.hibernate.StaleStateException;
import org.mardep.ssrs.dao.Criteria;
import org.mardep.ssrs.dao.Operator;
import org.mardep.ssrs.dao.PagingCriteria;
import org.mardep.ssrs.dao.SortByCriteria;
import org.mardep.ssrs.domain.AbstractPersistentEntity;
import org.mardep.ssrs.domain.constant.Sex;
import org.mardep.ssrs.service.IBaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

public abstract class AbstractDMI<T extends AbstractPersistentEntity<?>> {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	private ObjectMapper mapper = new ObjectMapper();

	protected abstract IBaseService getBaseService();

	@SuppressWarnings("rawtypes")
	protected DSResponse fetchByCriteria(T entity, DSRequest dsRequest) throws Exception{
		logger.info("Fetch: Entity-{}, DSRequest-{}", new Object[]{entity, dsRequest});
		DSResponse dsResponse = new DSResponse();
		dsResponse.setStatus(DSResponse.STATUS_SUCCESS);
		List<? extends AbstractPersistentEntity> list = getBaseService().findByCriteria(entity);
		dsResponse.setData(list);
		dsResponse.setTotalRows(list.size());
		return dsResponse;
	}

	@SuppressWarnings("rawtypes")
	public DSResponse update(T entity, DSRequest dsRequest) throws Exception {
		logger.info("Update: {}", dsRequest);
		DSResponse dsResponse = new DSResponse();
		dsResponse.setStatus(DSResponse.STATUS_SUCCESS);
		try {
			AbstractPersistentEntity dbEntity = getBaseService().save(entity);
			dsResponse.setTotalRows(1);
			dsResponse.setData(dbEntity);
			return dsResponse;
		} catch (DataIntegrityViolationException dve) {
			dsResponse.setFailure("Constraint Violation: " + (entity != null ? entity.getId() : "null"));
			return dsResponse;
		} catch (Exception ex){
			logger.error("Fail to update-{}, Exception-{}", new Object[]{entity, ex}, ex);
			return handleException(dsResponse, ex);
		}
	}

	public DSResponse add(T entity, DSRequest dsRequest) throws Exception {
		logger.info("Add: {}", dsRequest);
		DSResponse dsResponse = this.update(entity, dsRequest);
		return dsResponse;
	}
	public DSResponse remove(T entity, DSRequest dsRequest) throws Exception{
		logger.info("Remove Object Data: {}", mapper.writeValueAsString(entity));
		DSResponse dsResponse = new DSResponse();
		dsResponse.setStatus(DSResponse.STATUS_SUCCESS);
		try {
			getBaseService().delete(entity.getClass(), entity.getId());
			dsResponse.setTotalRows(1);
			dsResponse.setData(entity);
			return dsResponse;
		} catch (Exception ex){
			logger.error("Fail to update-{}, Exception-{}", new Object[]{entity, ex}, ex);
			return handleException(dsResponse, ex);
		}
	}

	public DSResponse fetch(T entity, DSRequest dsRequest){
		long startTime = System.currentTimeMillis();
		long startRow = dsRequest.getStartRow();
		long endRow = dsRequest.getEndRow();
		String operationId = dsRequest.getOperationId();
		logger.info("Fetch:OperationID-[{}], Entity-[{}], StartRow-[{}], EndRow-[{}]", new Object[]{operationId, entity, startRow, endRow});
		DSResponse dsResponse = new DSResponse();
		dsResponse.setStatus(DSResponse.STATUS_SUCCESS);
		List<T> resultList = new ArrayList<T>();
		PagingCriteria pagingCriteria = new PagingCriteria();
		if(endRow == -1){
			pagingCriteria.setFetchAll(true);
		}
		if(endRow<startRow){
			startRow = 0;
			endRow = 75;
		}
		pagingCriteria.setStartRow(startRow);
		pagingCriteria.setEndRow(endRow);
		SortByCriteria sortByCriteria = null;
		if(dsRequest.getSortBy()!=null){
			sortByCriteria = new SortByCriteria(dsRequest.getSortByFields());
		}else{
			sortByCriteria = new SortByCriteria(new ArrayList<String>());
		}
		Long totalRows = 0L;;
		try {
			Map<String, Object> criteriaMap = null;
			if(dsRequest.getAdvancedCriteria()!=null){
				criteriaMap = dsRequest.getAdvancedCriteria().getCriteriaAsMap();
			}else{
				criteriaMap = dsRequest.getCriteria();
			}
			Map<String, Criteria> map = constructFromAdvancedCriteriaMap(criteriaMap);

			IBaseService baseService = getBaseService();
			if(baseService == null){
				logger.error("##### Base Service not config  ####");
			}else{
				totalRows = baseService.findByPaging(map, sortByCriteria, pagingCriteria, resultList, getEntityClass());
			}
			dsResponse.setStartRow(dsRequest.getStartRow());
			dsResponse.setEndRow(endRow>totalRows ? totalRows:endRow);
			dsResponse.setData(resultList);
			dsResponse.setTotalRows(totalRows);
		} catch (Exception e) {
			handleException(dsResponse, e);
		}
		long endTime = System.currentTimeMillis();
		logger.info("Time:{}", (endTime - startTime));
		return dsResponse;
	}

	@SuppressWarnings("unchecked")
	protected Map<String, Criteria> constructFromAdvancedCriteriaMap(Map<String, Object> map){
		Map<String, Criteria> criteriaMap = new HashMap<String, Criteria>();
		Map<String, Class> enumClassMap = getEnumClassMap();
		if(map.containsKey("fieldName")){
			String fieldName = (String)map.get("fieldName");
			String operator = (String)map.get("operator");
			Object value = map.get("value");
			Criteria c = null;
			Operator scOperator = Operator.getEnum(operator);
			if(enumClassMap.containsKey(fieldName)){
				Class clazz = enumClassMap.get(fieldName);
				Enum e =Enum.valueOf(clazz, value.toString());
				c = new Criteria(fieldName, e, scOperator);
			}else{
				if(value instanceof Date && (Operator.GREATER_OR_EQUAL.equals(scOperator) || Operator.LESS_OR_EQUAL.equals(scOperator))){
					Calendar cal = Calendar.getInstance();
					switch(scOperator){
					case GREATER_OR_EQUAL:
						fieldName = fieldName+"From";
						cal.setTime((Date)value);
						cal.set(Calendar.HOUR_OF_DAY, 0);
						cal.set(Calendar.MINUTE, 0);
						cal.set(Calendar.SECOND, 0);
						cal.set(Calendar.MILLISECOND, 0);
						value = cal.getTime();
						break;
					case LESS_OR_EQUAL:
						fieldName = fieldName+"To";
						cal.setTime((Date)value);
						cal.set(Calendar.HOUR_OF_DAY, 23);
						cal.set(Calendar.MINUTE, 59);
						cal.set(Calendar.SECOND, 59);
						cal.set(Calendar.MILLISECOND, 999);
						value = cal.getTime();
						break;
					default:
						break;
					}
					c = new Criteria(fieldName, value, scOperator);
				}else{
					c = new Criteria(fieldName, value, scOperator);
				}

			}
			criteriaMap.put(fieldName, c);
		}else if(map.containsKey("criteria")){
			List<Map<String, Object>> tempCriteriaList = (List<Map<String, Object>>)map.get("criteria");
			for(Map<String, Object> tempMap:tempCriteriaList){
				Map<String, Criteria> tempCriteriaMap = constructFromAdvancedCriteriaMap(tempMap);
				criteriaMap.putAll(tempCriteriaMap);
			}
		}
		return criteriaMap;
	}

	protected Map<String, Criteria> prepareCriteriaMap(DSRequest dsRequest) {
		Map<String, Object> criteriaMap = null;
		if (dsRequest.getAdvancedCriteria() != null)
			criteriaMap = dsRequest.getAdvancedCriteria().getCriteriaAsMap();
		else
			criteriaMap = dsRequest.getCriteria();
		return constructFromAdvancedCriteriaMap(criteriaMap);
	}

	protected Map<String, Class> getEnumClassMap(){
		Map<String, Class> map = new HashMap<String, Class>();
		map.put("sex", Sex.class);
		return map;
	}


	@SuppressWarnings("unchecked")
	protected Class<T> getEntityClass(){
		return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	protected DSResponse handleException(Exception e) {
		DSResponse dsResponse = new DSResponse();
		return handleException(dsResponse, e);
	}

	protected DSResponse handleException(DSResponse dsResponse, Exception e) {
		logger.error(e.getMessage(), e);
		String errorMessage = getSpecErrorMessage(e);
		if (errorMessage == null || "".equals(errorMessage)) {
			errorMessage = e.getMessage() + "\n\nPlease contact administrator or try again later.";
		}
		return handleException(dsResponse, errorMessage);
	}

	protected DSResponse handleException(DSResponse dsResponse, String errorMessage) {
//		Date now = new Date();
//		dsResponse.setData(now.toString() + ":" + "\n\n" + errorMessage);
		dsResponse.setData(errorMessage);
		dsResponse.setErrors(null);
		dsResponse.setFailure();
		return dsResponse;
	}

	private String getSpecErrorMessage(Exception ex) {
		if (ex instanceof StaleStateException || ex instanceof OptimisticLockException) {
			return "Row was updated or deleted by another user, Please refresh the data grid and try again.";
		} else if (ex instanceof RuntimeException) {
			return ex.getMessage();
		} else if (ex.getCause() instanceof Exception) {
			return getSpecErrorMessage((Exception)ex.getCause());
		} else if (ex instanceof Exception) {
			return ex + " occurred.";
		} else {
			return null;
		}
	}
}

