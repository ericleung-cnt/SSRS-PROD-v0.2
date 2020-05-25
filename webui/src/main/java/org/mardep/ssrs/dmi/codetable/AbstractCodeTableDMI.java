package org.mardep.ssrs.dmi.codetable;

import org.mardep.ssrs.dmi.AbstractDMI;
import org.mardep.ssrs.domain.AbstractPersistentEntity;
import org.mardep.ssrs.service.IBaseService;
import org.mardep.ssrs.service.ICodeTableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractCodeTableDMI<T extends AbstractPersistentEntity<?>> extends AbstractDMI<T>{

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	protected ICodeTableService codeTableService;
	
	@Override
	protected IBaseService getBaseService(){
		return codeTableService;
	}

}
