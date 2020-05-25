package org.mardep.ssrs.dmi.sr;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.mardep.ssrs.domain.sr.Amendment;
import org.mardep.ssrs.domain.sr.Owner;
import org.mardep.ssrs.domain.sr.Transaction;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class OwnerDMI extends AbstractSrDMI<Owner> {

	@Override
	public DSResponse fetch(Owner entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(Owner entity, DSRequest dsRequest) {
		String operation = dsRequest.getOperation();
		Map values = dsRequest.getClientSuppliedValues();
		Long taskId = (Long) values.get("taskId");
		if ("ownerDS_updateData_owners".equals(operation)) {
			List<Owner> list = new ArrayList<Owner>();
			// {owners=[{type=C, name=3, address1=4, address2=5, address3=6, incorpPlace=7, applNo=2019/014}, {type=C, name=8, address1=9, address2=4, address3=01, incorpPlace=36, applNo=2019/014}]}
			List<Owner> owners = new ArrayList<>();
			for (Map<?,?> map : (List<Map<?, ?>>) values.get("owners")) {
				Owner owner = new Owner();
				owner.setType((String) map.get("type"));
				owner.setName((String) map.get("name"));
				owner.setAddress1((String) map.get("address1"));
				owner.setAddress2((String) map.get("address2"));
				owner.setAddress3((String) map.get("address3"));
				owner.setIncorpPlace((String) map.get("incorpPlace"));
				owner.setApplNo((String) map.get("applNo"));
				owner.setUpdatedBy((String) map.get("updatedBy"));
				owner.setCreatedBy((String) map.get("createdBy"));
				owner.setUpdatedDate((Date) map.get("updatedDate"));
				owner.setCreatedDate((Date) map.get("createdDate"));
				owner.setCharterSdate((Date) map.get("charterSdate"));
				owner.setCharterEdate((Date) map.get("charterEdate"));
				if (map.get("ownerSeqNo") instanceof String) {
					owner.setOwnerSeqNo(Integer.parseInt((String) map.get("ownerSeqNo")));
				} else if (map.get("ownerSeqNo") instanceof Number) {
					owner.setOwnerSeqNo(((Number) map.get("ownerSeqNo")).intValue());
				}
				owner.setVersion((Long) map.get("version"));
				owners.add(owner);
			};
			owners = shipRegService.createOwners(owners);

			return new DSResponse(owners, DSResponse.STATUS_SUCCESS);
		} else if ("ownerDS_change_receive".equals(operation)) {
			String applNo = (String) values.get("applNo");
			int ownerSeq = ((Long) values.get("ownerSeqNo")).intValue();
			shipRegService.receiveOwnerChange(applNo, ownerSeq);
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		} else if ("ownerDS_change_accept".equals(operation)) {
			shipRegService.acceptOwnerChange(taskId);
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		} else if ("ownerDS_change_approve".equals(operation)) {
			shipRegService.approveOwnerChange(taskId);
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		} else if ("ownerDS_change_crosscheck".equals(operation)) {
			shipRegService.crosscheckOwnerChange(taskId);
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		} else if ("ownerDS_change_withdraw".equals(operation)) {
			shipRegService.withdrawOwnerChange(taskId);
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		} else if ("ownerDS_change_complete".equals(operation)) {
			entity = shipRegService.completeOwnerChange(entity, taskId, getTx(values));
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		} else if ("ownerDS_Tran_receive".equals(operation)) {
			String applNo = (String) values.get("applNo");
			//int ownerSeq = ((Long) values.get("ownerSeqNo")).intValue();
			int ownerSeq = -1;
			shipRegService.receiveTransfer(applNo, ownerSeq);
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		} else if ("ownerDS_Tran_accept".equals(operation)) {
			shipRegService.acceptTransfer(taskId);
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		} else if ("ownerDS_Tran_approve".equals(operation)) {
			shipRegService.approveTransfer(taskId);
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		} else if ("ownerDS_Tran_ready".equals(operation)) {
			shipRegService.crosscheckTransfer(taskId);
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		} else if ("ownerDS_Tran_complete".equals(operation)) {
			Transaction tx = getTx(values);
			entity = shipRegService.completeTransfer(entity, taskId, tx);
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		} else if ("ownerDS_Tran_withdraw".equals(operation)) {
			shipRegService.withdrawTransfer(taskId);
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		} else if ("ownerDS_amend_details".equals(operation)) {
			Transaction tx = getTx(values);
			Amendment amm = new Amendment();
			amm.setApplNo(entity.getApplNo());
			amm.setCode(Transaction.CODE_CHG_OWNER_OTHERS);
			amm.setDetails(tx.getDetails());
			amm.setTransactionTime(new Date());
			amm.setUserId(UserContextThreadLocalHolder.getCurrentUserId());
			Owner result = shipRegService.amendOwner(entity, amm);
			return new DSResponse(result, DSResponse.STATUS_SUCCESS);
		} else if ("ownerDS_delete".equals(operation)) {
			String applNo = values.get("applNo").toString();
			int seq = Integer.parseInt(values.get("seq").toString());
			int result = shipRegService.removeOwnerByApplNoAndSeq(applNo, seq);
			return new DSResponse(result, result>0 ? DSResponse.STATUS_SUCCESS : DSResponse.STATUS_FAILURE);
		} else {
			Transaction tx = getTx(values);
			Owner result = shipRegService.updateOwner(entity, tx);
			return new DSResponse(result, DSResponse.STATUS_SUCCESS);
		}
	}

	@Override
	public DSResponse add(Owner entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}


}
