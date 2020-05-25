package org.mardep.ssrs.dmi.dn;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import org.mardep.ssrs.dao.dn.IDemandNoteItemDao;
import org.mardep.ssrs.dao.sr.IRegMasterDao;
import org.mardep.ssrs.domain.dn.AdjustAtcAmt;
import org.mardep.ssrs.domain.dn.DemandNoteItem;
import org.mardep.ssrs.domain.sr.RegMaster;
import org.mardep.ssrs.service.IDemandNoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class AdjustAtcAmtDMI {

	@Autowired
	IDemandNoteItemDao dnItemDao;
	
	@Autowired
	IRegMasterDao rmDao;
	
	@Autowired
	@Qualifier("demandNoteService")
	IDemandNoteService dnSvc;
	
	public DSResponse fetch(AdjustAtcAmt entity, DSRequest dsRequest) {
		DSResponse dsResponse = new DSResponse();
		Map suppliedValues = dsRequest.getClientSuppliedCriteria();
		RegMaster rm = null;
		DemandNoteItem dnItem = null;
		
		if (suppliedValues.containsKey("applNo")) {
			String applNo = suppliedValues.get("applNo").toString();
			rm = getRegMaster(applNo);
		}
		if (suppliedValues.containsKey("itemId")) {
			Long itemId = Long.parseLong(suppliedValues.get("itemId").toString());
			dnItem = getDemandNoteItem(itemId);
		}
		AdjustAtcAmt record = new AdjustAtcAmt();
		if (rm!=null) {
			String[] applNoList = new String[] {rm.getApplNo()};
			Map<String, BigDecimal> calculatedATC = rmDao.calculateAtc(applNoList);
			BigDecimal amt100 = calculatedATC.get(rm.getApplNo());
			BigDecimal amt50 = amt100.multiply(new BigDecimal("0.5")).setScale(1, RoundingMode.FLOOR);
			record.setApplNo(rm.getApplNo());
			record.setAmt100(amt100);
			record.setAmt50(amt50);
		}
		if (dnItem!=null) {
			record.setItemId(dnItem.getId());
			record.setAdhocText(dnItem.getAdhocDemandNoteText());
			record.setAdjustAmtReason(dnItem.getAdjustAmtReason());
			record.setCurrentAmt(dnItem.getAmount());
		}
		dsResponse.setData(record);
		dsResponse.setStatus(DSResponse.STATUS_SUCCESS);		
		return dsResponse;
	}
	
	private RegMaster getRegMaster(String applNo) {
		RegMaster rm = rmDao.findById(applNo);
		return rm;
	}
	
	private DemandNoteItem getDemandNoteItem(Long itemId) {
		DemandNoteItem item = dnItemDao.findById(itemId);
		return item;
	}
	
	public DSResponse update(AdjustAtcAmt entity, DSRequest dsRequest) {
		DSResponse dsResponse = new DSResponse();
		DemandNoteItem dnItem = getDemandNoteItem(entity.getItemId());
		dnItem.setAmount(entity.getAdjustAmt());
		dnItem.setAdhocDemandNoteText(entity.getAdhocText());
		dnItem.setAdjustAmtReason(entity.getAdjustAmtReason());
		dnSvc.saveAdjustAtcAmt(dnItem);
		dsResponse.setStatus(DSResponse.STATUS_SUCCESS);
		return dsResponse;
	}
}
