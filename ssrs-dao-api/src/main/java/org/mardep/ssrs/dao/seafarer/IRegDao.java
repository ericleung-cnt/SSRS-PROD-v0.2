package org.mardep.ssrs.dao.seafarer;

import org.mardep.ssrs.dao.IBaseDao;
import org.mardep.ssrs.domain.seafarer.CommonPK;
import org.mardep.ssrs.domain.seafarer.Reg;

public interface IRegDao extends IBaseDao<Reg, CommonPK> {

	public Reg findLatestBySeafarerId(String seafarerId);
	
}
