package org.mardep.ssrs.dao.sr;

import java.util.Date;

import org.mardep.ssrs.dao.IBaseDao;
import org.mardep.ssrs.domain.sr.Representative;

public interface IRepresentativeDao extends IBaseDao<Representative, String> {

	Representative findByApplId(String applNo, Date reportDate);
	
	Representative findByApplId(String applNo);

}
