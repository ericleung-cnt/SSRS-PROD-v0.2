package org.mardep.ssrs.dao.seafarer;

import java.util.List;

import org.mardep.ssrs.dao.IBaseDao;
import org.mardep.ssrs.domain.seafarer.CommonPK;
import org.mardep.ssrs.domain.seafarer.SeaService;

public interface ISeaServiceDao extends IBaseDao<SeaService, CommonPK> {

	SeaService findLatestBySeafarerId(String seafarerId);
	List<SeaService> findBySeafarerId(String seafarerId);
	
	SeaService findPreviousBySeafarerId(String seafarerId, Integer seqNo);
	
}
