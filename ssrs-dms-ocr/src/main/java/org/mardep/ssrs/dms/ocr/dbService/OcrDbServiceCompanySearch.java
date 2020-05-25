package org.mardep.ssrs.dms.ocr.dbService;

import java.io.IOException;
import java.text.ParseException;

import javax.transaction.Transactional;

import org.mardep.ssrs.dao.ocr.IOcrCompanySearchDao;
import org.mardep.ssrs.dms.ocr.xml.OcrXmlCompanySearch;
import org.mardep.ssrs.domain.ocr.OcrEntityCompanySearch;
import org.mardep.ssrs.domain.user.User;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.mardep.ssrs.vitaldoc.IVitalDocClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class OcrDbServiceCompanySearch implements IOcrDbServiceCompanySearch {

	@Autowired
	IOcrCompanySearchDao dao;

	@Autowired
	IVitalDocClient vd;

	@Override
	public OcrEntityCompanySearch save(OcrXmlCompanySearch xml, byte[] pdf ) throws IOException, ParseException {
		User user = new User();
		user.setId("OCR");
		UserContextThreadLocalHolder.setCurrentUser(user);

		OcrEntityCompanySearch entity = dao.getByCrNumber(xml.getCrNumber());
		if (entity==null) {
			entity = new OcrEntityCompanySearch();
			entity.setCrNumber(xml.getCrNumber());
		}
		entity.setCheckDate(xml.getCheckDate());
		entity.setCompanyName(xml.getCompanyName());
		entity.setPlaceOfIncorporation(xml.getPlaceOfIncorporaion());
		entity.setRegisteredOffice(xml.getRegisteredOffice());
		// TODO fix the docName
		long docId = vd.uploadCompanySearch(xml.getPdfName(), entity.getCrNumber(), entity.getCompanyName(), entity.getPlaceOfIncorporation(), entity.getRegisteredOffice(), entity.getCheckDate(), pdf);
		if (docId <= 0) {
			throw new IOException("upload failure");
		}
		entity.setDmsDocId(docId);
		return dao.save(entity);
	}

}
