package org.mardep.ssrs.dao.seafarer;

import org.mardep.ssrs.dao.IBaseDao;
import org.mardep.ssrs.domain.seafarer.Seafarer;

public interface ISeafarerDao extends IBaseDao<Seafarer, String> {

//	public List<Seafarer> findBySerbNoAnd
	Seafarer findByIdSerbNo(String id, String serbNo);
	Seafarer findBySerbNo(String serbNo);
}
