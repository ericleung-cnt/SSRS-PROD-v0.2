package org.mardep.ssrs.report.generator;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.mardep.ssrs.dao.cert.ICertIssueLogDao;
import org.mardep.ssrs.dao.codetable.IOfficeDao;
import org.mardep.ssrs.domain.constant.CertificateTypeEnum;
import org.mardep.ssrs.domain.constant.Cons;
import org.mardep.ssrs.domain.entity.cert.EntityCertIssueLog;
import org.mardep.ssrs.domain.srReport.TranscriptGrantingOffice;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("RPT_RD_002")
public class RPT_RD_002 extends AbstractSrReport {
	@Autowired
	ICertIssueLogDao certIssueLogDao;
	
	@Autowired
	IOfficeDao officeDao;

	public RPT_RD_002() {
		super("RPT-RD-002.jrxml", null);
	}
	
	@Override
	public byte[] generate(Map<String, Object> inputParam) throws Exception {
		Map<String, Object> params = new HashMap<String, Object>();
		Date reportDateFrom = (Date)inputParam.get("reportDateFrom");
		SimpleDateFormat format = createDateFormat();
		params.put("reportDateFrom", format.format(reportDateFrom));
		Date reportDateTo = (Date)inputParam.get("reportDateTo");
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(reportDateTo);
		cal.add(Calendar.DAY_OF_MONTH,1);
		cal.add(Calendar.SECOND, -1);
		reportDateTo = cal.getTime();
		
		params.put("reportDateTo", format.format(reportDateTo));
		String currentUser = UserContextThreadLocalHolder.getCurrentUserName();
		params.put("userId", currentUser);
		
		List<EntityCertIssueLog> certs = certIssueLogDao.getAllForReport(reportDateFrom, reportDateTo);
		
		Map<String, TranscriptGrantingOffice> offices = getIssueOfficeCount(certs);

		List<Map<String, Object>> rows = new ArrayList<>();		
		
		for(Entry<String, TranscriptGrantingOffice> office: offices.entrySet()) {
				logger.info("issueOfficeEngDesc:{}", office.getKey());
				logger.info("Certifed_Transcript_Count:{}", office.getValue().getCertifiedTranscriptCount());
				logger.info("Uncertifed_Transcript_Count:{}", office.getValue().getUncertifiedTranscriptCount());
				logger.info("Total_Count:{}", office.getValue().getTotalCount());
				logger.info("tel:{}", officeDao.findOfficeTel("SH"));
				
				Map<String, Object> row = new HashMap<>();
				row.put("issueOfficeEngDesc", officeDao.findOfficeName(office.getKey()));
				row.put("Certified_Transcript_Count", office.getValue().getCertifiedTranscriptCount());
				row.put("Uncertified_Transcript_Count", office.getValue().getUncertifiedTranscriptCount());
				row.put("Total_Count", office.getValue().getTotalCount());
				
				rows.add(row);
				logger.info("row:{}", rows);
		}
		return jasperReportService.generateReport(getReportFileName(), rows, params);
	}
	
	private Map<String, TranscriptGrantingOffice> getIssueOfficeCount(List<EntityCertIssueLog> certs){
		List<TranscriptGrantingOffice> offices = new ArrayList();
		Map<String, TranscriptGrantingOffice> officeMap = new HashMap<>();
		for (EntityCertIssueLog cert:certs) {			
			//TranscriptGrantingOffice office = new TranscriptGrantingOffice();
			//office.setIssueOfficeCode(cert.getIssueOffice());
			if(officeMap.containsKey(cert.getIssueOffice())) {
				TranscriptGrantingOffice trans = officeMap.get(cert.getIssueOffice());
				//if ("UNCERTIFIED_TRANSCRIPT".equals(cert.getCertType())) {
				if (CertificateTypeEnum.UNCERTIFIED_TRANSCRIPT.toString().equals(cert.getCertType())) {
					trans.setUncertifiedTranscriptCount(trans.getUncertifiedTranscriptCount()+1);
				} else if (CertificateTypeEnum.CERTIFIED_TRANSCRIPT.toString().equals(cert.getCertType())) {
					trans.setCertifiedTranscriptCount(trans.getCertifiedTranscriptCount()+1);
				}
				trans.setTotalCount(trans.getUncertifiedTranscriptCount()+trans.getCertifiedTranscriptCount());
				
			}else {				
				TranscriptGrantingOffice trans = new TranscriptGrantingOffice();
				if (CertificateTypeEnum.UNCERTIFIED_TRANSCRIPT.toString().equals(cert.getCertType())) {
					trans.setUncertifiedTranscriptCount(1);
				} else if (CertificateTypeEnum.CERTIFIED_TRANSCRIPT.toString().equals(cert.getCertType())) {
					trans.setCertifiedTranscriptCount(1);
				}
				trans.setTotalCount(trans.getUncertifiedTranscriptCount()+trans.getCertifiedTranscriptCount());
				
				officeMap.put(cert.getIssueOffice(), trans);				
			}			
		}
		return officeMap;
	}
}
