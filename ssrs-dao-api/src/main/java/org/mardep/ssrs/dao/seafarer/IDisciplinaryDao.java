package org.mardep.ssrs.dao.seafarer;

import org.mardep.ssrs.dao.IBaseDao;
import org.mardep.ssrs.domain.seafarer.CommonPK;
import org.mardep.ssrs.domain.seafarer.Disciplinary;

public interface IDisciplinaryDao extends IBaseDao<Disciplinary, CommonPK> {

	Disciplinary findLatestBySeafarerId(String seafarerId);
	
}
