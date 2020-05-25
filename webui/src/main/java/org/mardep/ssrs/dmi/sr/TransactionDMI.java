package org.mardep.ssrs.dmi.sr;

import org.mardep.ssrs.domain.sr.Transaction;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class TransactionDMI extends AbstractSrDMI<Transaction> {
	@Override
	public DSResponse fetch(Transaction entity, DSRequest dsRequest) {
		return super.fetch(entity, dsRequest);
	}
	@Override
	public DSResponse update(Transaction entity, DSRequest dsRequest) throws Exception {
		return super.update(entity, dsRequest);
	}
	@Override
	public DSResponse add(Transaction entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
}
