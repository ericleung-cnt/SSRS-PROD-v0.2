package org.mardep.ssrs.dmi.sr;

import org.mardep.ssrs.domain.sr.Mortgagee;
import org.mardep.ssrs.service.IMortgageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class MortgageeDMI extends AbstractSrDMI<Mortgagee> {

	@Autowired
	IMortgageService ms;
	
	@Override
	public DSResponse fetch(Mortgagee entity, DSRequest dsRequest) {
		// TODO Auto-generated method stub
		return super.fetch(entity, dsRequest);
	}
	
}
