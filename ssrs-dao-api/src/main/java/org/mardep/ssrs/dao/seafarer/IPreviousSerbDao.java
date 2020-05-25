package org.mardep.ssrs.dao.seafarer;

import java.util.List;

import org.mardep.ssrs.dao.IBaseDao;
import org.mardep.ssrs.domain.seafarer.CommonPK;
import org.mardep.ssrs.domain.seafarer.PreviousSerb;

public interface IPreviousSerbDao extends IBaseDao<PreviousSerb, CommonPK> {

	PreviousSerb findLatestBySeafarerId(String seafarerId);
	List<PreviousSerb> findBySeafarerId(String seafarerId);
	
	PreviousSerb findBySerbNo(String serbNo);
	
}
