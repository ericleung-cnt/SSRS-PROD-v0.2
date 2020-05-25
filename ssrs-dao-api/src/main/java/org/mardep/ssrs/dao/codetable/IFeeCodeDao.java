package org.mardep.ssrs.dao.codetable;

import java.util.List;

import org.mardep.ssrs.dao.IBaseDao;
import org.mardep.ssrs.domain.codetable.FeeCode;

public interface IFeeCodeDao extends IBaseDao<FeeCode, String> {

	List<FeeCode> findForFormCode(List<String> formCode);
	
}
