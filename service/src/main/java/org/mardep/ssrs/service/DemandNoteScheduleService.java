package org.mardep.ssrs.service;

import java.math.BigDecimal;
import java.util.List;

import javax.transaction.Transactional;

import org.mardep.ssrs.domain.constant.ReceiptStatus;
import org.mardep.ssrs.domain.dn.DemandNoteHeader;
import org.mardep.ssrs.domain.dn.DemandNoteReceipt;
import org.mardep.ssrs.domain.user.User;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service("demandNoteScheduleService")
@Transactional
public class DemandNoteScheduleService extends DemandNoteService implements IDemandNoteScheduleService{

	@Autowired
	IDnsService dnsService;

	@Scheduled(cron="${DemandNoteService.settlePendindReceipt.cron}")
	@Transactional
	@Override
	public synchronized void settlePendindReceipt(){
		User user = new User();
		user.setId("DemandNoteScheduleService");
		UserContextThreadLocalHolder.setCurrentUser(user);
		logger.info("#settlePendindReceipt");
		List<DemandNoteReceipt> pendingReceiptList = demandNoteReceiptDao.findPending();
		logger.info("Pending Receipt:[{}]", pendingReceiptList.size());
		for(DemandNoteReceipt dnReceipt:pendingReceiptList){
			String demandNoteNo = dnReceipt.getDemandNoteNo();
			String receiptNo = dnReceipt.getReceiptNo();
			Long receiptId = dnReceipt.getReceiptId();
			logger.info("Process Pending Receipt ID:{}, ReceiptNO:[{}], DemandNote NO:[{}]", new Object[]{receiptId, receiptNo, demandNoteNo});
			BigDecimal amount = dnReceipt.getAmount();
			DemandNoteHeader demandNote = dnReceipt.getDemandNoteHeader();
			BigDecimal demandNoteAmount = demandNote.getAmount();
			BigDecimal amountPaid = demandNote.getAmountPaid();

			dnReceipt.setStatus(ReceiptStatus.INSERT.getCode());
			demandNoteReceiptDao.save(dnReceipt);
			dnsService.updatePayment(receiptNo, dnReceipt.getDemandNoteNo(), amount, dnReceipt.getInputTime(), 
					dnReceipt.getPaymentType());
			
			logger.info("DemandNote:[{}], Amount Paid:{}", new Object[]{demandNoteNo, amountPaid});
			if(amountPaid==null){
				amountPaid = BigDecimal.ZERO;
			}
			BigDecimal afterAmountPaid = amountPaid.add(amount);
			demandNote.setAmountPaid(afterAmountPaid);
			logger.info("DemandNote:{} Amount Paid changed to:{}", new Object[]{demandNoteNo, afterAmountPaid});

			if(afterAmountPaid.compareTo(demandNoteAmount)>0){
				demandNote.setPaymentStatus(DemandNoteHeader.PAYMENT_STATUS_OVERPAID);
			}else if(afterAmountPaid.compareTo(demandNoteAmount)==0){
				demandNote.setPaymentStatus(DemandNoteHeader.PAYMENT_STATUS_PAID);
			}else if(afterAmountPaid.compareTo(demandNoteAmount)<0){
				demandNote.setPaymentStatus(DemandNoteHeader.PAYMENT_STATUS_PARTIAL);
			}
			demandNoteHeaderDao.save(demandNote);
		}
	}

	@Override
	@Scheduled(cron="${DemandNoteService.createAtcItem.cron}")
	public void createAtcItem() {
		logger.info("#createAtcItem");
		super.createAtcItem();
	}
	
}
