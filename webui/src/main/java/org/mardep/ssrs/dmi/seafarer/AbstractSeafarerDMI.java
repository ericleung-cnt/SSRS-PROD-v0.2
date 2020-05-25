package org.mardep.ssrs.dmi.seafarer;

import org.mardep.ssrs.dmi.AbstractDMI;
import org.mardep.ssrs.domain.AbstractPersistentEntity;
import org.mardep.ssrs.service.IBaseService;
import org.mardep.ssrs.service.ISeafarerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractSeafarerDMI<T extends AbstractPersistentEntity<?>> extends AbstractDMI<T>{

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	protected ISeafarerService seafarerService;
	
	@Override
	protected IBaseService getBaseService(){
		return seafarerService;
	}
	
}
