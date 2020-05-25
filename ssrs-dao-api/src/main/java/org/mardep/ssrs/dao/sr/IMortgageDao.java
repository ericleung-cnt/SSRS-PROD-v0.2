package org.mardep.ssrs.dao.sr;

import java.util.List;
import java.util.Map;

import org.mardep.ssrs.dao.IBaseDao;
import org.mardep.ssrs.domain.sr.Mortgage;
import org.mardep.ssrs.domain.sr.MortgagePK;

public interface IMortgageDao extends IBaseDao<Mortgage, MortgagePK> {

	Map<String, String> findMortgageRegDateNatures(String applNo);

	List<Mortgage> findByApplId(String applNo);

	char nextPriority(String applNo);
}
