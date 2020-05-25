package org.mardep.ssrs.dao.sr;

import java.util.List;

import org.mardep.ssrs.dao.IBaseDao;
import org.mardep.ssrs.domain.sr.Owner;
import org.mardep.ssrs.domain.sr.OwnerPK;

public interface IOwnerDao extends IBaseDao<Owner, OwnerPK> {

	/**
	 * Return Owner by Reg Master record
	 * @param imoNo
	 * @return
	 */
	List<Owner> findByImo(String imoNo);

	List<Owner> findByApplId(String applNo);

	int deleteByApplNoAndSeq(String applNo, int seq);
}
