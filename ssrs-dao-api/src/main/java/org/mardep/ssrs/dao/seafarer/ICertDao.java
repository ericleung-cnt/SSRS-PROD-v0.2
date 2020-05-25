package org.mardep.ssrs.dao.seafarer;

import org.mardep.ssrs.dao.IBaseDao;
import org.mardep.ssrs.domain.seafarer.Cert;
import org.mardep.ssrs.domain.seafarer.CommonPK;

public interface ICertDao extends IBaseDao<Cert, CommonPK> {

	Cert findLatestBySeafarerId(String seafarerId);
	
}
