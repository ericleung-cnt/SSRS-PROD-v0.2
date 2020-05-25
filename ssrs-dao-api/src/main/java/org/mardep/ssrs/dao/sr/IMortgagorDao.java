package org.mardep.ssrs.dao.sr;

import java.util.Date;
import java.util.List;

import org.mardep.ssrs.dao.IBaseDao;
import org.mardep.ssrs.domain.sr.Mortgage;
import org.mardep.ssrs.domain.sr.Mortgagor;
import org.mardep.ssrs.domain.sr.MortgagorPK;

public interface IMortgagorDao extends IBaseDao<Mortgagor, MortgagorPK> {

	List<String> findMortgagorNames(String applNo, String code);

	int deleteByName(String applNo, String priorityCode, String m);

	List<Mortgagor> findByCriteria(Mortgage mortgage);

	List<Mortgagor> findByTime(Mortgage mortgage, Date reportDate);

	List<Mortgagor> findByMortgage(Mortgage mortgage);
}
