package org.mardep.ssrs.dmi.sr;

import java.util.Map;

import org.mardep.ssrs.domain.sr.Amendment;
import org.mardep.ssrs.domain.sr.Representative;
import org.mardep.ssrs.domain.sr.Transaction;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class RepresentativeDMI extends AbstractSrDMI<Representative> {

	@Override
	public DSResponse fetch(Representative entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(Representative entity, DSRequest dsRequest) throws Exception {
		String operation = dsRequest.getOperation();
		Map values = dsRequest.getClientSuppliedValues();
		String applNo = (String) values.get("applNo");
		Long taskId = (Long) values.get("taskId");
		switch (operation) {
		case "repDS_change_receive":
			shipRegService.receiveRpChange(applNo);
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		case "repDS_change_accept":
			shipRegService.acceptRpChange(taskId);
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		case "repDS_change_approve":
			shipRegService.approveRpChange(taskId);
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		case "repDS_change_crosscheck":
			shipRegService.crosscheckRpChange(taskId);
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		case "repDS_change_complete":
			entity = shipRegService.completeRpChange(entity, taskId, getTx(values));
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		case "repDS_change_withdraw":
			shipRegService.withdrawRpChange(taskId);
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
//		case "rpDS_amend":
//			Transaction tx = getTx(values);
//			Amendment amm = new Amendment();
//			amm.setApplNo(entity.getApplNo());
//			amm.setCode(Transaction.CODE_CHG_RP_OTHERS);
//			amm.setDetails(tx.getDetails());
//			amm.setUserId(UserContextThreadLocalHolder.getCurrentUserId());
//			Representative result = shipRegService.amendRP(entity, amm);
//			return new DSResponse(result, DSResponse.STATUS_SUCCESS);
		}
		return super.update(entity, dsRequest);
	}

	@Override
	public DSResponse add(Representative entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}


}
