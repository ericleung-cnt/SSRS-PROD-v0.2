package org.mardep.ssrs.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.mardep.ssrs.dao.Criteria;
import org.mardep.ssrs.dao.Operator;
//import org.mardep.ssrs.dao.dn.DemandNoteRefundJpaDao;
import org.mardep.ssrs.dao.dn.IDemandNoteHeaderDao;
import org.mardep.ssrs.dao.dn.IDemandNoteItemDao;
import org.mardep.ssrs.dao.dn.IDemandNoteReceiptDao;
import org.mardep.ssrs.dao.dn.IDemandNoteRefundDao;
import org.mardep.ssrs.dao.dns.IControlDataDao;
import org.mardep.ssrs.dao.sr.IRegMasterDao;
//import org.mardep.ssrs.dns.pojo.inbound.updateRefundStatus.RefundStatus;
import org.mardep.ssrs.domain.constant.Cons;
import org.mardep.ssrs.domain.constant.DemandNoteRefundStatus;
import org.mardep.ssrs.domain.constant.ReceiptStatus;
//import org.mardep.ssrs.domain.dn.DemandNoteAging;
import org.mardep.ssrs.domain.dn.DemandNoteHeader;
import org.mardep.ssrs.domain.dn.DemandNoteItem;
import org.mardep.ssrs.domain.dn.DemandNoteReceipt;
import org.mardep.ssrs.domain.dn.DemandNoteRefund;
import org.mardep.ssrs.domain.dns.ControlAction;
import org.mardep.ssrs.domain.dns.ControlData;
import org.mardep.ssrs.domain.dns.ControlEntity;
import org.mardep.ssrs.domain.sr.RegMaster;
import org.mardep.ssrs.domain.user.User;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.mardep.ssrs.report.IDemandNoteGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;


/**
 * <li>PRG-MMO-022
 * <li>PRG-MMO-023
 * <li>PRG-MMO-024
 * <li>PRG-FIN-001
 * <li>PRG-FIN-002
 * <li>PRG-FIN-003
 * <li>PRG-FIN-004
 *
 *
 * @author Leo.LIANG
 *
 */
@Service("demandNoteService")
@Transactional
public class DemandNoteService extends AbstractService implements IDemandNoteService{

	@Autowired
	IDemandNoteHeaderDao demandNoteHeaderDao;

	@Autowired
	IDemandNoteItemDao demandNoteItemDao;

	@Autowired
	IDemandNoteReceiptDao demandNoteReceiptDao;

	@Autowired
	IDemandNoteRefundDao demandNoteRefundDao;

	@Autowired
	IControlDataDao controlDataDao;

	@Autowired
	@Qualifier("demandNoteGenerator")
	IDemandNoteGenerator demandNoteGenerator;

	@Autowired
	IRegMasterDao rmDao;

	private int dayCountDownForAtcDnItem = Integer.parseInt(System.getProperty("DemandNoteService.dayCountDownForAtcDnItem", "30"));

	/**
	 * Enquire DN(PRG-FIN-002)
	 *
	 * @param dnHeader
	 * @return
	 */
	@Override
	public DemandNoteHeader enquireDemandNoteDetail(String demandNoteNo){
		return demandNoteHeaderDao.findById(demandNoteNo);
	}

	/**
	 * TODO
	 * @param pk
	 * @return
	 */
	@Override
	public DemandNoteHeader cancel(String demandNoteNo, String cwRemark){
		String currentUser = UserContextThreadLocalHolder.getCurrentUserName();
		DemandNoteHeader dnhHeader = demandNoteHeaderDao.findById(demandNoteNo);
		dnhHeader.setCwStatus(Cons.CW_STATUS_C);
		dnhHeader.setCwBy(currentUser);
		dnhHeader.setCwTime(new Date());
		dnhHeader.setCwRemark(cwRemark);
		dnhHeader.setStatus(DemandNoteHeader.STATUS_CANCELLED);
		DemandNoteHeader dnh = demandNoteHeaderDao.save(dnhHeader);
		ControlData controlData = new ControlData();
		controlData.setEntity(ControlEntity.DN.getCode());
		controlData.setAction(ControlAction.CANCEL.getCode());
		controlData.setEntityId(demandNoteNo);
		ControlData newCD = controlDataDao.save(controlData);
		return dnh;
	}

	@Override
	public DemandNoteRefund refund(String demandNoteNo, BigDecimal refundAmount, String remarks){
		DemandNoteHeader dnhHeader = demandNoteHeaderDao.findById(demandNoteNo);
		if(dnhHeader==null) return null;

		dnhHeader.setStatus(DemandNoteHeader.STATUS_REFUNDED);
		demandNoteHeaderDao.save(dnhHeader);

		DemandNoteRefund refund = new DemandNoteRefund();
		refund.setDemandNoteNo(demandNoteNo);
		refund.setRefundAmount(refundAmount);
		refund.setRemarks(remarks);
		DemandNoteRefund dnr = demandNoteRefundDao.save(refund);


		ControlData controlData = new ControlData();
		controlData.setEntity(ControlEntity.REFUND.getCode());
		controlData.setAction(ControlAction.REFUND.getCode());
		controlData.setEntityId(dnr.getId().toString());
		controlDataDao.save(controlData);

//		DemandNoteHeader dnh = demandNoteHeaderDao.save(dnhHeader);
//		dnsService.create(dnh.getDemandNoteNo(), ControlEntity.REFUND, ControlAction.REFUND);
		return dnr;
	}

	@Override
	public DemandNoteRefund recommendRefund(String demandNoteNo, BigDecimal refundAmount, String remarks) {
//		DemandNoteHeader dnhHeader = demandNoteHeaderDao.findById(demandNoteNo);
//		if(dnhHeader==null) return null;

//		dnhHeader.setStatus(DemandNoteHeader.STATUS_REFUNDED);
//		demandNoteHeaderDao.save(dnhHeader);

		DemandNoteRefund refund = new DemandNoteRefund();
		refund.setDemandNoteNo(demandNoteNo);
		refund.setRefundAmount(refundAmount);
		refund.setRemarks(remarks);
		refund.setRefundStatus(DemandNoteRefundStatus.RECOMMENDED.toString());
		DemandNoteRefund dnr = demandNoteRefundDao.save(refund);

//		ControlData controlData = new ControlData();
//		controlData.setEntity(ControlEntity.REFUND.getCode());
//		controlData.setAction(ControlAction.REFUND.getCode());
//		controlData.setEntityId(dnr.getId().toString());
//		controlDataDao.save(controlData);

		return dnr;
	}

	@Override
	public DemandNoteRefund confirmRecommendRefund(DemandNoteRefund entity) {
		Date recommendedDate = new Date();
		String currentUser = UserContextThreadLocalHolder.getCurrentUserName();

		entity.setRefundStatus(DemandNoteRefundStatus.SUBMITTED.toString());
		entity.setRecommendedDate(recommendedDate);
		entity.setUserCode(currentUser);
		DemandNoteRefund dnr = demandNoteRefundDao.save(entity);

		ControlData controlData = new ControlData();
		controlData.setEntity(ControlEntity.REFUND.getCode());
		controlData.setAction(ControlAction.REFUND.getCode());
		controlData.setEntityId(dnr.getId().toString());
		controlDataDao.save(controlData);

		return dnr;
	}

	@Override
	public DemandNoteItem addItem(DemandNoteItem item){
		String dnNo = item.getDnDemandNoteNo();
		logger.info("'#AddDemandNote Item, DN No.:{}", dnNo);
		DemandNoteHeader dnHeader = null;
		if (dnNo != null) {
			dnHeader = demandNoteHeaderDao.findById(dnNo);
			item.setApplNo(dnHeader.getApplNo());
		}

		item.setChgIndicator("Y"); //TODO
		if (item.getGenerationTime() == null) {
			item.setGenerationTime(new Date());
		}
		String currentUser = UserContextThreadLocalHolder.getCurrentUserName();
		item.setUserId(currentUser);

		DemandNoteItem dnItem = demandNoteItemDao.save(item);

		if (dnHeader != null) {
			List<DemandNoteItem> itemList = demandNoteItemDao.findByDemandNoteNo(dnNo);
			BigDecimal totalAmount = BigDecimal.ZERO;
			for(DemandNoteItem dbDnItem:itemList){
				BigDecimal itemAmount = dbDnItem.getAmount();
				totalAmount = totalAmount.add(itemAmount);
			}

			dnHeader.setAmount(totalAmount);
			demandNoteHeaderDao.save(dnHeader);
		}

		return dnItem;
	}

	@Override
	public void removeItem(Long itemId, String reason){
		logger.info("'#Delete DemandNote Item, ItemID:{}", itemId);
		DemandNoteItem item = demandNoteItemDao.findById(itemId);
		item.setActive(false);
		item.setDeleteReson(reason);
		demandNoteItemDao.save(item);
	}

	private RegMaster getShipRegEntity(String applNo) {
		RegMaster rm = rmDao.findById(applNo);
		return rm;
	}

	@Override
	public DemandNoteHeader create(DemandNoteHeader header, String officeDnCode, boolean autopay) throws Exception{
		String demandNoteNo = getDemandNoteNumber(Cons.DNS_BILL_CODE, officeDnCode);
		Date generationDate = new Date();
		header.setDemandNoteNo(demandNoteNo);
		header.setAmount(BigDecimal.ZERO);
		header.setGenerationTime(generationDate);
		if (header.getDueDate()==null) {
			header.setDueDate(generationDate);
		}
		RegMaster rm = getShipRegEntity(header.getApplNo());
		if (rm!=null) {
			header.setShipNameEng(rm.getRegName());
			header.setShipNameChi(rm.getRegChiName());
			header.setGrossTon(rm.getGrossTon());
			header.setNetTon(rm.getRegNetTon());
		}

		DemandNoteHeader dnHeader = demandNoteHeaderDao.save(header);

		List<DemandNoteItem> diList = new ArrayList<DemandNoteItem>();
		long idx = 0;
		for(DemandNoteItem item:header.getDemandNoteItems()){
			idx++;
			item.setItemNo(idx);
			item.setDnDemandNoteNo(demandNoteNo);
			item.setDemandNoteHeader(dnHeader);
			DemandNoteItem dnItem = addItem(item);
			diList.add(dnItem);
		};
		dnHeader.setDemandNoteItems(diList);

		ControlData controlData = new ControlData();
		controlData.setEntity(ControlEntity.DN.getCode());
		controlData.setAction(ControlAction.CREATE.getCode());
		controlData.setEntityId(demandNoteNo);

		byte[] osContent = demandNoteGenerator.generate(demandNoteNo, autopay);
		controlData.setFile(osContent);
//		try {
//			Map<String, Object> inputParam = new HashMap<String, Object>();
//			inputParam.put("demandNoteNo", demandNoteNo);
//			byte[] dn = demandNoteGenerator.generate(inputParam);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		ControlData newCD = controlDataDao.save(controlData);
//		ControlData dnsResp = dnsService.create(dnHeader.getDemandNoteNo(), ControlEntity.DN, ControlAction.CREATE);
//		logger.info("dns processed {} {}", header.getDemandNoteNo(), dnsResp.getProcessed());

		return dnHeader;
	}
	@Override
	public DemandNoteHeader create(DemandNoteHeader header, boolean autopay) throws Exception{
//		String demandNoteNo = generateNextDemandNoteNo();
		logger.info("#Create DN Header");
		String officeCode = header.getApplNo() != null && header.getApplNo().length() > 1 ? Cons.SSRS_SR_OFFICE_CODE : Cons.SSRS_MMO_OFFICE_CODE;
		DemandNoteHeader dnHeader = create(header, officeCode, autopay);
		return dnHeader;
//		String demandNoteNo = getDemandNoteNumber(Cons.DNS_BILL_CODE, officeCode);
//		Date generationDate = new Date();
//		header.setDemandNoteNo(demandNoteNo);
//		header.setAmount(BigDecimal.ZERO);
//		header.setGenerationTime(generationDate);
//		if (header.getDueDate()==null) {
//			header.setDueDate(generationDate);
//		}
//		RegMaster rm = getShipRegEntity(header.getApplNo());
//		if (rm!=null) {
//			header.setShipNameEng(rm.getRegName());
//			header.setShipNameChi(rm.getRegChiName());
//			header.setGrossTon(rm.getGrossTon());
//			header.setNetTon(rm.getRegNetTon());
//		}
//
//		DemandNoteHeader dnHeader = demandNoteHeaderDao.save(header);
//
//		List<DemandNoteItem> diList = new ArrayList<DemandNoteItem>();
//		long idx = 0;
//		for(DemandNoteItem item:header.getDemandNoteItems()){
//			idx++;
//			item.setItemNo(idx);
//			item.setDnDemandNoteNo(demandNoteNo);
//			item.setDemandNoteHeader(dnHeader);
//			DemandNoteItem dnItem = addItem(item);
//			diList.add(dnItem);
//		};
//		dnHeader.setDemandNoteItems(diList);
//
//		ControlData controlData = new ControlData();
//		controlData.setEntity(ControlEntity.DN.getCode());
//		controlData.setAction(ControlAction.CREATE.getCode());
//		controlData.setEntityId(demandNoteNo);
//
//		byte[] osContent = demandNoteGenerator.generate(demandNoteNo, autopay);
//		controlData.setFile(osContent);
////		try {
////			Map<String, Object> inputParam = new HashMap<String, Object>();
////			inputParam.put("demandNoteNo", demandNoteNo);
////			byte[] dn = demandNoteGenerator.generate(inputParam);
////		} catch (Exception e) {
////			e.printStackTrace();
////		}
//		ControlData newCD = controlDataDao.save(controlData);
////		ControlData dnsResp = dnsService.create(dnHeader.getDemandNoteNo(), ControlEntity.DN, ControlAction.CREATE);
////		logger.info("dns processed {} {}", header.getDemandNoteNo(), dnsResp.getProcessed());
//
//		return dnHeader;
	}

	@Override
	public String getDemandNoteNumber(String billCode, String officeCode)
	{
		Calendar now = Calendar.getInstance();
		String year = String.valueOf(now.get(Calendar.YEAR));

		Long seqNo = demandNoteHeaderDao.findNextId();

		String yearString = String.format("%02d", Integer.parseInt(year) % 100);
		String dnNumber = "";

		dnNumber += billCode.substring(0, 2);
		dnNumber += officeCode.substring(0, 2);
		dnNumber += "00";
		dnNumber += String.format("%06d", seqNo);

		dnNumber += yearString;

		char[] dnNumArray = dnNumber.toCharArray();

		int sum_of_1357 = (dnNumArray[0] - '0')
						+ (dnNumArray[2] - '0')
						+ (dnNumArray[4] - '0')
						+ (dnNumArray[6] - '0')
						+ (dnNumArray[8] - '0')
						+ (dnNumArray[10] - '0')
						+ (dnNumArray[12] - '0');

		int sum_of_2468 = (dnNumArray[1] - '0')
						+ (dnNumArray[3] - '0')
						+ (dnNumArray[5] - '0')
						+ (dnNumArray[7] - '0')
						+ (dnNumArray[9] - '0')
						+ (dnNumArray[11] - '0')
						+ (dnNumArray[13] - '0');

		sum_of_2468 = sum_of_2468 * 3;
		int modChk = (sum_of_1357 + sum_of_2468) % 10;
		int result_chk = 0;
		if (modChk != 0)
			result_chk = java.lang.Math.abs((sum_of_1357 + sum_of_2468) % 10 - 10);
		dnNumber += String.valueOf(result_chk);

		logger.info("Generate DN No [{}] In year [{}]", new Object[]{dnNumber});
		return dnNumber;
	}

	@Override
	public List<DemandNoteItem> findSrDnItems() {
		return demandNoteItemDao.findSrDnItems();
	}

	@Override
	public List<DemandNoteHeader> findSrDn(Map criteria, long start, long end) {
		return demandNoteHeaderDao.findSrDn(criteria, start, end);
	}

	@Override
	public List<DemandNoteReceipt> findValue(String demandNoteNo) {
		return demandNoteReceiptDao.findValue(demandNoteNo);
	}
	@Override
	public long countSrDn(Map criteria) {
		return demandNoteHeaderDao.countSrDn(criteria);
	}

	@Override
	public List<DemandNoteItem> findUnusedByAppl(String applNo) {
		return demandNoteItemDao.findUnusedByApplNo(applNo);
	}
	@Override
	public void createAtcItem() {
		User user = new User();
		user.setId("SYSTEM");
		UserContextThreadLocalHolder.setCurrentUser(user);
		List<RegMaster> resultList = new ArrayList<>();
		Map<String, Criteria> map = new HashMap<String, Criteria>();

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		Date today0000;
		try {
			today0000 = simpleDateFormat.parse(simpleDateFormat.format(new Date()));
			//today0000 = simpleDateFormat.parse("20191212");
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}


		Calendar cal = Calendar.getInstance();
		cal.setTime(today0000);
		cal.add(Calendar.DAY_OF_MONTH, dayCountDownForAtcDnItem);
		Date from = cal.getTime();
		cal.add(Calendar.DAY_OF_MONTH, 1);
		cal.add(Calendar.MILLISECOND, - 1);
		Date to = cal.getTime();
		cal.setTime(from);
		cal.add(Calendar.YEAR, 1);
		Date newDue = cal.getTime();

		map.put("atfDueDateFrom", new Criteria("atfDueDate", from, Operator.GREATER_OR_EQUAL));
		map.put("atfDueDateTo", new Criteria("atfDueDate", to, Operator.LESS_OR_EQUAL));
		map.put("regStatus", new Criteria("regStatus", "R"));
		rmDao.findByPaging(map, null, null, resultList);
		createAtcDni(resultList, newDue);
	}

//	@Override
//	public void createAtcDni(List<RegMaster> resultList, Date dueDate) {
//		Date generationTime = new Date();
//		Map<String, BigDecimal> calculated = rmDao.calculateAtc(resultList.stream().map(rm -> { return rm.getApplNo(); } ).toArray(String[]::new));
//		for (RegMaster rm : resultList) {
//			DemandNoteItem item = new DemandNoteItem();
//			item.setActive(Boolean.TRUE);
//			BigDecimal amount = calculated.get(rm.getApplNo());
//			// give discount
//			// check regdate/detain for 2 years
//			// for every 2 year against reg date/detain date
//			Date compare = (rm.getDetainDate() != null) ? rm.getDetainDate() : rm.getRegDate();
//			int yeardiff = (int) Math.ceil((generationTime.getTime() - compare.getTime()) / 365.25 / 24 /3600 / 1000);
//			boolean discount = yeardiff > 2 && yeardiff % 2 == 1; // for year 3, 5, 7, etc..
//			if (discount) { // discount
//				amount = amount.multiply(new BigDecimal("0.5"));
//			}
//			item.setAmount(amount);
//
//			item.setApplNo(rm.getApplNo());
//			item.setChargedUnits(1);
//			item.setChgIndicator("Y");
//			item.setFcFeeCode("01");
//			item.setGenerationTime(generationTime);
//			item.setUserId("SYSTEM");
//			demandNoteItemDao.save(item);
//			if (dueDate != null) {
//				RegMaster update = rmDao.findById(rm.getApplNo());
//				update.setAtfDueDate(dueDate);
//				rmDao.save(update);
//			}
//			logger.info("ATC demand note item appl no {}, discount {}, amount {}, new Due Date {}",  rm.getApplNo(), discount, amount, dueDate);
//		}
//	}

	private BigDecimal getLastATC(String applNo) {
		DemandNoteItem dnItem = demandNoteItemDao.getLastAtc(applNo);
		if (dnItem!=null) {
			return dnItem.getAmount();
		} else {
			return new BigDecimal(0.00);
		}
	}

	@Override
	public void createAtcDni(List<RegMaster> resultList, Date dueDate) {
		DemandNoteAtcService atcSvc = new DemandNoteAtcService();
		Date generationTime = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(dueDate);
		int year = cal.get(Calendar.YEAR);
		Map<String, BigDecimal> calculated = rmDao.calculateAtc(resultList.stream().map(rm -> { return rm.getApplNo(); } ).toArray(String[]::new));
		for (RegMaster rm : resultList) {
			DemandNoteItem item = new DemandNoteItem();
			item.setActive(Boolean.TRUE);
			BigDecimal amount = calculated.get(rm.getApplNo());
			// give discount
			// check regdate/detain for 2 years
			// for every 2 year against reg date/detain date

//			Date compare = (rm.getDetainDate() != null) ? rm.getDetainDate() : rm.getRegDate();
//			int yeardiff = (int) Math.ceil((generationTime.getTime() - compare.getTime()) / 365.25 / 24 /3600 / 1000);
//			boolean discount = yeardiff > 2 && yeardiff % 2 == 1; // for year 3, 5, 7, etc..
//			if (discount) { // discount
//				amount = amount.multiply(new BigDecimal("0.5"));
//			}
			BigDecimal calcATC;
			BigDecimal lastATC = getLastATC(rm.getApplNo());
			//calcATC = atcSvc.calcAtcAmt(rm.getRegDate(), rm.getDetainDate(), generationTime, amount, lastATC);
			calcATC = atcSvc.calcAtcAmt(rm.getRegDate(), rm.getDetainDate(), dueDate, amount, lastATC);
			boolean discounted = !calcATC.equals(amount);

			item.setAmount(calcATC);

			item.setApplNo(rm.getApplNo());
			item.setChargedUnits(1);
			item.setChgIndicator("Y");
			item.setFcFeeCode("01");
			item.setGenerationTime(generationTime);
			item.setUserId("SYSTEM");
			item.setAdhocDemandNoteText((discounted ? "50" : "100" ) + "% (Year " + (year - 1) + "-" + year + ")");
			demandNoteItemDao.save(item);
			if (dueDate != null) {
				RegMaster update = rmDao.findById(rm.getApplNo());
				update.setAtfDueDate(dueDate);
				rmDao.save(update);
			}
			logger.info("ATC demand note item appl no {}, discount {}, amount {}, new Due Date {}",  rm.getApplNo(), discounted, calcATC, dueDate);
		}
	}

	@Override
	public DemandNoteHeader getDemandNoteHeader(String demandNoteNo) {
		DemandNoteHeader header = demandNoteHeaderDao.findById(demandNoteNo);
		return header;
	}

	@Override
	public List<DemandNoteReceipt> getDemandNoteReceipts(String demandNoteNo){
		List<DemandNoteReceipt> receipts = demandNoteReceiptDao.findByDemandNoteNo(demandNoteNo);
		return receipts;
	}

	@Override
	public List<DemandNoteRefund> getDemandNoteRefunds(String demandNoteNo){
		List<DemandNoteRefund> refunds = demandNoteRefundDao.findByDemandNoteNo(demandNoteNo);
		return refunds;
	}

	@Override
	public double getRefundAvailability(String demandNoteNo, String refundId) {
		DemandNoteHeader header = getDemandNoteHeader(demandNoteNo);
		List<DemandNoteReceipt> receipts = getDemandNoteReceipts(demandNoteNo);
		List<DemandNoteRefund> refunds = getDemandNoteRefunds(demandNoteNo);

		return getPossibleRefundAmt(refundId, header, receipts, refunds);

	}

	@Override
	public double getPossibleRefundAmt(String refundId, DemandNoteHeader header, List<DemandNoteReceipt> receipts, List<DemandNoteRefund> refunds) {
		double calcAmt = 0;
		double receiptAmt = 0;
		double refundAmt = 0;

		double demandNoteAmt = header.getAmount().doubleValue();

		if (receipts.size()>0) {
			for (DemandNoteReceipt r:receipts){
				if (r.getCanAdjStatus()==null) {
						//!r.getCanAdjStatus().equals(ReceiptStatus.CANCELLED.getCode())) {
					receiptAmt += r.getAmount().doubleValue();
				}
			}
		}

		// case 1: recommend to refund
		// status reject: not count into calc possible refund amt
		// status approved:
		//        recommended:
		//        submitted: count into calc possible refund amt
		// case 2: submit refund
		// status reject: count into calc possible refund amt
		// status approved:
		//        recommended:
		//        submitted: not count into calc possible refund amt
		if(refunds.size()>0) {
			if (refundId==null || refundId.isEmpty()) {
				for (DemandNoteRefund r:refunds) {
					if (r.getRefundStatus()!=null
							&& !r.getRefundStatus().equals(DemandNoteRefundStatus.REJECTED.toString())) {
						refundAmt += r.getRefundAmount().doubleValue();
					}
				}
			} else {
				for (DemandNoteRefund r:refunds) {
					if (r.getRefundStatus()!=null
							&& r.getRefundStatus().equals(DemandNoteRefundStatus.APPROVED.toString())) {
						refundAmt += r.getRefundAmount().doubleValue();
					}
				}
			}

		}

		calcAmt = receiptAmt - refundAmt;

		return calcAmt;
	}

	/**
	 * Set status, amountPaid, paymentStatus
	 * @param header
	 * @param receipts
	 * @param refunds
	 * @return
	 */
	public static String processDnStates(DemandNoteHeader header, List<DemandNoteReceipt> receipts, List<DemandNoteRefund> refunds) {
		BigDecimal amountPaid = BigDecimal.ZERO;
		boolean receiptCancelled = false;
		boolean hasRefund = false;
		for (DemandNoteReceipt receipt : receipts) {
			if (!ReceiptStatus.PENDING.getCode().equals(receipt.getStatus())) {
				if (receipt.getCanAdjStatus() == null) {
					amountPaid = amountPaid.add(receipt.getAmount());
				} else {
					receiptCancelled = true;
				}
			}
		}
		for (DemandNoteRefund refund : refunds) {
			if (DemandNoteRefundStatus.APPROVED.toString().equals(refund.getRefundStatus())) {
				hasRefund = true;
				amountPaid = amountPaid.subtract(refund.getRefundAmount());
			}
		}
		BigDecimal headerAmt = header.getAmount();
		if (headerAmt == null) {
			headerAmt = BigDecimal.ZERO;
		}
		header.setAmountPaid(amountPaid);
		if (!receiptCancelled && hasRefund) {
			header.setStatus(DemandNoteHeader.STATUS_REFUNDED);
		}

//		if (receipt amount + refund amount == 0 ) {
//		    if (has cancel receipt or ebs flag != "1")
//		    then "outstanding"
//		    else "auto pay arrange"
//		} else if (amount - receipt amount + refund amount < 0) {
//		    "overpaid"
//		} else {
//		    if (amount - receipt amount + refund amount > 0) {
//		    then "partial"
//		    else "paid"
//		}
		if (amountPaid.doubleValue() == 0 ) {
		    if (receiptCancelled || !"1".equals(header.getEbsFlag())) {
		    	header.setPaymentStatus(DemandNoteHeader.PAYMENT_STATUS_OUTSTANDING);
		    } else {
		    	header.setPaymentStatus(DemandNoteHeader.PAYMENT_STATUS_AUTOPAY_ARRANGED);
		    }
		} else if (headerAmt.subtract(amountPaid).doubleValue() < 0) {
	    	header.setPaymentStatus(DemandNoteHeader.PAYMENT_STATUS_OVERPAID);
		} else {
			if (headerAmt.subtract(amountPaid).doubleValue() > 0) {
		    	header.setPaymentStatus(DemandNoteHeader.PAYMENT_STATUS_PARTIAL);
			} else {
		    	header.setPaymentStatus(DemandNoteHeader.PAYMENT_STATUS_PAID);
			}
		}

		return header.getPaymentStatus();
	}

	@Override
	public DemandNoteItem saveAdjustAtcAmt(DemandNoteItem entity) {
		DemandNoteItem savedItem = demandNoteItemDao.save(entity);
		return savedItem;
	}
	
	@Override
	public List<DemandNoteItem> findByDemandNoteNo(String demandNoteNo){
		List<DemandNoteItem> items = demandNoteItemDao.findByDemandNoteNo(demandNoteNo);
		return items;
	}
}
