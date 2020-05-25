package org.mardep.ssrs.report.generator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mardep.ssrs.dao.codetable.IClassSocietyDao;
import org.mardep.ssrs.dao.codetable.IFeeCodeDao;
import org.mardep.ssrs.dao.sr.ICsrFormOwnerDao;
import org.mardep.ssrs.dao.sr.IRegMasterDao;
import org.mardep.ssrs.domain.codetable.ClassSociety;
import org.mardep.ssrs.domain.codetable.FeeCode;
import org.mardep.ssrs.domain.dn.DemandNoteItem;
import org.mardep.ssrs.domain.sr.CsrFormOwner;
import org.mardep.ssrs.domain.sr.Owner;
import org.mardep.ssrs.domain.sr.RegMaster;
import org.mardep.ssrs.report.IReportGenerator;
import org.mardep.ssrs.service.IDemandNoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("CSRForm")
public class CSRFormGenerator extends AbstractReportGenerator implements IReportGenerator {

	private final String CSR_FEE_CODE = "50";

	@Override
	public String getReportFileName() {
		return "CSRForm.jrxml";
	}


	@Autowired
	ICsrFormOwnerDao ownerDao;
	@Autowired
	IClassSocietyDao classDao;
	@Autowired
	IDemandNoteService demandNoteService;

	@Autowired
	IFeeCodeDao fcDao;

	@Autowired
	IRegMasterDao rmDao;

	@Override
	public byte[] generate(Map<String, Object> inputParam) throws Exception {
		String imoNo = (String) inputParam.get("imoNo");
		int formSeq = ((Number) inputParam.get("formSeq")).intValue();
		String applNo = (String) inputParam.get("applNo");

		HashMap<String, Object> params = new HashMap<>();
		if (Boolean.TRUE.equals(inputParam.get("paymentRequired"))) {
			FeeCode code = fcDao.findById(CSR_FEE_CODE);
			Date generationTime = new Date();
			RegMaster rm = new RegMaster();
			//rm.setImoNo(imoNo);
			rm.setApplNo(applNo);
			List<RegMaster> list = rmDao.findByCriteria(rm);
			if (!list.isEmpty()) {
				list.sort((a,b) -> {
					Date regDateB = b.getRegDate();
					Date regDateA = a.getRegDate();
				return (int) ((regDateB == null ? Integer.MAX_VALUE : regDateB.getTime()) - (regDateA == null ? Integer.MAX_VALUE : regDateA.getTime())); } );
				//String applNo = list.get(0).getApplNo();
				DemandNoteItem entity = new DemandNoteItem();
				entity.setActive(true);
				entity.setAmount(code.getFeePrice());
				entity.setApplNo(applNo);
				entity.setChargedUnits(1);
				entity.setFcFeeCode(code.getId());
				entity.setGenerationTime(generationTime);
				entity.setAdhocDemandNoteText(""+formSeq);
				demandNoteService.addItem(entity);
			}
		}

//		{
		/*
		docAudit=null,
				smcAuthority=null,
		 * srApproved=Tue May 14 00:00:00 GMT+08:00 2019,
		 * registrarName=null,
		 * owners=[],
		 * csrIssued=null, csrIssueDate=null, isscAudit=null,
		 * formerFlagReminded=true,
		 * fsqcConfirmed=null, csrRemarks=null,
		 * sqaUpdateRequired=true, updatedBy=SYSTEM,
		 * portfolioReceived=Wed Feb 13 00:00:00 GMT+08:00 2019,
		 * applNo=1999/001,
		 * shipManagerAddress3=df,
		 * docAuthority=null, formerFlagRequested=null,
		 * shipManagerAddress1=a1,
		 * shipManagerAddress2=s2, createdBy=SYSTEM,
		 * shipManager=DDD,
		 *  formAccepted=null, safetyActAddress2=null,
		 * smcAudit=null,
		 *  companyCopyReceived=null,
		 *  deregDate=Tue Jan 29 00:00:00 GMT+08:00 2019,
		 *  isscAuthority=null,
		 *  csrCollected=Fri Feb 22 00:00:00 GMT+08:00 2019,
		 *  revisedRequired=null
		 * */
		params.put("formApplyDate", inputParam.get("formApplyDate"));
		params.put("registrationDate", inputParam.get("registrationDate"));
		params.put("deregDate", inputParam.get("deregDate"));
		params.put("formNo", Long.valueOf(1).equals(inputParam.get("formSeq")) ? "1" : "2");
		params.put("shipName", inputParam.get("shipName"));
		CsrFormOwner search = new CsrFormOwner();
		search.setImoNo(imoNo);
		search.setFormSeq(formSeq);
		List<CsrFormOwner> ownerList = ownerDao.findByCriteria(search);
		HashMap<String, Object> single = new HashMap<String, Object>();

		List<Map<String, Object>> subreportOwner = new ArrayList<>();
		single.put("ownerList", subreportOwner);
		params.put("SUBREPORT_1", jasperReportService.getJasperReport("CSRFormOwners.jrxml"));

		String owner = "";
		String demise = "";
		if (!ownerList.isEmpty()) {
			for (CsrFormOwner csrOwner : ownerList) {
				String ownerName = csrOwner.getOwnerName();
				String address1 = csrOwner.getAddress1();
				String address2 = csrOwner.getAddress2();
				String address3 = csrOwner.getAddress3();
				if (Owner.TYPE_DEMISE.equals(csrOwner.getOwnerType())) {
					demise = ownerName + "\n";
					if (address1 != null) {
						demise += "\n" + address1;
					}
					if (address2 != null) {
						demise += "\n" + address2;
					}
					if (address3 != null) {
						demise += "\n" + address3;
					}
				} else {
					Map<String, Object> row = new HashMap<>();
					row.put("ownerName", ownerName);
					row.put("address1", address1);
					row.put("address2", address2);
					row.put("address3", address3);
					subreportOwner.add(row);
					if (owner != null && !owner.isEmpty()) {
						owner += "\n\n";
					}
					owner += ownerName;
					if (address1 != null) {
						owner += "\n" + address1;
					}
					if (address2 != null) {
						owner += "\n" + address2;
					}
					if (address3 != null) {
						owner += "\n" + address3;
					}
				}
			}
		} else {
			owner = "N/A";
		}
		if (owner == null || owner.isEmpty()) {
			owner = "N/A";
		}
		if (demise == null || demise.isEmpty()) {
			demise = "N/A";
		}
		params.put("owner", owner);
		//params.put("ownerId", "N/A"); // TODO //
		params.put("ownerId", inputParam.get("imoOwnerId") == null ? "N/A" : inputParam.get("imoOwnerId"));
		params.put("demise", demise);
		params.put("safety", getSafetyAddress(inputParam));
		String classSocietyId = (String) inputParam.get("classSocietyId");
		String classSoc = null;
		if (classSocietyId != null) {
			ClassSociety cs = classDao.findById(classSocietyId);
			classSoc = cs.getName();
		}
		String classSociety2 = (String) inputParam.get("classSociety2");
		if (classSociety2 != null) {
			ClassSociety cs2 = classDao.findById(classSociety2);
			if (cs2 != null) {
				if (classSoc == null) {
					classSoc = cs2.getName();
				} else {
					classSoc += "\n" + cs2.getName();
				}
			}
		}
		params.put("classSoc", classSoc == null ? "N/A" : classSoc);

		String docAuthority = (String) inputParam.get("docAuthority");
		String doc = null;
		if (docAuthority != null) {
			ClassSociety cs = classDao.findById(docAuthority);
			doc = cs.getName();
		}
		params.put("compliance", doc == null ? "N/A" : doc);

		String smcAuthority = (String) inputParam.get("smcAuthority");
		String smc = null;
		if (smcAuthority != null) {
			ClassSociety cs = classDao.findById(smcAuthority);
			smc = cs.getName();
		}
		params.put("safetySoc", smc == null ? "N/A" : smc);

		String isscAuthority = (String) inputParam.get("isscAuthority");
		String issc = null;
		if (isscAuthority != null) {
			ClassSociety cs = classDao.findById(isscAuthority);
			issc = cs.getName();
		}

		params.put("iss", issc == null ? "N/A" : issc);
		Object remark = inputParam.get("remark");
		params.put("remark", remark == null ? "N/A" : remark);
		//params.put("safetyId", "N/A"); // TODO //
		params.put("safetyId", inputParam.get("imoCompanyId") == null ? "N/A" : inputParam.get("imoCompanyId"));
		params.put("imoNo", inputParam.get("imoNo"));
		params.put("formSeq", inputParam.get("formSeq"));
		params.put("registrar", inputParam.get("registrarName"));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		Object inputDate = inputParam.get("inputDate");
		if (inputDate == null) {
			inputDate = new Date();
		}
		params.put("issueDate", sdf.format(inputDate));
		for (String code : new String[]{"docAuthority", "docAudit",
				"smcAuthority", "smcAudit",
				"isscAuthority", "isscAudit",
				}) {
			String key = (String) inputParam.get(code);
			if (key != null) {
				ClassSociety cs = classDao.findById(key);
				if (cs != null) {
					params.put(code, cs.getName());
				}
			}
		}
		return super.generate(Arrays.asList(single), params);
	}

	private String getSafetyAddress(Map<String, Object> inputParam) {
		String addr = (inputParam.get("shipManager") != null ? inputParam.get("shipManager") : "") + "\n\n" +
				(inputParam.get("shipManagerAddress1") != null ? inputParam.get("shipManagerAddress1") : "") + "\n" +
				(inputParam.get("shipManagerAddress2") != null ? inputParam.get("shipManagerAddress2") : "") + "\n" +
				(inputParam.get("shipManagerAddress3") != null ? inputParam.get("shipManagerAddress3") : "") + "\n\n" +
				(inputParam.get("safetyActAddress1") != null ? inputParam.get("safetyActAddress1") : "") + "\n" +
				(inputParam.get("safetyActAddress2") != null ? inputParam.get("safetyActAddress2") : "") + "\n" +
				(inputParam.get("safetyActAddress3") != null ? inputParam.get("safetyActAddress3") : "") ;

		return addr;
	}

}
