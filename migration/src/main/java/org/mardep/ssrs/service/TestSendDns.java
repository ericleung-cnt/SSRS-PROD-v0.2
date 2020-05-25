package org.mardep.ssrs.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.mardep.ssrs.dao.codetable.ISystemParamDao;
import org.mardep.ssrs.dao.dn.IDemandNoteHeaderDao;
import org.mardep.ssrs.dao.dn.IDemandNoteItemDao;
import org.mardep.ssrs.dao.dns.IControlDataDao;
import org.mardep.ssrs.dns.IDnsOutService;
import org.mardep.ssrs.dns.pojo.common.CreateReceiptAction;
import org.mardep.ssrs.dns.pojo.common.ReceiptItem;
import org.mardep.ssrs.dns.pojo.outbound.RequestFile;
import org.mardep.ssrs.dns.pojo.outbound.createDemandNote.Action;
import org.mardep.ssrs.dns.pojo.outbound.createDemandNote.DemandNoteInfo;
import org.mardep.ssrs.dns.pojo.outbound.createDemandNote.DemandNoteItem;
import org.mardep.ssrs.dns.pojo.outbound.createDemandNote.DemandNoteRequest;
import org.mardep.ssrs.dns.pojo.outbound.createDemandNote.DemandNoteResponse;
import org.mardep.ssrs.dns.pojo.outbound.createReceipt.CreateReceiptInfo;
import org.mardep.ssrs.dns.pojo.outbound.createReceipt.ReceiptRequest;
import org.mardep.ssrs.domain.codetable.FeeCode;
import org.mardep.ssrs.domain.codetable.SystemParam;
import org.mardep.ssrs.domain.constant.Cons;
import org.mardep.ssrs.domain.dn.DemandNoteHeader;
import org.mardep.ssrs.domain.dn.DemandNoteReceipt;
import org.mardep.ssrs.domain.dns.ControlAction;
import org.mardep.ssrs.domain.dns.ControlData;
import org.mardep.ssrs.domain.dns.ControlEntity;
import org.mardep.ssrs.domain.user.User;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
@Component
public class TestSendDns implements TestService {

	@Autowired
	@Qualifier("demandNoteService")
	IDemandNoteService dns;

	@Autowired
	IDnsOutService dnsOutService;

	@Autowired
	IDemandNoteItemDao demandNoteItemDao;

	@Autowired
	IDnsService dnsService;

	@Autowired
	ISystemParamDao systemParamDao;

	@Autowired
	IControlDataDao controlDataDao;

	@Autowired
	IDemandNoteHeaderDao headerDao;

	private int countPerBatch = 1000;
	private Date adjustTime;

	private Date start;

	private Date end;

	public TestSendDns() throws ParseException {
		User user = new User();
		user.setId("DM");
		UserContextThreadLocalHolder.setCurrentUser(user);
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		adjustTime = format.parse("20190921");
		start = format.parse("20180401");
		end = format.parse("20191030");
	}
	/**
	 * step 3 read demand note from sql server and send to dns
	 * @throws ParseException
	 */
	public boolean sendDns() throws ParseException {
		DemandNoteHeader entity = new DemandNoteHeader();
		entity.setPaymentStatus(null);
		entity.setStatus(null);
		entity.setCreatedBy("DM");
		List<DemandNoteHeader> headers = dns.findByCriteria(entity);
		System.out.println("headers.size() ="+ headers.size());

		int receiptCount = 0;
		int dnCount = 0;

		List<String> failedDn = new ArrayList<>();
		List<String> failedRe = new ArrayList<>();
		SystemParam sp = systemParamDao.findById("RECEIPT_NO_SEQ");
		int nextVal = (Integer.parseInt(sp.getValue()) + 1) % 100000;
		for (DemandNoteHeader header : headers) {
			String original = header.getDemandNoteNo();
			if (original.length() == 15) {
				continue;
			}
			if (header.getAdjustReason() != null) {
				continue;
			}
			DemandNoteReceipt receiptCriteria = new DemandNoteReceipt();
			receiptCriteria.setDemandNoteNo(original);
			List<DemandNoteReceipt> receipts = dns.findByCriteria(receiptCriteria);
			boolean send = false;
			if (receipts.isEmpty()) {
				send = true;
			}
			if (!send) {
				for (DemandNoteReceipt receipt : receipts) {
					if (!receipt.getInputTime().before(start) /*inclusive*/ && receipt.getInputTime().before(end)/*exclusive*/) {
						send = true;
						break;
					}
				}
				if (!send) {
					BigDecimal received = BigDecimal.ZERO;
					for (DemandNoteReceipt receipt : receipts) {
						if (receipt.getCanAdjStatus() == null) {
							received = received.add(receipt.getAmount());
						}
					}
					if (!received.equals(header.getAmount())) {
						send = true;
					}
				}
			}

			if (!send) {
				System.out.println("not send " + original);
				continue;
			}
			System.out.println("send " + original);

			String officeCode = header.getApplNo() != null && header.getApplNo().length() > 1 ? Cons.SSRS_SR_OFFICE_CODE : Cons.SSRS_MMO_OFFICE_CODE;
			// create new demand note no. (length 15)
			String demandNoteNo = dns.getDemandNoteNumber(Cons.DNS_BILL_CODE, officeCode);
			ControlData controlData = new ControlData();
			controlData.setEntity(ControlEntity.DN.getCode());
			controlData.setAction(ControlAction.CREATE.getCode());
			controlData.setEntityId(demandNoteNo);

			ControlData newCD = controlDataDao.save(controlData);
			List<org.mardep.ssrs.domain.dn.DemandNoteItem> demandNoteItemList = demandNoteItemDao.findByDemandNoteNo(original);
			try {
				sendDemandNote(header, officeCode, demandNoteNo, newCD, demandNoteItemList);
			} catch (Exception e) {
				failedDn.add(original);
				continue;
			}
			dnCount++;

			header.setAdjustReason(demandNoteNo);
			header.setAdjustTime(adjustTime);
			headerDao.save(header);

			for (DemandNoteReceipt dnr : receipts) {

				ReceiptRequest req = new ReceiptRequest();
				ControlData cd = new ControlData();
				cd.setEntity(ControlEntity.RECEIPT.getCode());
				cd.setAction(ControlAction.CREATE.getCode());
				cd.setEntityId(dnr.getReceiptNo());
				cd = controlDataDao.save(cd);
				req.setControlId(Long.toString(cd.getId()));

				req.setCreateReceiptAction(CreateReceiptAction.U);
				CreateReceiptInfo info = new CreateReceiptInfo();
				info.setBillCode("05"); // SSRS
				info.setDnNo(demandNoteNo);
				info.setMachineID("");
				ArrayList<ReceiptItem> paymentList = new ArrayList<>();
				ReceiptItem item = new ReceiptItem();
				item.setPaymentAmount(dnr.getAmount());
				String paymentType;
				if (header.getEbsFlag() == null) {
					paymentType = "10"; // cash
				} else {
					switch (header.getEbsFlag()) {
					case "2":
						paymentType = "10"; // 10-CASH
						break;
					case "6":
						paymentType = "90";
						break;
					case "1": // autopay
						paymentType = "70";
						break;
					case "5": // TODO unknown
						paymentType = "10"; // cash
						break;
					default:
						paymentType = "50"; // credit card
						break;
					}
				}

				item.setPaymentType(paymentType);
				paymentList.add(item);
				info.setPaymentList(paymentList);
				info.setReceiptAmount(dnr.getAmount());
				info.setReceiptDate(dnr.getInputTime());


				info.setReceiptNo(dnr.getReceiptNo());
				req.setReceiptInfo(info);

				try {
					dnsOutService.sendReceipt(req);
				} catch (Exception e) {
					failedRe.add(dnr.getReceiptNo());
					continue;
				}

				if (dnr.getCanAdjStatus() != null) {
					try {
						cancel(demandNoteNo, header, dnr);
					} catch (Exception e) {
						failedRe.add(dnr.getReceiptNo());
						continue;
					}
				}
				receiptCount++;
			}
			if (header.getStatus() != null) {
				switch (header.getStatus()) {
				case DemandNoteHeader.STATUS_CANCELLED:
					simulateCancel(header);
					break;
				case DemandNoteHeader.STATUS_WRITTEN_OFF:
					// TODO
					break;
				}
			}
			if (dnCount >= countPerBatch) {
				break;
			}
		}
		String next = String.valueOf(nextVal);
		sp.setValue(next);
		systemParamDao.save(sp);

		System.out.println("dn count = " + dnCount);
		System.out.println("receipt count = " + receiptCount);
		System.out.println("failed dn size "+failedDn.size());
		System.out.println("failed re size "+failedRe.size());
		System.out.println("failed dn "+failedDn);
		System.out.println("failed re "+failedRe);

		return dnCount > 0;
	}

	private void cancel(String demandNoteNo, DemandNoteHeader header, DemandNoteReceipt dnr) {
		ReceiptRequest req = new ReceiptRequest();
		ControlData cd = new ControlData();
		cd.setEntity(ControlEntity.RECEIPT.getCode());
		cd.setAction(ControlAction.CANCEL.getCode());
		cd.setEntityId(dnr.getReceiptNo());
		cd = controlDataDao.save(cd);
		req.setControlId(Long.toString(cd.getId()));

		req.setCreateReceiptAction(CreateReceiptAction.C);
		CreateReceiptInfo info = new CreateReceiptInfo();
		info.setBillCode("05"); // SSRS
		info.setDnNo(demandNoteNo);
		info.setMachineID("");
		ArrayList<ReceiptItem> paymentList = new ArrayList<>();
		ReceiptItem item = new ReceiptItem();
		item.setPaymentAmount(dnr.getAmount());
		String paymentType;
		if (header.getEbsFlag() == null) {
			paymentType = "10"; // cash
		} else {
			switch (header.getEbsFlag()) {
			case "2":
				paymentType = "10"; // 10-CASH
				break;
			case "6":
				paymentType = "90";
				break;
			case "1": // autopay
				paymentType = "70";
				break;
			case "5": // TODO unknown
				paymentType = "10"; // cash
				break;
			default:
				paymentType = "50"; // credit card
				break;
			}
		}

		item.setPaymentType(paymentType);
		paymentList.add(item);
		info.setPaymentList(paymentList);
		info.setReceiptAmount(dnr.getAmount());
		info.setReceiptDate(dnr.getInputTime());


		info.setReceiptNo(dnr.getReceiptNo());
		req.setReceiptInfo(info);

		dnsOutService.sendReceipt(req);
	}
	private void simulateCancel(DemandNoteHeader header) {
		ControlData controlData = new ControlData();
		controlData.setEntity(ControlEntity.DN.getCode());
		controlData.setAction(ControlAction.CANCEL.getCode());
		controlData.setEntityId(header.getAdjustReason());
		controlData = controlDataDao.save(controlData);
		//dnsService.createDemandNote(header.getAdjustReason(), , true);

		List<org.mardep.ssrs.domain.dn.DemandNoteItem> demandNoteItemList = demandNoteItemDao.findByDemandNoteNo(header.getDemandNoteNo());
		DemandNoteRequest request = new DemandNoteRequest();
		//Header info
		request.setAction(Action.C);
		request.setControlId(controlData.getId().toString());
		RequestFile rf = new RequestFile();
		rf.setdFile(controlData.getFile());
		request.setFile(rf);

		//Body Info
		DemandNoteInfo info = new DemandNoteInfo();
		info.setDnNo(header.getAdjustReason());
		info.setLastUpdateDatetime(new Date());
		info.setUserCode("DM");
		info.setIssueDate(DateUtils.truncate(header.getGenerationTime(), Calendar.DATE));
		info.setAmountTTL(header.getAmount());
		info.setBillCode(Cons.DNS_BILL_CODE);
		String officeCode = header.getApplNo() != null && header.getApplNo().length() > 1 ? Cons.SSRS_SR_OFFICE_CODE : Cons.SSRS_MMO_OFFICE_CODE;
		info.setOfficeCode(officeCode);
		info.setPayerName(header.getBillName() != null && header.getBillName().length() > 120 ? header.getBillName().substring(0,  120) : header.getBillName());
		info.setRemarks(header.getCwRemark());

		boolean autopay = false;
		if (header.getEbsFlag() == null) {
		} else {
			switch (header.getEbsFlag()) {
			case "1": // autopay
				autopay = true;
				break;
			}
		}
		info.setAutopayRequest(autopay ? 1 : 0);
		info.setIsAutopay(autopay ? 1 : 0);
		info.setDueDate(header.getDueDate());
		info.setDnStatus(12);
		//Charge Items
		List<DemandNoteItem> itemList = new ArrayList<DemandNoteItem>();
		int[] idx = {0};
		demandNoteItemList.forEach(dni -> {
			idx[0]++;
			itemList.add(construct(idx[0], dni));
		});

		info.setItemList(itemList);
		request.setDemandNoteInfo(info);
		DemandNoteResponse result = dnsOutService.sendDemandNote(request);
	}

	private void sendDemandNote(DemandNoteHeader header, String officeCode, String demandNoteNo, ControlData newCD,
			List<org.mardep.ssrs.domain.dn.DemandNoteItem> demandNoteItemList) {
		DemandNoteRequest request = new DemandNoteRequest();
		//Header info
		request.setAction(Action.U);
		request.setControlId(Long.toString(newCD.getId()));
		RequestFile rf = new RequestFile();
		rf.setdFile(newCD.getFile());
		request.setFile(rf);

		//Body Info
		DemandNoteInfo info = new DemandNoteInfo();
		info.setDnNo(demandNoteNo);
		info.setLastUpdateDatetime(header.getUpdatedDate());
		info.setUserCode(header.getUpdatedBy());
		info.setIssueDate(DateUtils.truncate(header.getGenerationTime(), Calendar.DATE));
		info.setAmountTTL(header.getAmount());
		info.setBillCode(Cons.DNS_BILL_CODE);
		info.setOfficeCode(officeCode);
		info.setPayerName(header.getBillName() != null && header.getBillName().length() > 120 ? header.getBillName().substring(0,  120) : header.getBillName());
		info.setRemarks(header.getCwRemark());
		info.setAutopayRequest(0);
		info.setIsAutopay(0);
		info.setDueDate(header.getDueDate());
		info.setDnStatus(DemandNoteHeader.STATUS_WRITTEN_OFF.equals(header.getCwStatus()) ? /*written-off status (i.e. dnStatus = 11)*/11 : 3);
		//Charge Items
		List<DemandNoteItem> itemList = new ArrayList<DemandNoteItem>();
		int[] idx = {0};
		demandNoteItemList.forEach(dni -> {
			idx[0]++;
			itemList.add(construct(idx[0], dni));
		});

		info.setItemList(itemList);
		request.setDemandNoteInfo(info);
		DemandNoteResponse result = dnsOutService.sendDemandNote(request);
		if (result.getBaseResult() != null) {
			// TODO
		}
	}

	private DemandNoteItem construct(int idx, org.mardep.ssrs.domain.dn.DemandNoteItem dni){
		DemandNoteItem item = new DemandNoteItem();
    	item.setItemNo(idx);
    	item.setIsRemark(0);
    	if(dni.getFcFeeCode()!=null && dni.getFeeCode()!=null){
    		FeeCode feeCode = dni.getFeeCode();
    		item.setParticular(feeCode.getEngDesc());
    		item.setRevenueType(feeCode.getFormCode());
    		item.setFeeCode(feeCode.getId());
    		item.setUnitPrice(feeCode.getFeePrice());
    	}
    	item.setUnit(dni.getChargedUnits());
    	item.setAmount(dni.getAmount());
    	return item;
	}

	@Override
	public void retry(String oldDemandNoteNo) {

		DemandNoteHeader header = dns.findById(DemandNoteHeader.class, oldDemandNoteNo);
		String original = header.getDemandNoteNo();
		if (original.length() == 15) {
			return;
		}
		DemandNoteReceipt receiptCriteria = new DemandNoteReceipt();
		receiptCriteria.setDemandNoteNo(original);
		List<DemandNoteReceipt> receipts = dns.findByCriteria(receiptCriteria);
		boolean send = false;
		if (receipts.isEmpty()) {
			send = true;
		}
		if (!send) {
			for (DemandNoteReceipt receipt : receipts) {
				if (!receipt.getInputTime().before(start) /*inclusive*/ && receipt.getInputTime().before(end)/*exclusive*/) {
					send = true;
					break;
				}
			}
			if (!send) {
				BigDecimal received = BigDecimal.ZERO;
				for (DemandNoteReceipt receipt : receipts) {
					if (receipt.getCanAdjStatus() == null) {
						received = received.add(receipt.getAmount());
					}
				}
				if (!received.equals(header.getAmount())) {
					send = true;
				}
			}
		}

		if (!send) {
			System.out.println("not send " + original);
			return;
		}
		System.out.println("send " + original);

		String officeCode = header.getApplNo() != null && header.getApplNo().length() > 1 ? Cons.SSRS_SR_OFFICE_CODE : Cons.SSRS_MMO_OFFICE_CODE;
		// create new demand note no. (length 15)
		String demandNoteNo = header.getAdjustReason();
		ControlData controlData = new ControlData();
		controlData.setEntity(ControlEntity.DN.getCode());
		controlData.setAction(ControlAction.CREATE.getCode());
		controlData.setEntityId(demandNoteNo);

		ControlData newCD = controlDataDao.save(controlData);
		List<org.mardep.ssrs.domain.dn.DemandNoteItem> demandNoteItemList = demandNoteItemDao.findByDemandNoteNo(original);
		try {
			sendDemandNote(header, officeCode, demandNoteNo, newCD, demandNoteItemList);
		} catch (Exception e) {
			System.out.println("sendDemandNote exception " + e.getMessage());
			return;
		}

		header.setAdjustReason(demandNoteNo);
		header.setAdjustTime(adjustTime);
		headerDao.save(header);

		for (DemandNoteReceipt dnr : receipts) {

			ReceiptRequest req = new ReceiptRequest();
			ControlData cd = new ControlData();
			cd.setEntity(ControlEntity.RECEIPT.getCode());
			cd.setAction(ControlAction.CREATE.getCode());
			cd.setEntityId(dnr.getReceiptNo());
			cd = controlDataDao.save(cd);
			req.setControlId(Long.toString(cd.getId()));

			req.setCreateReceiptAction(CreateReceiptAction.U);
			CreateReceiptInfo info = new CreateReceiptInfo();
			info.setBillCode("05"); // SSRS
			info.setDnNo(demandNoteNo);
			info.setMachineID("");
			ArrayList<ReceiptItem> paymentList = new ArrayList<>();
			ReceiptItem item = new ReceiptItem();
			item.setPaymentAmount(dnr.getAmount());
			String paymentType;
			if (header.getEbsFlag() == null) {
				paymentType = "10"; // cash
			} else {
				switch (header.getEbsFlag()) {
				case "2":
					paymentType = "10"; // 10-CASH
					break;
				case "6":
					paymentType = "90";
					break;
				case "1": // autopay
					paymentType = "70";
					break;
				case "5": // TODO unknown
					paymentType = "10"; // cash
					break;
				default:
					paymentType = "50"; // credit card
					break;
				}
			}

			item.setPaymentType(paymentType);
			paymentList.add(item);
			info.setPaymentList(paymentList);
			info.setReceiptAmount(dnr.getAmount());
			info.setReceiptDate(dnr.getInputTime());


			info.setReceiptNo(dnr.getReceiptNo());
			req.setReceiptInfo(info);

			try {
				dnsOutService.sendReceipt(req);
			} catch (Exception e) {
				System.out.println("error : dnsOutService.sendReceipt  " + dnr.getReceiptNo() + " " + header.getDemandNoteNo());
				continue;
			}
			if (dnr.getCanAdjStatus() != null) {
				try {
					cancel(demandNoteNo, header, dnr);
				} catch (Exception e) {
					continue;
				}
			}
		}
		if (header.getStatus() != null) {
			switch (header.getStatus()) {
			case DemandNoteHeader.STATUS_CANCELLED:
				simulateCancel(header);
				break;
			case DemandNoteHeader.STATUS_WRITTEN_OFF:
				// TODO
				break;
			}
		}
	}

}
