package org.mardep.ssrs.dao.dns;

import java.util.Date;
import java.util.List;

import org.mardep.ssrs.dao.IBaseDao;
import org.mardep.ssrs.domain.dns.SoapMessageOut;

public interface ISoapMessageOutDao extends IBaseDao<SoapMessageOut, Long> {

	SoapMessageOut findByControlId(Long controlId);
	List<SoapMessageOut> findListByControlId(Long controlId);
	SoapMessageOut readOne();
	List<SoapMessageOut> findResponseAfter(Date date);
}
