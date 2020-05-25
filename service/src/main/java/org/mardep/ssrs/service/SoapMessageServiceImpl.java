package org.mardep.ssrs.service;

import javax.transaction.Transactional;

import org.mardep.ssrs.dao.dns.IControlDataDao;
import org.mardep.ssrs.dao.dns.ISoapMessageInDao;
import org.mardep.ssrs.dao.dns.ISoapMessageOutDao;
import org.mardep.ssrs.dns.ISoapMessageService;
import org.mardep.ssrs.domain.dns.SoapMessageIn;
import org.mardep.ssrs.domain.dns.SoapMessageOut;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class SoapMessageServiceImpl extends AbstractService implements ISoapMessageService {

	private ISoapMessageOutDao soapMessageOutDao;
	private ISoapMessageInDao soapMessageInDao;
	private IControlDataDao controlDataDao;
	
	 
	
}
