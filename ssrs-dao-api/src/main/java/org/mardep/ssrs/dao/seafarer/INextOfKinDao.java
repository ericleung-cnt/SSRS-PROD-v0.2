package org.mardep.ssrs.dao.seafarer;

import org.mardep.ssrs.dao.IBaseDao;
import org.mardep.ssrs.domain.seafarer.CommonPK;
import org.mardep.ssrs.domain.seafarer.NextOfKin;

public interface INextOfKinDao extends IBaseDao<NextOfKin, CommonPK> {

	public NextOfKin findLatestBySeafarerId(String seafarerId);
	
}
