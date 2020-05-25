package org.mardep.ssrs.dao.seafarer;

import java.util.List;

import org.mardep.ssrs.dao.IBaseDao;
import org.mardep.ssrs.domain.seafarer.CommonPK;
import org.mardep.ssrs.domain.seafarer.Employment;

public interface IEmploymentDao extends IBaseDao<Employment, CommonPK> {

	Employment findLatestBySeafarerId(String seafarerId);
	List<Employment> findBySeafarerId(String seafarerId);
}
