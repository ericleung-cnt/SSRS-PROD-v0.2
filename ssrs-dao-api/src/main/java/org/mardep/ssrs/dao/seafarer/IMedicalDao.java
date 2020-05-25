package org.mardep.ssrs.dao.seafarer;

import org.mardep.ssrs.dao.IBaseDao;
import org.mardep.ssrs.domain.seafarer.CommonPK;
import org.mardep.ssrs.domain.seafarer.Medical;

public interface IMedicalDao extends IBaseDao<Medical, CommonPK> {
	
	public Medical findLatestBySeafarerId(String seafarerId);
	
}
