package org.mardep.ssrs.dns.processor;

import org.mardep.ssrs.dns.pojo.BaseRequest;
import org.mardep.ssrs.dns.pojo.BaseResponse;
import org.mardep.ssrs.service.AbstractService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractPojoService<T extends BaseRequest, R extends BaseResponse> extends AbstractService{
	
	protected final Logger logger=LoggerFactory.getLogger(getClass());
	public abstract R process(T pojo);
}
