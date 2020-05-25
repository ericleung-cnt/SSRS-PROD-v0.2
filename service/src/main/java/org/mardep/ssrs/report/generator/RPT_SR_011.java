package org.mardep.ssrs.report.generator;

import java.io.ByteArrayOutputStream;
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
import java.util.Locale;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang.StringUtils;
import org.mardep.ssrs.dao.codetable.IFeeCodeDao;
import org.mardep.ssrs.dao.codetable.IReasonCodeDao;
import org.mardep.ssrs.dao.codetable.IRegistrarDao;
import org.mardep.ssrs.dao.sr.IBuilderMakerDao;
import org.mardep.ssrs.dao.sr.ITransactionDao;
import org.mardep.ssrs.domain.codetable.FeeCode;
import org.mardep.ssrs.domain.codetable.ReasonCode;
import org.mardep.ssrs.domain.codetable.ReasonCodePK;
import org.mardep.ssrs.domain.codetable.Registrar;
import org.mardep.ssrs.domain.dn.DemandNoteItem;
import org.mardep.ssrs.domain.sr.BuilderMaker;
import org.mardep.ssrs.domain.sr.Mortgage;
import org.mardep.ssrs.domain.sr.Mortgagee;
import org.mardep.ssrs.domain.sr.Mortgagor;
import org.mardep.ssrs.domain.sr.Owner;
import org.mardep.ssrs.domain.sr.RegMaster;
import org.mardep.ssrs.domain.sr.Representative;
import org.mardep.ssrs.domain.sr.Transaction;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.mardep.ssrs.report.EnglishReportLongDateFormat;
import org.mardep.ssrs.service.IDemandNoteService;
import org.mardep.ssrs.service.IMortgageService;
import org.mardep.ssrs.service.IShipRegService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JRException;

@Service("RPT_SR_011")
public class RPT_SR_011 extends AbstractSrReport {

	@Autowired
	IBuilderMakerDao bmDao;

	@Autowired
	IRegistrarDao rDao;

	@Autowired
	ITransactionDao transactionDao;

	@Autowired
	IShipRegService service;

	@Autowired
	IMortgageService mortgageSrv;

	@Autowired
	IReasonCodeDao reasons;

	@Autowired
	IDemandNoteService demandNoteService;

	@Autowired
	IFeeCodeDao fcDao;

	public RPT_SR_011() {
		super("RPT-SR-011.jrxml", null);
	}

	@Override
	public byte[] generate(Map<String, Object> inputParam) throws Exception {
		Date reportDate = (Date)inputParam.get("reportDate");
		Date generateTime = new Date(System.currentTimeMillis() + 5000 /* buffer for server time gap */);
		if (reportDate == null) {
			reportDate = generateTime;
		}
		if (reportDate.after(generateTime)) {
			logger.warn("report date {} generate date {} ", reportDate, generateTime  );
			throw new IllegalArgumentException("Report time after current time is not allowed");
		}
		for (String applNo : ((String) inputParam.get("applNo")).split("\\,")) {
			RegMaster regMaster = getRegMaster(applNo, reportDate);
			if (regMaster == null || "A".equals(regMaster.getRegStatus())) {
				throw new IllegalArgumentException("APPL " + applNo + " is not registered");
			}
		}
		if (Boolean.TRUE.equals(inputParam.get("paymentRequired"))) {
//			Fee Code 11 - Copy of, or extract from, any entry in the register
			FeeCode code11 = fcDao.findById("11");
			FeeCode code12 = fcDao.findById("12");
			Date generationTime = generateTime;
			for (String applNo : ((String) inputParam.get("applNo")).split("\\,")) {
				DemandNoteItem fc11 = new DemandNoteItem();
				fc11.setActive(true);
				fc11.setAmount(code11.getFeePrice());
				fc11.setApplNo(applNo);
				fc11.setChargedUnits(1);
				fc11.setFcFeeCode("11");
				fc11.setGenerationTime(generationTime);
				demandNoteService.addItem(fc11);
				if (Boolean.TRUE.equals(inputParam.get("certified"))) {
//			Fee Code 12 - Certification of a copy of, or extract from, any entry in the register
					DemandNoteItem fc12 = new DemandNoteItem();
					fc12.setActive(true);
					fc12.setAmount(code12.getFeePrice());
					fc12.setApplNo(applNo);
					fc12.setChargedUnits(1);
					fc12.setFcFeeCode("12");
					fc12.setGenerationTime(generationTime);
					demandNoteService.addItem(fc12);
				}
			}
		}
		if (System.currentTimeMillis() - reportDate.getTime() < 60000) {
			reportDate = new Date(reportDate.getTime() + 60000); // add some delay for showing latest tx
		}
		String applNoList = (String) inputParam.get("applNo");
		boolean printMortgage = Boolean.TRUE.equals(inputParam.get("printMortgage"));
		logger.info("Report Date:{}", reportDate);

		//for user to override issueDate (Date Back)
		Date issueDateUI = (Date)inputParam.get("issueDate");
		if (issueDateUI == null) {
			issueDateUI = new Date();
		}

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("certified", inputParam.get("certified"));
		if (Boolean.TRUE.equals(inputParam.get("printLogo"))) {
			params.put("logo", RPT_SR_011.class.getResource("/images/400px-Regional_Emblem_of_Hong_Kong.svg.png"));
		}
		params.put("reportDate", (new EnglishReportLongDateFormat(false).format(reportDate) + " " + new SimpleDateFormat("HH:mm").format(reportDate)).replace(",", ""));
		params.put("reportDateChi", new SimpleDateFormat("y\u5e74M\u6708d\u65e5HH\u6642mm\u5206", Locale.CHINESE).format(reportDate));
		String currentUser = UserContextThreadLocalHolder.getCurrentUserName();
		params.put("userId", currentUser);
		if (printMortgage) {
			params.put("SUBREPORT_2", jasperReportService.getJasperReport("RPT-SR-011-subreport02.jrxml"));
			params.put("printMortgage", Boolean.TRUE);
			params.put("MortgagorSubReport", jasperReportService.getJasperReport("Transcript_Mortgagor_SubReport.jrxml"));
			params.put("MortgageeSubReport", jasperReportService.getJasperReport("Transcript_Mortgagee_SubReport.jrxml"));
			params.put("MortgagorList", prepareMortgagorList());
			params.put("MortgageeList", prepareMortgageeList());
		}
		params.put("SUBREPORT_1", jasperReportService.getJasperReport("RPT-SR-011-subreport01.jrxml"));
		params.put("SUBREPORT_3", jasperReportService.getJasperReport("RPT-SR-011-subreport03.jrxml"));
		params.put("issueDate", issueDateUI);

		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.MINUTE, 2);
		Date issueDate = cal.getTime(); // generateTime;
		params.put("issueTime", (new EnglishReportLongDateFormat(false).format(issueDate) + " at " + new SimpleDateFormat("HH:mm").format(issueDate)).replace(",", ""));
		params.put("issueTimeChi", new SimpleDateFormat("y\u5e74M\u6708d\u65e5HH\u6642mm\u5206", Locale.CHINESE).format(issueDate));
		Long registrarId = (Long) inputParam.get("registrar");
		params.put("registrar", "");
		if (registrarId != null) {
			Registrar registrar = rDao.findById(registrarId);
			if(registrar!=null){
				params.put("registrar", registrar.getName());
			}else{
				logger.info("Registrar is NULL for ID:{}", registrarId);
			}
		} else {
			params.put("registrar", "");
		}

		if (applNoList.contains(",")) {
			try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
				ZipOutputStream zip = new ZipOutputStream(baos);
				for (String applNo : applNoList.split("\\,")) {
					byte[] pdf = getPdf(reportDate, printMortgage, params, applNo);
					String name = applNo.replace('/', '_')+ ".pdf";
					ZipEntry entry = new ZipEntry(name);
					zip.putNextEntry(entry);
					zip.write(pdf);
					zip.closeEntry();
				}
				zip.finish();
				zip.flush();
				return baos.toByteArray();
			}
		} else {
			return getPdf(reportDate, printMortgage, params, applNoList);
		}
	}

	private List<Map<String, Object>> prepareMortgagorList(){
		List<Map<String, Object>> mortgagorList = new ArrayList<Map<String, Object>>();
		Map<String, Object> item = new HashMap<String, Object>();

		return mortgagorList;
	}

	private List<Map<String, Object>> prepareMortgageeList(){
		List<Map<String, Object>> mortgageeList = new ArrayList<Map<String, Object>>();
		Map<String, Object> item = new HashMap<String, Object>();
		item.put("mortgageeName", "mortgageeName");
		item.put("mortgageeAddress", "mortgageeAddress");
		item.put("telNo", "123-456");
		item.put("faxNo", "5566");
		item.put("email", "abc.com");
		return mortgageeList;
	}

	private String prepareMortgageeNameList(List<Mortgagee> mortgagees) {
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<mortgagees.size(); i++) {
			Mortgagee m = mortgagees.get(i);
			sb.append(m.getName());
			sb.append("\n");
			sb.append(m.getAddress1()==null ? "" : m.getAddress1());
			sb.append("\n");
			sb.append(m.getAddress2()==null ? "" : m.getAddress2());
			sb.append("\n");
			sb.append(m.getAddress3()==null ? "" : m.getAddress3());
			sb.append("\n");
			if (i<(mortgagees.size()-1)){
				if (m.getAddress1().length()<52) {
					sb.append("\n");
				}
				if (m.getAddress2().length()<52) {
					sb.append("\n");
				}
				if (m.getAddress3().length()<52) {
					sb.append("\n");
				}
				//sb.append("\n\n\n");
			}
		}
		return sb.toString();
	}

	private String prepareMortgageeTelList(List<Mortgagee> mortgagees) {
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<mortgagees.size(); i++) {
			sb.append("Tel. No.\n");
			sb.append("電話號碼 \n");
			sb.append("Fax No.\n");
			sb.append("傳真號碼 \n");
			sb.append("Telex No./Email\n");
			sb.append("電傳號碼／電郵地址 \n");
			if (i<(mortgagees.size()-1)) {
				sb.append("\n");
			}
		}
		return sb.toString();
	}

	private String prepareMortgageeTelDataList(List<Mortgagee> mortgagees) {
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<mortgagees.size(); i++) {
			Mortgagee m = mortgagees.get(i);
			sb.append(m.getTelNo() != null ? m.getTelNo() : "- ");
			sb.append("\n\n");
			sb.append(m.getFaxNo() != null ? m.getFaxNo() : "- ");
			sb.append("\n\n");
			sb.append(m.getEmail() != null ? m.getEmail() : "- ");
			sb.append("\n\n");
			if (i<(mortgagees.size()-1)) {
				sb.append("\n");
			}
		}
		return sb.toString();
	}

	private byte[] getPdf(Date reportDate, boolean printMortgage, Map<String, Object> params, String applNo)
			throws JRException, ParseException {
		return jasperReportService.generateReport(getReportFileName(), Arrays.asList(getReportContent(applNo, reportDate, printMortgage, params)), params);
	}

	private String evaluatePlaceOfIncorp(Owner owner) {
		if ((owner.getOverseaCert()==null || owner.getOverseaCert().isEmpty()) &&
				(owner.getIncortCert()==null || owner.getIncortCert().isEmpty())) {
			return owner.getIncorpPlace();
		} else {
			return "HONG KONG";
		}
	}

	protected void processRegistrar(HashMap<Object, Object> regMasterMap, RegMaster regMasterObject, Map<String, Object> params){
		Long registrarId = regMasterObject.getRegistrar();
		regMasterMap.put("registrar", "");
		if (registrarId != null) {
			Registrar registrar = rDao.findById(registrarId);
			if(registrar!=null){
				regMasterMap.put("registrar", registrar.getName());
			}else{
				logger.info("Registrar is NULL for ID:{}", registrarId);
			}
		}
	}

	private HashMap<Object, Object> getReportContent(String applNo, Date reportDate, boolean printMortgage, Map<String, Object> params) throws ParseException {
		boolean isCod = getClass().getName().endsWith("_cod");
		boolean isPercentage = true;
		
		RegMaster regM = getRegMaster(applNo, reportDate);
		HashMap<Object, Object> regMaster = new HashMap<>();
		if (regM == null) {
			return regMaster;
		}
		regMaster.put("regMaster", regM);
		regMaster.put("shipName", regM.getRegName());

		if (regM.getRcReasonCode() != null && regM.getRcReasonType() != null) {
			ReasonCodePK id = new ReasonCodePK(regM.getRcReasonCode(), regM.getRcReasonType());
			ReasonCode reason = reasons.findById(id);
			if (reason != null) {
				regMaster.put("reason", reason.getEngDesc());
				regMaster.put("reasonChi", reason.getChiDesc());
			}
		}
		
		// 20200513
		if ("S".equals(regM.getIntUnit())){
			params.put("pInterestHeldEng", "No. of Shares Held");
			params.put("pInterestHeldChi", "權益份額數目");
			isPercentage = false;
		} else if ("%".equals(regM.getIntUnit())){
			params.put("pInterestHeldEng", "Percentage of Interest Held");
			params.put("pInterestHeldChi", "權益百分比數目");	
			isPercentage = true;
		}
		// 20200513

		processRegistrar(regMaster, regM, params);
//		Long registrarId = regM.getRegistrar();
//		regMaster.put("registrar", "");
//		if (registrarId != null) {
//			Registrar registrar = rDao.findById(registrarId);
//			if(registrar!=null){
//				regMaster.put("registrar", registrar.getName());
//			}else{
//				logger.info("Registrar is NULL for ID:{}", registrarId);
//			}
//		}

		regMaster.put("imoNo", isNull(regM.getImoNo(), "-"));
		regMaster.put("callSign", isNull(regM.getCallSign(), "-"));
		regMaster.put("officialNo", regM.getOffNo());
		regMaster.put("materialOfHull", regM.getMaterial());
		DecimalFormat tonnageFormat = new DecimalFormat("#,###.## tons");
		regMaster.put("grossTonnage", tonnageFormat.format(isNull(regM.getGrossTon(), BigDecimal.ZERO)));
		regMaster.put("howPropelled",
				regM.getHowProp()!=null ? regM.getHowProp() : "N/A");
		regMaster.put("netTonnage", tonnageFormat.format(isNull(regM.getRegNetTon(), BigDecimal.ZERO)));

		regMaster.put("noOfSetsEngine",
				regM.getEngSetNum()!=null ? String.valueOf(regM.getEngSetNum()) : "0");
		regMaster.put("totalEnginePower",
				regM.getEngPower()!=null ? regM.getEngPower() : "N/A" );

		regMaster.put("portOfReg", "HONG KONG");
		regMaster.put("portOfRegChi", "\u9999\u6e2f");

		regMaster.put("dateKeelLaid", regM.getBuildDate());
		List<BuilderMaker> blders = getBuilders(applNo, reportDate);
		if (!blders.isEmpty()) {
			BuilderMaker builder = blders.get(0);
			regMaster.put("builder", builder);
		}

		regMaster.put("mainEngineType",
				regM.getEngDesc1()!=null ? regM.getEngDesc1() : "N/A");
		// 20190807 set default to N/A if engine make and model is null
		String engineMakeAndModel = "";
		if (regM.getEngModel1()!=null) {
			engineMakeAndModel = regM.getEngModel1() + "\n";
		}
		if (regM.getEngModel2()!=null) {
			engineMakeAndModel = engineMakeAndModel + regM.getEngModel2();
		}
		if (engineMakeAndModel.isEmpty()) {
			engineMakeAndModel = "N/A";
		}
		regMaster.put("engineMakeAndModel", engineMakeAndModel);

		Representative rep = getRep(applNo, reportDate);
		if (rep != null) {
			if ("".equals(rep.getName())) {
				params.put("noRP","T");
			} else {
				params.put("noRP","F");
				regMaster.put("repName", rep.getName());
				regMaster.put("repAddress",
					(rep.getAddress1() == null ? "" : rep.getAddress1()) + "\n" +
							(rep.getAddress2() == null ? "" : rep.getAddress2()) + "\n" +
							(rep.getAddress3() == null ? "" : rep.getAddress3()));
			}
//			regMaster.put("repName", rep.getName() + "\n" +
//					(rep.getAddress1() == null ? "--" : rep.getAddress1()) + "\n" +
//					(rep.getAddress2() == null ? "--" : rep.getAddress2()) + "\n" +
//					(rep.getAddress3() == null ? "--" : rep.getAddress3()));
		} else {
			params.put("noRP", "T");
		}
		List<Map<String, ?>> dataList = new ArrayList<Map<String, ?>>();
		List<Owner> owners = getOwners(applNo, reportDate);
		regMaster.put("demiseDetails", "");
		regMaster.put("ownerDetails", "");
		for (int i = 0; i < owners.size(); i++) {
			Owner owner = owners.get(i);
			boolean demise = Owner.TYPE_DEMISE.equals(owner.getType());
			if (!demise && (owner.getIntMixed() == null || owner.getIntMixed().intValue() <= 0)
					&& (owner.getIntNumberator() == null || owner.getIntNumberator() <= 0)) {
				continue;
			}
			if (demise && (owner.getCharterEdate() == null || owner.getCharterSdate() == null)) {
				continue;
			}
			String ownerAddr = (owner.getAddress1() != null ? owner.getAddress1() :"") +
					(owner.getAddress2()!=null && !owner.getAddress2().isEmpty() ? "\n" + owner.getAddress2() : "") +
					(owner.getAddress3()!=null  && !owner.getAddress3().isEmpty() ? "\n" + owner.getAddress3() : "");
			String ownerAddrConc = (owner.getAddress1() != null ? owner.getAddress1() :"") +
					(StringUtils.isNotBlank(owner.getAddress2()) ? " "+owner.getAddress2().trim() : "") +
					(StringUtils.isNotBlank(owner.getAddress3()) ? " "+owner.getAddress3().trim() : "");
			if (demise) {
				regMaster.put("demiseEDate", owner.getCharterEdate());
				regMaster.put("demiseSDate", owner.getCharterSdate());
				//regMaster.put("demisePlace", owner.getIncorpPlace());
				regMaster.put("demisePlace", "HONG KONG");
				// cod print dc address in one line
				regMaster.put("demiseDetails", owner.getName() + "\n" + (isCod ? ownerAddrConc : ownerAddr));
			} else {
				HashMap<String, Object> subreportRow = new HashMap<>();
				subreportRow.put("ownerName", owner.getName());
				subreportRow.put("ownerStatus", owner.getStatus());
				// SSRS-210 address in three lines
				subreportRow.put("ownerAddress", ownerAddr);

				subreportRow.put("ownerAddressConc", ownerAddrConc);
				// 2019.07.16 remove output of percentage and place of incorporation
				// add again by jira SSRS-104
				subreportRow.put("percentage", owner.getIntMixed() == null ?
						(owner.getIntNumberator() != null ? owner.getIntNumberator() : "-") + "/" +
						(owner.getIntDenominator() != null ? owner.getIntDenominator() : "-") :
							owner.getIntMixed().toString());
				subreportRow.put("placeOfIncorp", evaluatePlaceOfIncorp(owner));
				dataList.add(subreportRow);
				String ownerStr = owner.getName() != null?  (owner.getName() + "\n" + ownerAddr) : "";
				regMaster.put("ownerDetails", ownerStr);
				regMaster.put("ownerAddrConc", ownerAddrConc);
				regMaster.put("ownerName", owner.getName());
			}
		}
		regMaster.put("owners", dataList);
		processMortgages(applNo, reportDate, printMortgage, regMaster, owners, isPercentage);

		Map<String, Object> signatureSection = new HashMap<>();
		if (regM.getApplNoSuf().equals("P")) {
			Date expiryDate = regM.getProvExpDate();
			signatureSection.put("bProReg", true);
			signatureSection.put("proRegExpiryDateEng", "Provisional Registry Expiry Date "
					+ new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(expiryDate).toUpperCase());
			signatureSection.put("proRegExpiryDateChi", "臨時註冊屆滿日期: "
					//+ new SimpleDateFormat("y\u5e74M\u6708d\u65e5HH\u6642mm\u5206", Locale.CHINESE).format(expiryDate));
					+ new SimpleDateFormat("y\u5e74M\u6708d\u65e5", Locale.CHINESE).format(expiryDate));
		} else {
			signatureSection.put("bProReg", false);
			signatureSection.put("proRegExpiryDateEng", " ");
			signatureSection.put("proRegExpiryDateChi", " ");
		}
		signatureSection.put("shipName", regMaster.get("shipName"));
		signatureSection.put("officialNo", regMaster.get("officialNo"));
		signatureSection.put("mortgages", regMaster.get("mortgages"));
		signatureSection.put("regMaster", regMaster.get("regMaster"));
		signatureSection.put("reason", regMaster.get("reason"));
		signatureSection.put("reasonChi", regMaster.get("reasonChi"));
		List signatureDs =  new ArrayList();
		signatureDs.add(signatureSection);
		regMaster.put("subreportDataSource", signatureDs);

		return regMaster;
	}

	protected List<Owner> getOwners(String applNo, Date reportDate) {
		List<Owner> owners = service.findOwners(applNo, reportDate);
		return owners;
	}

	protected Representative getRep(String applNo, Date reportDate) {
		Representative rep = service.findRepById(applNo, reportDate);
		return rep;
	}

	protected List<BuilderMaker> getBuilders(String applNo, Date reportDate) {
		List<BuilderMaker> blders = service.findBuilders(applNo, reportDate);
		return blders;
	}

	protected RegMaster getRegMaster(String applNo, Date reportDate) {
		RegMaster regM = service.findById(applNo, reportDate);
		return regM;
	}

	/**
	 *
	 * @param applNo
	 * @return Map: key is mortgage code(A, B, C...), Value is
	 */
	protected Map<String, String> getTransactionMap(List<Transaction> tranactionList){
		Map<String, String> mortgageDateStrMap=new HashMap<>();
		SimpleDateFormat rptDf = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
		for(Transaction transaction:tranactionList){
			String transactionDetails = transaction.getDetails();
			if(transactionDetails!=null && transactionDetails.length()>10 &&
					(transactionDetails.startsWith("MORTGAGE ") || transactionDetails.startsWith("TRANSFER OF MORTGAGE "))){
				String code = "";
				if (transactionDetails.startsWith("MORTGAGE ")) {
					code = StringUtils.substring(transactionDetails, 10, 11);
				}
				//else if (transactionDetails.startsWith("TRANSFER OF MORTGAGE ")){
				//	code = StringUtils.substring(transactionDetails, 0, 22);
				//}
				Date date = transaction.getDateChange();
				String hourChange = transaction.getHourChange();
				if(!mortgageDateStrMap.containsKey(code) && date!=null && StringUtils.length(hourChange)==4){
					String regDateStr = rptDf.format(date)+" "+StringUtils.substring(hourChange, 0, 2)+":"+StringUtils.substring(hourChange, 2, 4);
					mortgageDateStrMap.put(code, regDateStr);
				}
			}
		}
		return mortgageDateStrMap;
	}

	protected void processMortgages(String applNo, Date reportDate, boolean printMortgage,
			HashMap<Object, Object> regMaster, List<Owner> owners, boolean isPercentage) throws ParseException {
		if (printMortgage) {
			List<Map<String, ?>> dataList2 = new ArrayList<Map<String, ?>>();
			List<Mortgage> mortgages = getMortgage(applNo, reportDate);

			List<Transaction> transactionList = transactionDao.findForMortgage(applNo);

			Map<String, String> transactionMap = getTransactionMap(transactionList);
//			Map<String, String> natures = mortgageSrv.findMortgageRegDateNatures(applNo);
//			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-ddHHmm");
			SimpleDateFormat rptDf = new SimpleDateFormat("dd-MMM-yyyy HH:mm", Locale.ENGLISH);
			int j=0;
			for (int i = 0; i < mortgages.size(); i++) {
				Mortgage mortgage = mortgages.get(i);
				Date regTime = mortgage.getRegTime();
				if (regTime == null || Mortgage.STATUS_DISCHARGED.equals(mortgage.getMortStatus()) || Mortgage.STATUS_CANCELLED.equals(mortgage.getMortStatus())) {
					continue;
				}
				j++;
				HashMap<String, Object> subreportRow = new HashMap<>();
				String mortgageCode = mortgage.getPriorityCode();
				subreportRow.put("mortgageCode", mortgageCode);
				String pc;
				switch (j) {
				case 1:
					pc = "(1st Priority Code)";
					break;
				case 2:
					pc = "(2nd Priority Code)";
					break;
				case 3:
					pc = "(3rd Priority Code)";
					break;
				default:
					pc = "(" + (j) + "th Priority Code)";
					break;
				}
				subreportRow.put("priorityCode", pc);
				List<Mortgagor> mortgagors = getMortgagors(reportDate, mortgage);
				StringBuilder name = new StringBuilder();
				StringBuilder place = new StringBuilder();
				DecimalFormat intFormat = new DecimalFormat("#");
				mortgagors.forEach(m -> {
					for (Owner owner : owners) {
						if (owner.getOwnerSeqNo().equals(m.getSeq())) {
							name.append(owner.getName()).append(", ");
							//place.append(owner.getRegPlace() != null ? owner.getRegPlace() : owner.getIncorpPlace()).append(", ");
							String placeOfIncorp = evaluatePlaceOfIncorp(owner);
							place.append(placeOfIncorp).append(", ");
							if (isPercentage) {
							subreportRow.put("propertyOfShip", intFormat.format(owner.getIntMixed() != null ? owner.getIntMixed() : BigDecimal.ZERO) + " Percentage");
							} else {
								subreportRow.put("propertyOfShip", intFormat.format(owner.getIntMixed() != null ? owner.getIntMixed() : BigDecimal.ZERO) + " Shares");								
							}
							break;
						}
					}
				});

				if (name.length() >= 2) {
					name.delete(name.length() - 2, name.length());
				}
				if (place.length() >= 2) {
					place.delete(place.length() - 2, place.length());
				}
				subreportRow.put("mortgagorName", name.toString());
				subreportRow.put("placeOfIncorp", place.toString());

				List<Mortgagee> mortgagees = getMortgagees(reportDate, mortgage);
				StringBuilder geeNames = new StringBuilder();
				StringBuilder addresses = new StringBuilder();
				StringBuilder tel = new StringBuilder();
				StringBuilder fax = new StringBuilder();
				StringBuilder email = new StringBuilder();

				for (Mortgagee mortgagee : mortgagees) {
					geeNames.append(mortgagee.getName()).append(", ");
					addresses.append(getAddress(mortgagee.getAddress1(), mortgagee.getAddress2(), mortgagee.getAddress3())).append("\n");
					tel.append(isNull(mortgagee.getTelNo(), "-")).append(", ");
					fax.append(isNull(mortgagee.getFaxNo(), "-")).append(", ");
					email.append(isNull(mortgagee.getEmail(), "-")).append(", ");
				}
				if (geeNames.length() >= 2) {
					geeNames.delete(geeNames.length() - 2, geeNames.length());
				}
				if (addresses.length() >= 1) {
					addresses.delete(addresses.length() - 1, addresses.length());
				}
				if (tel.length() >= 2) {
					tel.delete(tel.length() - 2, tel.length());
				}
				if (fax.length() >= 2) {
					fax.delete(fax.length() - 2, fax.length());
				}
				if (email.length() >= 2) {
					email.delete(email.length() - 2, email.length());
				}
				subreportRow.put("mortgageeName", geeNames.toString());
				subreportRow.put("mortgageeAddress", addresses.toString());
				subreportRow.put("telNo", tel.toString());
				subreportRow.put("faxNo", fax.toString());
				subreportRow.put("email", email.toString());

				subreportRow.put("mortgageeNameList", prepareMortgageeNameList(mortgagees));
				subreportRow.put("mortgageeTelList", prepareMortgageeTelList(mortgagees));
				subreportRow.put("mortgageeTelDataList", prepareMortgageeTelDataList(mortgagees));

				String regDateStr="";
				if(transactionMap.containsKey(mortgageCode)){
					regDateStr = transactionMap.get(mortgageCode);
				}else{
					logger.warn("No Reg DateTime for MortgageCode:{}", mortgageCode);
					Date date = mortgage.getRegTime();
					regDateStr = rptDf.format(date);
				}
				subreportRow.put("regDate", regDateStr.toUpperCase());
				subreportRow.put("nature", mortgage.getAgreeTxt());
				subreportRow.put("bTransferLine", false);
				subreportRow.put("transferRegDate", "");	// set default value for transfer reg date and transfer detail
				subreportRow.put("transferDetail", "");

				//if (transactionMap.containsKey("TRANSFER OF MORTGAGE ")) {
				subreportRow.put("transferRegDate", "");
				subreportRow.put("transferDetail", "");
					for (Transaction t : transactionList) {
						if ("34".equals(t.getTransactionCode())) { // transfer
							String priorityCode = StringUtils.substring(t.getDetails(), 22, 23);
							//if (StringUtils.substring(t.getDetails(), 22, 23)==mortgageCode) {
							if (priorityCode.equals(mortgageCode)) {
								Date dateChange = t.getDateChange();
								String dateHour = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(dateChange);
								String hour = StringUtils.substring(t.getHourChange(), 0, 2);
								String minute = StringUtils.substring(t.getHourChange(), 2, 4);
								dateHour = dateHour.toUpperCase() + " " + hour + ":" + minute;
								subreportRow.put("transferRegDate", dateHour);
								subreportRow.put("transferDetail", t.getDetails());
								break;
							}
						}
					}
				//}
				dataList2.add(subreportRow);
			}
			regMaster.put("mortgages", dataList2);
		}
	}

	protected List<Mortgagee> getMortgagees(Date reportDate, Mortgage mortgage) {
		return mortgageSrv.findMortgagees(mortgage, reportDate);
	}

	protected List<Mortgage> getMortgage(String applNo, Date reportDate) {
		return mortgageSrv.findMortgages(applNo, reportDate);
	}

	protected List<Mortgagor> getMortgagors(Date reportDate, Mortgage mortgage) {
		return mortgageSrv.findMortgagors(mortgage, reportDate);
	}

	private String getAddress(String... lines) {
		String result = "";
		for (String line : lines) {
			if (line != null) {
				result += "" + line+"\n";
			}
		}
		return result.trim();
	}

	private Object isNull(Object value, Object replacement) {
		return value != null ? value : replacement;
	}

}
