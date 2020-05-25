package org.mardep.ssrs.dmi.dns;

import org.mardep.ssrs.dmi.AbstractDMI;
import org.mardep.ssrs.dns.ISoapMessageService;
import org.mardep.ssrs.domain.dns.SoapMessageIn;
import org.mardep.ssrs.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class SoapMessageInDMI extends AbstractDMI<SoapMessageIn> {
	
	@Autowired
	private ISoapMessageService soapMessageService;

	@Override
	public DSResponse fetch(SoapMessageIn entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	protected IBaseService getBaseService() {
		return soapMessageService;
	}

}
