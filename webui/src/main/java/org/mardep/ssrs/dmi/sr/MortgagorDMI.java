package org.mardep.ssrs.dmi.sr;

import org.mardep.ssrs.domain.sr.Mortgagor;
import org.mardep.ssrs.service.IMortgageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class MortgagorDMI extends AbstractSrDMI<Mortgagor> {

	@Autowired
	IMortgageService ms;
	
	@Override
	public DSResponse fetch(Mortgagor entity, DSRequest dsRequest) {
		return super.fetch(entity, dsRequest);
	}

}
