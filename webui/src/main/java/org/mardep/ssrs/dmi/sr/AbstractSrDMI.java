package org.mardep.ssrs.dmi.sr;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import org.mardep.ssrs.dmi.AbstractDMI;
import org.mardep.ssrs.domain.AbstractPersistentEntity;
import org.mardep.ssrs.domain.sr.Transaction;
import org.mardep.ssrs.service.IBaseService;
import org.mardep.ssrs.service.IShipRegService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractSrDMI<T extends AbstractPersistentEntity<?>> extends AbstractDMI<T>{

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	protected IShipRegService shipRegService;
//
	@Override
	protected IBaseService getBaseService(){
		return shipRegService;
	}

	protected Transaction getTx(Map data) {
		Transaction tx = null;
		Map<?,?> txMap = (Map<?, ?>) data.get("tx");
		if (txMap != null) {
			tx = new Transaction();
			Date dateChange = (Date) txMap.get("changeDate");

			Number number;
			Object changeHourValue = txMap.get("changeHour");
			if (changeHourValue instanceof String) {
				number = Integer.parseInt((String) changeHourValue);
			} else if (changeHourValue instanceof Number) {
				number = (Number) changeHourValue;
			} else {
				number = 0;
			}
			if (number == null) {
				throw new NullPointerException("change hour is missing");
			}
			number = (10000 + number.intValue());
			String hourChange = number.toString().substring(number.toString().length() - 4);
			tx.setDateChange(dateChange);
			tx.setHourChange(hourChange);
			tx.setDetails((String) txMap.get("details"));
			String applNo = (String) txMap.get("applNo");
			tx.setHandledBy((String) txMap.get("handledBy"));
			tx.setHandlingAgent((String) txMap.get("handlingAgent"));
			if (applNo == null) {
				applNo = (String) data.get("applNo");
			}
			tx.setApplNo(applNo);
		}
		return tx;
	}

	protected void setValues(Object target, Map<?, ?> keyValues) {
		Class<?> clazz = target.getClass();
		for (Object key:keyValues.keySet()) {
			String methodName = "get";
			if (key instanceof String) {
				Object value = keyValues.get(key);
				if (value != null) {
					Method m = null;
					String keyStr = (String) key;
					if (keyStr.length() > 1) {
						String fieldName = keyStr.substring(0,1).toUpperCase() + keyStr.substring(1);
						methodName = "get"+fieldName;
						try {
							m = clazz.getMethod(methodName, new Class[0]);
							if (m != null) {
								if (Integer.class.equals(m.getReturnType())) {
									if (value instanceof Number) {
										value = ((Number) value).intValue();
									} else if (value instanceof String) {
										try {
											value = Integer.parseInt((String) value);
										} catch (NumberFormatException e) {
											logger.warn("illegal argument {}.{}({})", target, m, value);
										}
									}
								} else if (BigDecimal.class.equals(m.getReturnType())) {
									if (value instanceof String) {
										try {
											value = new BigDecimal((String) value);
										} catch (Exception e) {
											logger.warn("illegal argument {}.{}({})", target, m, value);
										}
									} else if (value instanceof Long) {
										value = new BigDecimal((Long) value);
									}
								}

								m = clazz.getMethod("set"+fieldName, m.getReturnType());
								if (m != null) {
									m.invoke(target, value);
								}
							}
						} catch (NoSuchMethodException e) {
						} catch (SecurityException e) {
						} catch (IllegalAccessException e) {
						} catch (IllegalArgumentException e) {
							logger.warn("illegal argument {}.{}({})", target, m, value);
						} catch (InvocationTargetException e) {
						}
					}
				}
			}
		}
	}

}
