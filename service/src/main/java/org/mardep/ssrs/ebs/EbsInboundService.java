package org.mardep.ssrs.ebs;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.envers.exception.RevisionDoesNotExistException;
import org.mardep.ssrs.dao.codetable.IFeeCodeDao;
import org.mardep.ssrs.dao.codetable.IShipTypeDao;
import org.mardep.ssrs.dao.codetable.ISystemParamDao;
import org.mardep.ssrs.dao.dn.IDemandNoteItemDao;
import org.mardep.ssrs.dao.dn.IDemandNoteReceiptDao;
import org.mardep.ssrs.dao.sr.IEbsTranscriptDao;
import org.mardep.ssrs.dao.sr.IRegMasterDao;
import org.mardep.ssrs.dao.sr.IRepresentativeDao;
import org.mardep.ssrs.dao.user.ITransactionLockDao;
import org.mardep.ssrs.dns.pojo.common.CreateReceiptAction;
import org.mardep.ssrs.dns.pojo.common.ReceiptInfo;
import org.mardep.ssrs.dns.pojo.common.ReceiptItem;
import org.mardep.ssrs.dns.pojo.inbound.createReceipt.ReceiptStatus;
import org.mardep.ssrs.dns.pojo.inbound.createReceipt.ReceiptStatusRequest;
import org.mardep.ssrs.dns.pojo.inbound.createReceipt.ReceiptStatusResponse;
import org.mardep.ssrs.dns.pojo.outbound.createDemandNote.Action;
import org.mardep.ssrs.dns.service.inbound.IReceiptService;
import org.mardep.ssrs.domain.codetable.FeeCode;
import org.mardep.ssrs.domain.codetable.ShipType;
import org.mardep.ssrs.domain.codetable.SystemParam;
import org.mardep.ssrs.domain.dn.DemandNoteHeader;
import org.mardep.ssrs.domain.dn.DemandNoteItem;
import org.mardep.ssrs.domain.dn.DemandNoteReceipt;
import org.mardep.ssrs.domain.sr.EbsTranscript;
import org.mardep.ssrs.domain.sr.RegMaster;
import org.mardep.ssrs.domain.sr.Representative;
import org.mardep.ssrs.domain.user.TransactionLock;
import org.mardep.ssrs.ebs.pojo.BaseResponse;
import org.mardep.ssrs.ebs.pojo.Result;
import org.mardep.ssrs.ebs.pojo.Vessel;
import org.mardep.ssrs.ebs.pojo.inbound.createDn4Atc.CreateDn4AtcRequest;
import org.mardep.ssrs.ebs.pojo.inbound.createDn4Atc.CreateDn4AtcResponse;
import org.mardep.ssrs.ebs.pojo.inbound.download.DownloadDnRequest;
import org.mardep.ssrs.ebs.pojo.inbound.download.DownloadDnResponse;
import org.mardep.ssrs.ebs.pojo.inbound.download.DownloadTranscriptRequest;
import org.mardep.ssrs.ebs.pojo.inbound.download.DownloadTranscriptResponse;
import org.mardep.ssrs.ebs.pojo.inbound.getTakeUpRateStat.GetTakeUpRateStatRequest;
import org.mardep.ssrs.ebs.pojo.inbound.getTakeUpRateStat.GetTakeUpRateStatResponse;
import org.mardep.ssrs.ebs.pojo.inbound.isSettled.IsSettledDn;
import org.mardep.ssrs.ebs.pojo.inbound.isSettled.IsSettledRequest;
import org.mardep.ssrs.ebs.pojo.inbound.isSettled.IsSettledResponse;
import org.mardep.ssrs.ebs.pojo.inbound.outstandingAtcList.OutstandingAtcListRequest;
import org.mardep.ssrs.ebs.pojo.inbound.outstandingAtcList.OutstandingAtcListResponse;
import org.mardep.ssrs.ebs.pojo.inbound.outstandingAtcList.OutstandingDemandNote;
import org.mardep.ssrs.ebs.pojo.inbound.outstandingDnList.OutstandingDnListRequest;
import org.mardep.ssrs.ebs.pojo.inbound.outstandingDnList.OutstandingDnListResponse;
import org.mardep.ssrs.ebs.pojo.inbound.outstandingDnList.OutstandingDnListResponseDemandNote;
import org.mardep.ssrs.ebs.pojo.inbound.receipt.ReceiptDn;
import org.mardep.ssrs.ebs.pojo.inbound.receipt.ReceiptRequest;
import org.mardep.ssrs.ebs.pojo.inbound.receipt.ReceiptResponse;
import org.mardep.ssrs.ebs.pojo.inbound.rejectAutopay.DemandNote;
import org.mardep.ssrs.ebs.pojo.inbound.rejectAutopay.RejectAutopayRequest;
import org.mardep.ssrs.ebs.pojo.inbound.rejectAutopay.RejectAutopayResponse;
import org.mardep.ssrs.ebs.pojo.inbound.retrieveVessel4Transcript.RetrieveVessel4TranscriptRequest;
import org.mardep.ssrs.ebs.pojo.inbound.retrieveVessel4Transcript.RetrieveVessel4TranscriptResponse;
import org.mardep.ssrs.ebs.pojo.inbound.retrieveVesselByIMO.RetrieveVesselByIMORequest;
import org.mardep.ssrs.ebs.pojo.inbound.retrieveVesselByIMO.RetrieveVesselByIMOResponse;
import org.mardep.ssrs.ebs.pojo.inbound.searchVessel4Transcript.SearchVessel4TranscriptRequest;
import org.mardep.ssrs.ebs.pojo.inbound.searchVessel4Transcript.SearchVessel4TranscriptResponse;
import org.mardep.ssrs.ebs.pojo.inbound.settleDn.SettleDnRequest;
import org.mardep.ssrs.ebs.pojo.inbound.settleDn.SettleDnResponse;
import org.mardep.ssrs.ebs.pojo.inbound.shipReg.ShipRegRequest;
import org.mardep.ssrs.ebs.pojo.inbound.shipReg.ShipRegResponse;
import org.mardep.ssrs.ebs.pojo.inbound.transcriptApp.PaymentItem;
import org.mardep.ssrs.ebs.pojo.inbound.transcriptApp.SubmitReq;
import org.mardep.ssrs.ebs.pojo.inbound.transcriptApp.SubmitTranscriptResponse;
import org.mardep.ssrs.ebs.pojo.inbound.transcriptApp.ValidateReq;
import org.mardep.ssrs.ebs.pojo.inbound.transcriptApp.ValidateTranscriptResponse;
import org.mardep.ssrs.ebs.pojo.inbound.updateAtc.UpdateAtcRequest;
import org.mardep.ssrs.ebs.pojo.inbound.updateAtc.UpdateAtcResponse;
import org.mardep.ssrs.report.IDemandNoteGenerator;
import org.mardep.ssrs.report.generator.RPT_SR_011;
import org.mardep.ssrs.service.IDemandNoteService;
import org.mardep.ssrs.service.IDnsService;
import org.mardep.ssrs.vitaldoc.IVitalDocClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EbsInboundService {

	private static final int CAUSE_LOCK = 2;

	private static final int CAUSE_NOT_FOUND = 1;

	private static final int CAUSE_INVALID_DNNO = 3;

	private static final int CAUSE_INVALID_FORMAT_DOCID = 4;
	private static final int CAUSE_DNNO_NOT_SETTLED = 5;
	private static final int CAUSE_INVALID_FORMAT_TXNID = 6;

	private static final String FEE_CODE_ATC = "01";



	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	IRegMasterDao rmDao;

	@Autowired
	IShipTypeDao shipTypeDao;

	@Autowired
	@Qualifier("demandNoteService")
	IDemandNoteService dns;

	@Autowired
//	@Qualifier("demandNoteGenerator")
	IDemandNoteGenerator demandNoteGenerator;

	@Autowired
	IDnsService dnsService;

	@Autowired
	IVitalDocClient vitalDocClient;

	@Autowired
	IDemandNoteReceiptDao receiptDao;

	@Autowired
	@Qualifier("RPT_SR_011")
	RPT_SR_011 transcriptService;

	@Autowired
	IFeeCodeDao fcDao;

	@Autowired
	IRepresentativeDao repDao;

	@Autowired
	ITransactionLockDao lockDao;

	@Autowired
	IEbsTranscriptDao ebsTranscriptDao;

	@Autowired
	IReceiptService receiptService;

	@Autowired
	IDemandNoteItemDao dnItemDao;

	@Autowired ISystemParamDao systemParamDao;

	@Value("${EbsInboundService.timeToSleep}")
	private long timeToSleep;

	@Value("${EbsInboundService.pollingTimeout}")
	private long pollingTimeout;

	/**
	 * 3.1.6	Interface 1 – Search Vessel for Transcript
	 * Response result Type: E
	 * E0001 - Internal Server Error (Database exception or interface connection failed)
	 * @param req
	 * @return
	 */
	@Transactional
	public SearchVessel4TranscriptResponse searchVessel4Transcript(SearchVessel4TranscriptRequest req) {
		String shipName = req.getVesselName();
		SearchVessel4TranscriptResponse resp = new SearchVessel4TranscriptResponse();
		List<RegMaster> rmList;
		try {
			rmList = rmDao.searchVessel4Transcript(shipName);
			resp.setShipList(convert(rmList));
		} catch (Exception e) {
			error(resp, e, "rmDao.searchVessel4Transcript(" + shipName + ")");
		}
		return resp;
	}

	private List<Vessel> convert(List<RegMaster> rmList) {
		List<ShipType> types = shipTypeDao.findAll();
		List<Vessel> shipList = new ArrayList<>();
		for (RegMaster rm : rmList) {
			Vessel v1 = new Vessel();
			v1.setApplNo(rm.getApplNo());
			v1.setCallSign(rm.getCallSign());
			v1.setGt(rm.getGrossTon());
			v1.setImo(rm.getImoNo());
			v1.setNt(rm.getRegNetTon());
			v1.setOfficialNo(rm.getOffNo());
			/*
			 * when reg_status = 'A' then ''
         when reg_status = 'R' and reg_date<= sysdate then 'Registered'
         when reg_status = 'R' and reg_date> sysdate then 'Scheduled ' || to_char(reg_date, 'YYYYMMDD')
         else 'Scheduled'

			 */
			String status = "Scheduled";
			switch (rm.getRegStatus()) {
			case "A":
				status = "Pending for Registration Completion";
				break;
			case "R":

				status = rm.getRegDate() == null? "Scheduled" :
					rm.getRegDate().getTime() <= System.currentTimeMillis() ?
				"Registered"
						: "Scheduled " + new SimpleDateFormat("yyyyMMdd").format(rm.getRegDate());
				break;
			default:
				break;
			}
			v1.setRegStatus(status);

			v1.setVesselChiName(rm.getRegChiName());
			v1.setVesselName(rm.getRegName());
			for (ShipType type : types) {
				if (type.getId().equals(rm.getShipTypeCode())) {
					v1.setVesselTypeDesc(type.getStDesc());
					break;
				}
			}
			v1.setRegDate(rm.getRegDate());
			shipList.add(v1);
		}
		return shipList;
	}

	/**
	 * 3.1.8	Interface 3 – Retrieve Vessel by IMO
	 * @param req
	 * @return
	 */
	@Transactional
	public RetrieveVesselByIMOResponse retrieveVesselByIMO(RetrieveVesselByIMORequest req) {
		String imo = req.getImo();
		RetrieveVesselByIMOResponse resp = new RetrieveVesselByIMOResponse();
		try {
			RegMaster rm = rmDao.retrieveVesselByIMO(imo);
			if (rm != null) {
				List<Vessel> shipList = convert(Arrays.asList(rm));
				resp.setShipList(shipList);
			}
		} catch (Exception e) {
			return error(resp, e, "rmDao.retrieveVesselByIMO(" + imo + ")");
		}
		return resp;
	}

	/**
	 * 3.1.7	Interface 2 – Retrieve Vessel for Transcript
	 * retrieve the registered or deregistered vessel record which is allowed to apply uncertified Transcript Application
	 * If there is more than one record found, the first record should be returned
	 *
	 * E0001 - Internal Server Error (Database exception or interface connection failed)
	 * @param req
	 * @return
	 */
	@Transactional
	public RetrieveVessel4TranscriptResponse retrieveVessel4Transcript(RetrieveVessel4TranscriptRequest req) {
		String officialNo = req.getOfficialNo();
		String shipName = req.getVesselName();
		RetrieveVessel4TranscriptResponse resp = new RetrieveVessel4TranscriptResponse();
		try {
			RegMaster rm = rmDao.retrieveVessel4Transcript(officialNo ,shipName);
			List<Vessel> shipList;
			if (rm != null) {
				shipList = convert(Arrays.asList(rm));
			} else {
				shipList = new ArrayList<>();
			}
			resp.setShipList(shipList);
		} catch (Exception e) {
			error(resp, e, "rmDao.retrieveVessel4Transcript("
					+ officialNo + ", " + shipName
					+ ")");
		}
		return resp;
	}

	/**
	 * 3.1.9	Interface 4 – Validate Transcript
	 * E0001 - Internal Server Error (Database exception or interface connection failed)
	 * M0001 - The ship name could not be found on the registration period or not on the register
	 * M0002 - Ship's particulars, owner's particulars or mortgage transaction is being updated. Please retry a few minutes later
	 * @param req
	 * @return
	 */
	@Transactional
	public ValidateTranscriptResponse validateTranscriptApp(ValidateReq req) {
		String applNo = req.getApplNo();
		Calendar inputDate = req.getInputDate();
		ValidateTranscriptResponse resp = validateTranscript(applNo, inputDate, false);
		return resp;
	}

	/**
	 * M0001 - The ship name could not be found on the registration period or not on the register.
	 * M0002 - Ship's particulars, owner's particulars or mortgage transaction is being updated. Please retry a few minutes later.
	 * @param applNo
	 * @param inputDate
	 * @param ignoreLock
	 * @return
	 */
	private ValidateTranscriptResponse validateTranscript(String applNo, Calendar inputDate, boolean ignoreLock) {
		ValidateTranscriptResponse resp = new ValidateTranscriptResponse();
		if (inputDate == null) {
			inputDate = Calendar.getInstance();
		}
		try {
			FeeCode fc = rmDao.validateTranscriptApp(applNo, inputDate);
			TransactionLock lock = ignoreLock ? null : lockDao.findById(applNo);
			if (fc == null) {
				resp.setStatus(ValidateTranscriptResponse.STATUS_REJECTED);
				resp.setResultType(BaseResponse.RESULT_TYPE_BUSINESS_ERROR);
				resp.getResultList().add(result("M0001", CAUSE_NOT_FOUND));
			} else if (lock == null) {
				resp.paymentItem = new ArrayList<>();
				PaymentItem item = new PaymentItem();
				item.setFee(fc.getFeePrice());
				item.setRevenueType(new BigDecimal(fc.getFormCode()));
				item.setFeeEngDesc(fc.getEngDesc());
				item.setFeeChiDesc(fc.getChiDesc());
				resp.paymentItem.add(item);
				resp.setStatus(ValidateTranscriptResponse.STATUS_APPROVED);
			} else {
				resp.setStatus(ValidateTranscriptResponse.STATUS_REJECTED);
				resp.setResultType(BaseResponse.RESULT_TYPE_BUSINESS_ERROR);
				Result result = (lock != null) ? result("M0002", CAUSE_LOCK) : result("M0001", CAUSE_NOT_FOUND);
				resp.getResultList().add(result);
			}
		} catch (Exception e) {
			logger.error("rmDao.validateTranscriptApp("
					+ applNo+ ", " + inputDate
					+ ")", e);
			resp.setResultType(BaseResponse.RESULT_TYPE_SYSTEM_ERROR);
			Result result = new Result();
			result.setResultCode(Result.CODE_SERVER_ERROR);
			result.setDescriptionEn(e.getMessage());
			resp.getResultList().add(result);
		}
		return resp;
	}

	private Result result(String string, int cause, Object... params) {
		Result result = new Result();
		result.setResultCode(string);
		switch (cause) {
		case CAUSE_NOT_FOUND:
			result.setDescriptionEn("The ship name could not be found on the registration period or not on the register");
			result.setDescriptionSC("\u8239\u53ea\u540d\u79f0\u4e0d\u5728\u6ce8\u518c\u671f\u95f4\u6216\u4e0d\u5728\u8bb0\u5f55\u518c\u91cc\u3002");
			result.setDescriptionTC("\u8239\u96bb\u540d\u7a31\u4e0d\u5728\u8a3b\u518a\u671f\u9593\u6216\u4e0d\u5728\u8a18\u9304\u518a\u88e1\u3002");
			break;
		case CAUSE_LOCK:
			result.setDescriptionEn("Ship's particulars, owner's particulars or mortgage transaction is being updated. Please retry a few minutes later.");
			result.setDescriptionSC("\u8239\u8236\u8d44\u6599\uff0c\u8239\u4e1c\u8d44\u6599\u6216\u6309\u63ed\u4ea4\u6613\u7684\u8be6\u60c5\u6b63\u5728\u88ab\u66f4\u65b0\u3002\u8bf7\u51e0\u5206\u949f\u540e\u91cd\u8bd5\u3002");
			result.setDescriptionTC("\u8239\u8236\u8cc7\u6599\uff0c\u8239\u6771\u8cc7\u6599\u6216\u6309\u63ed\u4ea4\u6613\u7684\u8a73\u60c5\u6b63\u5728\u88ab\u66f4\u65b0\u3002\u8acb\u5e7e\u5206\u9418\u5f8c\u91cd\u8a66\u3002");
			break;
		case CAUSE_INVALID_DNNO:
			result.setDescriptionEn("Invalid demand note number" + ((params != null && params.length > 0) ? " - " + params[0] : ""));
			result.setDescriptionSC("\u65e0\u6548\u7684\u7f34\u6b3e\u5355\u7f16\u53f7" + ((params != null && params.length > 0) ? " - " + params[0] : ""));
			result.setDescriptionTC("\u7121\u6548\u7684\u7e73\u6b3e\u55ae\u7de8\u865f" + ((params != null && params.length > 0) ? " - " + params[0] : ""));
			break;
		case CAUSE_INVALID_FORMAT_DOCID:
			result.setDescriptionEn("Invalid Format" + ((params != null && params.length > 0) ? " - " + params[0] : ""));
			result.setDescriptionSC("\u65e0\u6548\u7684\u7f16\u53f7" + ((params != null && params.length > 0) ? " - " + params[0] : ""));
			result.setDescriptionTC("\u7121\u6548\u7684\u7de8\u865f" + ((params != null && params.length > 0) ? " - " + params[0] : ""));
			break;
		case CAUSE_DNNO_NOT_SETTLED:
			result.setDescriptionEn("The requested demand note is not settled" + ((params != null && params.length > 0) ? " - " + params[0] : ""));
			result.setDescriptionSC("\u6240\u8bf7\u6c42\u7684\u7f34\u6b3e\u5355\u5c1a\u672a\u7ed3\u6e05" + ((params != null && params.length > 0) ? " - " + params[0] : ""));
			result.setDescriptionTC("\u6240\u8acb\u6c42\u7684\u7e73\u6b3e\u55ae\u5c1a\u672a\u7d50\u6e05" + ((params != null && params.length > 0) ? " - " + params[0] : ""));
			break;
		case CAUSE_INVALID_FORMAT_TXNID:
			result.setDescriptionEn("Invalid Format" + ((params != null && params.length > 0) ? " - " + params[0] : ""));
			result.setDescriptionSC("Invalid Format" + ((params != null && params.length > 0) ? " - " + params[0] : ""));
			result.setDescriptionTC("Invalid Format" + ((params != null && params.length > 0) ? " - " + params[0] : ""));
			break;
		}

		return result;
	}

	/**
	 * Interface 5
	 * Submit uncertified Transcript Application.
	 * SSRS should also return the index key of the transcript.
	 * eBS will then call Interface 6 – Download Transcript.
	 * For autopay, the demand note record should also be created in SSRS DB
	 * and transferred to DNS.
	 * For PPS, Credit Card or Union Pay, the demand note record should not
	 * be created
	 * then SSRS should return "Approved" status.
	 * If the application is rejected, then SSRS should not create DN.
	 * M0001 - The ship name could not be found on the registration period or not on the register.
	 * M0002 - Ship's particulars, owner's particulars or mortgage transaction is being updated. Please retry a few minutes later.
	 * applNo,inputDate,billingPerson,paymentMethod
	 * @param req
	 * @return
	 */
	@Transactional
	public SubmitTranscriptResponse submitTranscriptApp(SubmitReq req) {
		SubmitTranscriptResponse resp = new SubmitTranscriptResponse();
		String applicant = req.getBillingPerson();
		BigDecimal paymentMethod = req.getPaymentMethod();

		ValidateTranscriptResponse vResp = validateTranscript(req.applNo, req.inputDate, req.getIgnoreLock());
		resp.status = vResp.status;
		if (vResp.status == ValidateTranscriptResponse.STATUS_APPROVED) {
			FeeCode fc = rmDao.validateTranscriptApp(req.applNo, req.inputDate);
			resp.totalAmount = fc.getFeePrice();
			resp.docId = req.applNo + "," + new SimpleDateFormat("yyyyMMddHHmm").format(req.inputDate.getTime());
			if (SubmitReq.PAYMENT_METHOD_AUTOPAY == paymentMethod.intValue()) {
				// create demand note
				try {
					DemandNoteHeader header = createDn(applicant, fc, req.applNo, true);
					resp.dnNo = header.getDemandNoteNo();
					DownloadDnRequest dl = new DownloadDnRequest();
					dl.dnNo = resp.dnNo;
					boolean readyBeforeReturn = false;
					if (readyBeforeReturn) {
						long startPolling = System.currentTimeMillis();
						DownloadDnResponse downloadDn = downloadDn(dl);
						while (downloadDn.pdf == null || downloadDn.pdf.length == 0) {
							if (System.currentTimeMillis() - startPolling > pollingTimeout) {
								resp.setResultType(BaseResponse.RESULT_TYPE_SYSTEM_ERROR);
								Result result = new Result();
								result.setResultCode("E0001");
								String desc = " Internal Server Error, polling DMS timeout " + pollingTimeout;
								result.setDescriptionEn(desc);
								result.setDescriptionTC(desc);
								result.setDescriptionSC(desc);
								resp.getResultList().add(result);
								return resp;
							}
							logger.debug("waiting for DMS index operation {}" + timeToSleep);
							Thread.sleep(timeToSleep);
							downloadDn = downloadDn(dl);
						}
						logger.info("demand note ready {}", dl.dnNo);
					}
				} catch (Exception e) {
					resp.setResultType(BaseResponse.RESULT_TYPE_SYSTEM_ERROR);
					Result result = new Result();
					result.setResultCode("E0001");
					String desc = " Internal Server Error, " + e.getMessage();
					result.setDescriptionEn(desc);
					result.setDescriptionTC(desc);
					result.setDescriptionSC(desc);
					resp.getResultList().add(result);
					return resp;
				}
			} else {
				// remember txnCode, billing person, amount, feecode, fee price
				EbsTranscript ebsTranscript = new EbsTranscript();
				ebsTranscript.setAmount(fc.getFeePrice());
				ebsTranscript.setApplNo(req.applNo);
				ebsTranscript.setBillingPerson(req.billingPerson);
				ebsTranscript.setFeeCode(fc.getId());
				ebsTranscript = ebsTranscriptDao.save(ebsTranscript);
				resp.txnCode = String.valueOf(ebsTranscript.getTxnCode());
			}
		} else {
			resp.setResultType(vResp.getResultType());
			resp.getResultList().add(vResp.getResultList().get(0));
		}
		return resp;
	}

	private DemandNoteHeader createDn(String applicant, FeeCode fc, String applNo) throws Exception {
		return createDn(applicant, fc, applNo, false);
	}
	private DemandNoteHeader createDn(String applicant, FeeCode fc, String applNo, boolean autopay) throws Exception {
		return createDn(applicant, fc, applNo, null, null, null, autopay);
	}

	private DemandNoteHeader createDn(String applicant, FeeCode fc, String applNo, String address1, String address2, String address3, boolean autopay) throws Exception {
		DemandNoteHeader header = new DemandNoteHeader();
		header.setAmount(fc.getFeePrice());
		header.setBillName(applicant != null && applicant.length() > 80 ? applicant.substring(0, 80) : applicant);
		header.setAddress1(address1);
		header.setAddress2(address2);
		header.setAddress3(address3);
		header.setApplNo(applNo);

		ArrayList<DemandNoteItem> items = new ArrayList<>();
		DemandNoteItem item;
		if (FEE_CODE_ATC.equals(fc.getId())) {
			DemandNoteItem criteria = new DemandNoteItem();
			criteria.setApplNo(applNo);
			criteria.setFcFeeCode("01");
			criteria.setActive(Boolean.TRUE);
			item = null;
			List<DemandNoteItem> existing = dnItemDao.findByCriteria(criteria);
			for (DemandNoteItem it : existing) {
				if (it.getDnDemandNoteNo() ==  null) {
					item = it;
					break;
				}
			}
			if (item == null) {
				throw new IllegalArgumentException("no outstanding atc item for applNo " + applNo);
			}
			item.setAmount(fc.getFeePrice());
			dnItemDao.save(item); // update the price
		} else {
			item = new DemandNoteItem();
			item.setAmount(fc.getFeePrice());
			item.setApplNo(applNo);
			item.setChargedUnits(1);
			item.setFcFeeCode(fc.getId());
		}
		items.add(item);
		header.setDemandNoteItems(items);
		header.setEbsFlag(autopay ? "1" : "0");
		if (autopay) {
			header.setPaymentStatus(DemandNoteHeader.PAYMENT_STATUS_AUTOPAY_ARRANGED);
		}
		header = dns.create(header, autopay);

		String demandNoteNo = header.getDemandNoteNo();
		byte[] osContent = demandNoteGenerator.generate(demandNoteNo, autopay);
		header.setPdf(osContent);

		dnsService.createDemandNote(demandNoteNo, Action.U, autopay);

		if(osContent!=null){
			vitalDocClient.uploadDemandNote(demandNoteNo, osContent);
		}

		return header;
	}
	/**
	 * 3.1.11	Interface 6 – Download Transcript
	 * @param req
	 * @return
	 */
	@Transactional
	public DownloadTranscriptResponse downloadTranscript(DownloadTranscriptRequest req) {
		DownloadTranscriptResponse resp = new DownloadTranscriptResponse();
		Map<String, Object> inputParam = new HashMap<String, Object>();
		String docId = req.getDocId();
		String[] tokens = docId.split("\\,");
		if (tokens.length < 2) {
			resp.setResultType(BaseResponse.RESULT_TYPE_SYSTEM_ERROR);
			resp.getResultList().add(result("E0002", CAUSE_INVALID_FORMAT_DOCID));
			return resp;
		} else {
			String applNo = tokens[0];
			try {
				Date reportDate = new SimpleDateFormat("yyyyMMddHHmm").parse(tokens[1]);
				inputParam.put("reportDate", reportDate);
				inputParam.put("applNo", applNo);
				inputParam.put("printLogo", Boolean.TRUE);
				inputParam.put("printMortgage", Boolean.TRUE);
				resp.docId = docId;
				resp.pdf = transcriptService.generate(inputParam);
				boolean dmsCopy = Boolean.getBoolean("EbsInboundService.downloadTranscript.dmsCopy");
				if (dmsCopy) {
					vitalDocClient.uploadEbsTranscript(req.docId, req.docId, req.docId, resp.pdf);
				}
			} catch (ParseException e) {
				resp.setResultType(BaseResponse.RESULT_TYPE_SYSTEM_ERROR);
				resp.getResultList().add(result("E0002", CAUSE_INVALID_FORMAT_DOCID));
				return resp;
			} catch (Exception e) {
				return error(resp, e, null);
			}
		}
		return resp;
	}
	private <T extends BaseResponse> T error(T resp, Throwable e, String msg) {
		if (msg == null) {
			msg = "ssrs-ebs error";
		}
		if (e == null) {
			e = new Exception();
		}
		logger.error(msg, e);
		resp.setResultType(BaseResponse.RESULT_TYPE_SYSTEM_ERROR);
		Result result = new Result();
		result.setResultCode(Result.CODE_SERVER_ERROR);
		result.setDescriptionEn(e == null ? msg : e.getMessage());
		resp.getResultList().add(result);
		return resp;
	}

	public DownloadDnResponse downloadDn(DownloadDnRequest req) {
		DownloadDnResponse resp = new DownloadDnResponse();
		try {
			resp.docId = req.dnNo;
			List<DemandNoteItem> items = dnItemDao.findByDemandNoteNo(req.dnNo);
			if (!items.isEmpty()) {
				resp.pdf = vitalDocClient.downloadDemandNote(req.dnNo);
				long startTime = System.currentTimeMillis();
				while (resp.pdf == null || resp.pdf.length == 0) {
					if (System.currentTimeMillis() - startTime > 40000) {
						logger.info("dont wait and timeout " + req.dnNo);
						break;
					} else {
						logger.info("wait and retry " + req.dnNo);
						Thread.sleep(4000);
						resp.pdf = vitalDocClient.downloadDemandNote(req.dnNo);
					}
				}
			}
		} catch (IOException e) {
			return error(resp, e, null);
		} catch (InterruptedException e) {
			resp.pdf = null;
		}
		if (resp.pdf == null || resp.pdf.length == 0) {
			resp.setResultType(BaseResponse.RESULT_TYPE_SYSTEM_ERROR);
			resp.getResultList().add(result("E0002", CAUSE_INVALID_FORMAT_DOCID));
		}
		return resp;
	}

	@Transactional
	public CreateDn4AtcResponse createDn4Atc(CreateDn4AtcRequest req) {
		CreateDn4AtcResponse resp = new CreateDn4AtcResponse();
		FeeCode atcFeeCode = new FeeCode();
		atcFeeCode.setId(FEE_CODE_ATC);
		atcFeeCode.setFeePrice(req.amount);
		try {
			Representative rp = repDao.findById(req.getApplNo());
			String rpName = rp != null ? rp.getName() : "-";
			//  2019/11/25 14:43:34 Insert Representative Person address into Demand Note for ATC 
			DemandNoteHeader dn = createDn(rpName, atcFeeCode, req.applNo,
					rp == null ? null : rp.getAddress1(), 
							rp == null ? null : rp.getAddress2(), 
									rp == null ? null : rp.getAddress3(), req.autopay);
			DownloadDnRequest req2 = new DownloadDnRequest();
			req2.dnNo = dn.getDemandNoteNo();
			resp.docId = dn.getDemandNoteNo();
			resp.pdf = dn.getPdf();
			return resp;
		} catch (Exception e) {
			return error(resp, e, null);
		}
	}

	/**
	 * interface 8
	 * settle the demand note and create receipt.
	 * For autopay, update the actual value date to SSRS
	 * and create receipt in SSRS as well as transfer receipt to DNS
	 * For PPS, Credit Card or Union Pay,
	 * create demand note and receipt in SSRS
	 * demand note and receipt should be transferred to DNS
	 * If the demand note or receipt record is failed to create/update in SSRS DB,
	 * then SSRS should rollback the data and return the error code with error message.
	 * @param req
	 * @return
	 */
	@Transactional
	public SettleDnResponse settleDn(SettleDnRequest req) {
		SettleDnResponse resp = new SettleDnResponse();

		DemandNoteHeader header;
		if (SubmitReq.PAYMENT_METHOD_AUTOPAY != req.paymentMethod) {
			// create demand note
			String txnCode = req.getTxnCode();

			EbsTranscript transcript = ebsTranscriptDao.findById(Long.parseLong(txnCode));
			if (transcript == null) {
				resp.setResultType(BaseResponse.RESULT_TYPE_SYSTEM_ERROR);
				resp.getResultList().add(result("E0002", CAUSE_INVALID_FORMAT_TXNID, txnCode));
				return resp;
			} else {
				FeeCode fc = new FeeCode();
				fc.setFeePrice(transcript.getAmount());
				fc.setId(transcript.getFeeCode());
				try {
					header = createDn(transcript.getBillingPerson(), fc, transcript.getApplNo());
					DownloadDnRequest dl = new DownloadDnRequest();
					dl.dnNo = header.getDemandNoteNo();
					boolean readyBeforeReturn = false;
						if (readyBeforeReturn) {
							long startPolling = System.currentTimeMillis();
						DownloadDnResponse downloadDn = downloadDn(dl);
						while (downloadDn.pdf == null || downloadDn.pdf.length == 0) {
							if (System.currentTimeMillis() - startPolling > pollingTimeout) {
								resp.setResultType(BaseResponse.RESULT_TYPE_SYSTEM_ERROR);
								Result result = new Result();
								result.setResultCode("E0001");
								String desc = " Internal Server Error, polling DMS timeout " + pollingTimeout;
								result.setDescriptionEn(desc);
								result.setDescriptionTC(desc);
								result.setDescriptionSC(desc);
								resp.getResultList().add(result);
								return resp;
							}
							logger.debug("waiting for DMS index operation {}" + timeToSleep);
							Thread.sleep(timeToSleep);
							downloadDn = downloadDn(dl);
						}
					}
				} catch (Exception e) {
					resp.setResultType(BaseResponse.RESULT_TYPE_SYSTEM_ERROR);
					Result result = new Result();
					result.setResultCode("E0001");
					String desc = " Internal Server Error, " + e.getMessage();
					result.setDescriptionEn(desc);
					result.setDescriptionTC(desc);
					result.setDescriptionSC(desc);
					resp.getResultList().add(result);
					return resp;
				}
			}
		} else {
			header = dns.findById(DemandNoteHeader.class, req.dnNo);
			if (header == null) {
				resp.setResultType(BaseResponse.RESULT_TYPE_SYSTEM_ERROR);
				Result result = new Result();
				result.setResultCode("E0001");
				String desc = "unexpected dnNo " + req.dnNo;
				result.setDescriptionEn(desc);
				result.setDescriptionTC(desc);
				result.setDescriptionSC(desc);
				resp.getResultList().add(result);
				return resp;
			}
		}
		//resp.docId = header.getDemandNoteNo();

		// create receipt

		String dnNo = header.getDemandNoteNo();
		resp.docId = dnNo;
		BigDecimal amount = header.getAmount();
		Date receiptDate = req.valueDate.getTime();
		String paymentType = toDnsPayment(req.paymentMethod);

		SystemParam sp = systemParamDao.findById("RECEIPT_NO_SEQ");
		int nextVal = (Integer.parseInt(sp.getValue()) + 1) % 100000;
		String next = String.valueOf(nextVal);
		sp.setValue(next);
		systemParamDao.save(sp);
//		2019/09/11 10:37:29 From eBS
//		Receipt Payment Type = Autopay:ZSR + 2 digits of year + 5 digits (seq: max +1)
		String receiptNo = "ZSR" + new SimpleDateFormat("yy").format(new Date()) + new DecimalFormat("00000").format(nextVal);
		if (!"70".equals(paymentType)) {
//		Receipt Payment Type = PPS:EBS + 7 digits (seq: max +1)
//		Receipt Payment Type = Credit Card: EBS + 7 digits (seq: max +1)
			receiptNo = "ES" + new DecimalFormat("0000000").format(nextVal);
		}


		// create control data

		// call dns to update payment
		boolean pending = false;
		if (receiptDate.after(new Date())) {
			pending = true;
			logger.info("dnNo {} is not send, it is pending {}", dnNo, receiptDate);
		} else {
			dnsService.updatePayment(receiptNo, dnNo, amount, receiptDate, paymentType);
		}

		// simulate dns callback
		ReceiptStatusRequest receiptStatusReq = new ReceiptStatusRequest();

		ReceiptStatus recStat = new ReceiptStatus();
		recStat.setAction(CreateReceiptAction.U); // for new receipt
		ReceiptInfo recInfo = new ReceiptInfo();

		recInfo.setDnNo(dnNo);
		recInfo.setReceiptNo(receiptNo);
		recInfo.setReceiptDate(receiptDate);
		recInfo.setReceiptAmount(amount);
		recInfo.setPaymentRef(pending ? IReceiptService.PENDING_REF : "");
		ReceiptItem ri = new ReceiptItem();
		ri.setPaymentType(paymentType);
		ri.setPaymentAmount(amount);
		recInfo.setPaymentList(new ArrayList<>());
		recInfo.getPaymentList().add(ri);
		recInfo.setMachineID("");

		recStat.setReceipt(recInfo);
		receiptStatusReq.setCreateReceipt(recStat);
		ReceiptStatusResponse response = receiptService.processDnsRequest(receiptStatusReq);
		if (logger.isDebugEnabled()) {
			logger.debug("receipt status {}", response.getBaseResult());
		}
		// TODO check update_receipt response

		return resp;
	}
	private String toDnsPayment(int paymentMethod) {
		String dnsType;
		switch (paymentMethod) {
		case 1: // EBS_AUTOPAY
			dnsType = "70";
			break;
		case 6: // EBS_PPS
			dnsType = "90";
			break;
		case 7: // EBS_CREDITCARD
		case 8: // EBS_UNIONPAY
			dnsType = "50";
			break;
		default:
			throw new IllegalArgumentException("unknown ebs payment type " + paymentMethod);
		}
		/*
		 * dns payment type
		 * 10-CASH
		 * 20-CHEQUE
		 * 30-EPS
		 * 40-REMITTANCE
		 * 50-CREDIT_CARD
		 * 60-DEPOSIT
		 * 70-AUTOPAY
		 * 80-OCTOPUS
		 * 90-PPS
		 */
		return dnsType;
	}

	@Transactional
	public UpdateAtcResponse updateAtc(UpdateAtcRequest req) {
		RegMaster regMaster = rmDao.findById(req.getApplNo());
		UpdateAtcResponse resp = new UpdateAtcResponse();
		if (regMaster != null) {
			regMaster.setEpayment(req.isEpaymentIndicator() ? "Y" :"N");
			rmDao.save(regMaster);
		} else {
			resp.setResultType(BaseResponse.RESULT_TYPE_SYSTEM_ERROR);
			resp.resultList.add(result("E0002", CAUSE_INVALID_FORMAT_DOCID));
		}
		return resp;
	}

	@Transactional
	public IsSettledResponse isSettled(IsSettledRequest req) {
		IsSettledResponse resp = new IsSettledResponse();
		for (String dn : req.dnNoList) {
			DemandNoteHeader dnh = dns.findById(DemandNoteHeader.class, dn);
			if (dnh != null) {
				IsSettledDn isSettled = new IsSettledDn();
				isSettled.dnNo = dn;
				switch (dnh.getStatus()) {
				case DemandNoteHeader.STATUS_ISSUED:
					isSettled.status =
					DemandNoteHeader.PAYMENT_STATUS_OVERPAID.equals(dnh.getPaymentStatus()) ||
					DemandNoteHeader.PAYMENT_STATUS_PAID.equals(dnh.getPaymentStatus()) ?
							"P" :"O";
					break;
				case DemandNoteHeader.STATUS_CANCELLED:
					isSettled.status = "C";
					break;
				case DemandNoteHeader.STATUS_REFUNDED:
					isSettled.status = "F";
					break;
				case DemandNoteHeader.STATUS_WRITTEN_OFF:
					isSettled.status = "W";
					break;
				default:
					continue;
				}
				resp.dnList.add(isSettled);
			}
		}
		return resp;
	}
	@Transactional
	public OutstandingAtcListResponse outstandingAtcList(OutstandingAtcListRequest req) {
		OutstandingAtcListResponse resp = new OutstandingAtcListResponse();
		List<Object[]> items = dnItemDao.getOutstandingDn(null);
		for (Object[] row : items) {
			OutstandingDemandNote dn = new OutstandingDemandNote();
			dn.applNo = (String) row[0];
			dn.vesselName = (String) row[1];
			resp.demandNoteList.add(dn);
		}
		return resp;
	}
	@Transactional
	public OutstandingDnListResponse outstandingDnList(OutstandingDnListRequest req) {
		List<Object[]> items =  dnItemDao.getOutstandingDn(req.imoList);
		OutstandingDnListResponse resp = new OutstandingDnListResponse();
		for (Object[] row : items) {
			OutstandingDnListResponseDemandNote item = new OutstandingDnListResponseDemandNote();
			item.setApplNo((String) row[0]);
			item.setShipName((String) row[1]);
			item.shipCName = (String) row[2];
			if (row[3] != null) {
				Calendar genDate = Calendar.getInstance();
				genDate.setTime((Date) row[3]);
				item.setGenDate(genDate);
			}
			Calendar due = Calendar.getInstance();
			due.setTime((Date) row[4]);
			item.setAtcDueDate(due);
			item.setAmount((BigDecimal) row[5]);
			item.setItemNo((Long) row[6]);
			item.setRegNetTon((BigDecimal) row[7]);
			item.setImoNo((String) row[8]);
			resp.demandNoteList.add(item);
		}
		return resp;
	}
	@Transactional
	public ReceiptResponse receipt(ReceiptRequest req) {
		List<DemandNoteReceipt> receipts;
		if (req.dnNo != null) {
			receipts = receiptDao.findByDemandNoteNo(req.dnNo);
		} else {
			receipts = receiptDao.findByReceiptNo(req.receiptNo);
		}
		ReceiptResponse resp = new ReceiptResponse();
		for (DemandNoteReceipt receipt : receipts) {
			if (receipt.getCancelDate() != null || receipt.getCanAdjStatus() != null) {
				continue;
			}
			ReceiptDn e = new ReceiptDn();
			e.amount = receipt.getAmount();
			e.dnNo = receipt.getDemandNoteNo();
			if (receipt.getInputTime() != null) {
				e.receiptDate = Calendar.getInstance();
				e.receiptDate.setTime(receipt.getInputTime());
			}
			e.receiptNo = receipt.getReceiptNo();
			resp.getDnList().add(e);
		}
		if (resp.getDnList().isEmpty()) {
			resp.setResultType("E");
			Result r = new Result();
			r.setDescriptionEn("no record found");
			r.setDescriptionSC(r.getDescriptionEn());
			r.setDescriptionTC(r.getDescriptionEn());
			resp.getResultList().add(r);
		}

		return resp;
	}
	/**
	 * Interface-9 reject autopay payment.
	 * SSRS should update SSRS DB tables.
	 * all payment records are successfully rejected in SSRS DB,
	 * SSRS should return demand note files in PDF format.
	 * One PDF file represents one demand note. If one of the payments is failed to reject in SSRS DB, then SSRS should rollback all the data and return the error code with error message.
	 * @param req
	 * @return
	 */
	@Transactional
	public RejectAutopayResponse rejectAutopay(RejectAutopayRequest req) {
		String rejectedBy = req.getRejectedBy();
		List<String> demandNoteList = req.getDemandNoteList();
		logger.info("rejectAutopay, "+rejectedBy + demandNoteList);
		RejectAutopayResponse resp = new RejectAutopayResponse();
		List<DemandNoteReceipt> targets = new ArrayList<>();
		for (String dnNo : demandNoteList) {
			DemandNoteReceipt criteria = new DemandNoteReceipt();
			criteria.setDemandNoteNo(dnNo);
			// check dnNo
			DemandNoteHeader dnh = dns.findById(DemandNoteHeader.class, dnNo);
			if (dnh == null) {
				// M0001
				resp.setResultType(BaseResponse.RESULT_TYPE_BUSINESS_ERROR);
				resp.resultList.add(result("M0001", CAUSE_INVALID_DNNO, dnNo));
				return resp;
			}
			List<DemandNoteReceipt> receipts = receiptDao.findByCriteria(criteria);
			if (!receipts.isEmpty()) {

				DemandNoteReceipt receipt = receipts.get(0);
				targets.add(receipt);
			} else {
				// M0002
				resp.setResultType(BaseResponse.RESULT_TYPE_BUSINESS_ERROR);
				resp.resultList.add(result("M0002", CAUSE_DNNO_NOT_SETTLED, dnNo));
				return resp;
			}
		}
		for (DemandNoteReceipt receipt : targets) {
			addReceipt(resp, receipt);
		}
		try {
			for (DemandNote dn : resp.demandNoteList) {
					byte[] osContent = demandNoteGenerator.generate(dn.getDnNo(), false);

					if(osContent!=null){
						vitalDocClient.uploadDemandNote(dn.getDnNo(), osContent);
					}
			}
		} catch (Exception e) {
			logger.error("reject autopay", e);
			resp.setResultType(BaseResponse.RESULT_TYPE_SYSTEM_ERROR);
			Result result = new Result();
			result.setResultCode(Result.CODE_SERVER_ERROR);
			result.setDescriptionEn(e.getMessage());
			resp.getResultList().add(result);
		}
		return resp;
	}

	private void addReceipt(RejectAutopayResponse resp, DemandNoteReceipt receipt) {
		ReceiptInfo recInfo = new ReceiptInfo();
		recInfo.setDnNo(receipt.getDemandNoteNo());
		recInfo.setReceiptNo(receipt.getReceiptNo());
		recInfo.setReceiptDate(new Date());
		recInfo.setMachineID("");
		recInfo.setReceiptAmount(receipt.getAmount());

		ReceiptStatus recStat = new ReceiptStatus();
		recStat.setAction(CreateReceiptAction.C); // Cancel
		recStat.setReceipt(recInfo);
		ReceiptStatusRequest receiptStatusRequest = new ReceiptStatusRequest();
		receiptStatusRequest.setCreateReceipt(recStat);
		ReceiptStatusResponse response = receiptService.processDnsRequest(receiptStatusRequest);
		if (logger.isDebugEnabled()) {
			logger.debug("receipt status {}", response.getBaseResult());
		}

		// TODO check cancel_recipet response
		DemandNote dn = new DemandNote();
		dn.setDnNo(receipt.getDemandNoteNo());
		dn.setReceiptAmt(receipt.getAmount());
		Calendar receiptDate = Calendar.getInstance();
		receiptDate.setTime(receipt.getInputTime());
		dn.setReceiptDate(receiptDate);
		dn.setReceiptNo(receipt.getReceiptNo());
		resp.getDemandNoteList().add(dn);

		dnsService.cancelPayment(receipt.getReceiptNo(), receipt.getDemandNoteNo(), receipt.getAmount(),
				receipt.getInputTime(), receipt.getPaymentType());
	}

	@Transactional
	public ShipRegResponse shipReg(ShipRegRequest req) {
		ShipRegResponse resp = new ShipRegResponse();
		List<RegMaster> list = rmDao.ebsShipReg(req.vesselName, req.officialNo, req.imoNo);
		resp.setShipList(convert(list));
		return resp;
	}


	public GetTakeUpRateStatResponse getTakeUpRateStatRequest(GetTakeUpRateStatRequest req) {
		GetTakeUpRateStatResponse resp = new GetTakeUpRateStatResponse();
		//FromDate/ToDate are mandatory
//		JIRA-191
		Date startDate = DateUtils.truncate(req.fromDate, Calendar.DATE).getTime();
		Date endDate = DateUtils.truncate(req.toDate, Calendar.DATE).getTime();
		logger.info("Start Date:{}", startDate);
		logger.info("End Date:{}", endDate);
		Long[] result = rmDao.getTakeUpRate(startDate, endDate);
//		Long[] result = rmDao.getTakeUpRate(req.fromDate.getTime(), req.toDate.getTime());

		resp.resultList = new ArrayList<Result>();
		resp.transcriptTotal = result[0];
		resp.hkRegShipTotal = result[1];
		return resp;
	}
}
