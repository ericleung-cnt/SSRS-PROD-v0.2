package org.mardep.ssrs.dmi.sr;

import org.mardep.ssrs.domain.user.TransactionLock;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class TxnLockDMI extends AbstractSrDMI<TransactionLock> {
	@Override
	public DSResponse fetch(TransactionLock entity, DSRequest dsRequest) {
		return super.fetch(entity, dsRequest);
	}
	@Override
	public DSResponse add(TransactionLock entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}

	@Override
	public DSResponse remove(TransactionLock entity, DSRequest dsRequest)  throws Exception{
		return super.remove(entity, dsRequest);
	}
}
