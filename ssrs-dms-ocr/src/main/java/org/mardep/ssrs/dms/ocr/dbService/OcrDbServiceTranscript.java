package org.mardep.ssrs.dms.ocr.dbService;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import javax.transaction.Transactional;

import org.mardep.ssrs.dao.ocr.IOcrTranscriptDao;
import org.mardep.ssrs.dms.ocr.service.IOcrBaseService;
import org.mardep.ssrs.dms.ocr.xml.OcrXmlTranscript;
import org.mardep.ssrs.dms.ocr.xml.OcrXmlTranscript_Ship;
import org.mardep.ssrs.domain.ocr.OcrEntityTranscript;
import org.mardep.ssrs.domain.ocr.OcrEntityTranscriptApplicant;
import org.mardep.ssrs.domain.user.User;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.mardep.ssrs.vitaldoc.IVitalDocClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class OcrDbServiceTranscript implements IOcrDbServiceTranscript {

	@Autowired
	IOcrBaseService baseSvc;
	
	@Autowired
	IOcrTranscriptDao dao;

	@Autowired
	IOcrDbServiceTranscriptApplicant applicantSvc;
	
	@Autowired
	IVitalDocClient vd;

	@Override
	public void save(OcrXmlTranscript xml, byte[] pdf) throws IOException, ParseException {
		// TODO Auto-generated method stub
		User user = new User();
		user.setId("OCR");
		UserContextThreadLocalHolder.setCurrentUser(user);

		String address = xml.getAddress();
		String applicantCompanyName = xml.getApplicantCompanyName();
		String contactPerson = xml.getContactPerson();
		String email = xml.getEmail();
		Date forDate = xml.getApplyForDate();
		String issueType = xml.getIssueType();
		String tel = xml.getTel();
		
//		OcrEntityTranscript entity = new OcrEntityTranscript();
//		entity.setAddress(xml.getAddress());
//		entity.setApplicantCompanyName(xml.getApplicantCompanyName());
//		entity.setContactPerson(xml.getContactPerson());
//		entity.setEmail(xml.getEmail());
//		entity.setForDate(xml.getApplyForDate());
//		entity.setImoNumber(xml.getImoNumber());
//		entity.setIssueType(xml.getIssueType());
//		entity.setOfficialNumber(xml.getOfficialNumber());
//		entity.setTel(xml.getTel());
//		entity.setVesselName(xml.getVesselName());
		
		OcrEntityTranscript entity = constructEntity(address, applicantCompanyName, contactPerson, email,
				forDate, issueType, tel, xml.getImoNumber(), xml.getOfficialNumber(), xml.getVesselName());
		
		if (baseSvc.getDmsEnabled()) {
			long docId = vd.uploadRequestTranscript(xml.getPdfName(), entity.getVesselName(), entity.getOfficialNumber(), entity.getImoNumber(), entity.getForDate(), entity.getApplicantCompanyName(), pdf);
			if (docId < 0) {
				throw new IOException("upload failure");
			}			
			entity.setDmsDocid(docId);
		}
		dao.save(entity);
		if (xml.getShipList().size()>0) {
			for (OcrXmlTranscript_Ship ship:xml.getShipList()) {
				OcrEntityTranscript shipEntity = constructEntity(address, applicantCompanyName, contactPerson, email,
						forDate, issueType, tel, ship.getImoNumber(), ship.getOfficialNumber(), ship.getShipName());
				dao.save(shipEntity);
			}
		}
		//return dao.save(entity);
		if (baseSvc.getOcrTranscriptApplicantEnabled()) {
			OcrEntityTranscriptApplicant applicant = constructApplicant(address, applicantCompanyName, contactPerson, email, tel); 
			applicantSvc.saveIfNotExist(applicant);
		}
	}

	private OcrEntityTranscript constructEntity(String address, String applicantCompanyName, String contactPerson, String email, 
			Date forDate, String issueType, String tel, String imoNumber, String officialNumber, String vesselName) {
		OcrEntityTranscript entity = new OcrEntityTranscript();
		entity.setAddress(address);
		entity.setApplicantCompanyName(applicantCompanyName);
		entity.setContactPerson(contactPerson);
		entity.setEmail(email);
		entity.setForDate(forDate);
		entity.setImoNumber(imoNumber);
		entity.setIssueType(issueType);
		entity.setOfficialNumber(officialNumber);
		entity.setTel(tel);
		entity.setVesselName(vesselName);
		
		return entity;
	}
	
	private OcrEntityTranscriptApplicant constructApplicant(String address, String applicantCompanyName, String contactPerson, String email, String tel) {
		OcrEntityTranscriptApplicant applicant = new OcrEntityTranscriptApplicant();
		applicant.setAddress(address);
		applicant.setApplicantCompanyName(applicantCompanyName);
		applicant.setContactPerson(contactPerson);
		applicant.setEmail(email);
		applicant.setTel(tel);
		
		return applicant;
	}
}