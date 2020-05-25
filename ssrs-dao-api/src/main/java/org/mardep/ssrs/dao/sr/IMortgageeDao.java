package org.mardep.ssrs.dao.sr;

import java.util.Date;
import java.util.List;

import org.mardep.ssrs.dao.IBaseDao;
import org.mardep.ssrs.domain.sr.Mortgage;
import org.mardep.ssrs.domain.sr.Mortgagee;
import org.mardep.ssrs.domain.sr.MortgageePK;

public interface IMortgageeDao extends IBaseDao<Mortgagee, MortgageePK> {

	List<Mortgagee> findByCriteria(Mortgage mortgage);

	List<Mortgagee> findByTime(Mortgage mortgage, Date reportDate);
	
	List<Mortgagee> findByMortgage(Mortgage mortgage);
}
