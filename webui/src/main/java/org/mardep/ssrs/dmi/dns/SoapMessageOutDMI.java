package org.mardep.ssrs.dmi.dns;

import org.mardep.ssrs.dmi.AbstractDMI;
import org.mardep.ssrs.dns.ISoapMessageService;
import org.mardep.ssrs.domain.dns.SoapMessageOut;
import org.mardep.ssrs.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class SoapMessageOutDMI extends AbstractDMI<SoapMessageOut> {

	@Autowired
	private ISoapMessageService soapMessageService;

	@Override
	public DSResponse fetch(SoapMessageOut entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}
	
	@Override
	protected IBaseService getBaseService() {
		return soapMessageService;
	}

}
