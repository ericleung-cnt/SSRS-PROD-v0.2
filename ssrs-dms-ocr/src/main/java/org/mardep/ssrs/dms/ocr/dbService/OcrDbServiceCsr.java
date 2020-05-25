package org.mardep.ssrs.dms.ocr.dbService;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import javax.transaction.Transactional;

import org.mardep.ssrs.dao.ocr.IOcrRegMasterDao;
import org.mardep.ssrs.dms.ocr.xml.OcrXmlCsrForm2;
import org.mardep.ssrs.dms.ocr.xml.OcrXmlCsrInitial;
import org.mardep.ssrs.domain.sr.CsrForm;
import org.mardep.ssrs.domain.sr.CsrFormOwner;
import org.mardep.ssrs.domain.sr.RegMaster;
import org.mardep.ssrs.domain.user.User;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.mardep.ssrs.service.ICsrService;
import org.mardep.ssrs.vitaldoc.IVitalDocClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class OcrDbServiceCsr implements IOcrDbServiceCsr {

	@Autowired
	protected ICsrService csrService;

	@Autowired
	protected IOcrRegMasterDao regMasterDao;

	@Autowired
	IVitalDocClient vd;

	@Override
	public void save(OcrXmlCsrInitial xml, byte[] pdf) throws IOException {
		// TODO Auto-generated method stub
		User user = new User();
		user.setId("OCR");
		UserContextThreadLocalHolder.setCurrentUser(user);

		RegMaster entityRegMaster = regMasterDao.getByShipNameAndIMO(xml.getShipName(),  xml.getImoNo());
		if (entityRegMaster!=null) {
			CsrForm entity = new CsrForm();
			entity.setImoNo(xml.getImoNo());
			entity.setFormSeq(1);
			entity.setApplNo(entityRegMaster.getApplNo());
			entity.setRegistrationDate(entityRegMaster.getRegDate());
			entity.setShipName(xml.getShipName());
			entity.setOwners(new ArrayList<CsrFormOwner>());
			long docId = vd.uploadCsr(xml.getPdfName(), xml.getImoNo(), xml.getShipName(), pdf);
			if (docId <= 0) {
				throw new IOException("upload failure");
			}
			csrService.save(entity);
		}
	}

	@Override
	public void save(OcrXmlCsrForm2 xml, byte[] pdf) throws IOException, ParseException {
		// TODO Auto-generated method stub
		User user = new User();
		user.setId("OCR");
		UserContextThreadLocalHolder.setCurrentUser(user);

		RegMaster entityRegMaster = regMasterDao.getByShipNameAndIMO(xml.getShipName(),  xml.getImoNo());
		if (entityRegMaster!=null) {
			CsrForm latestEntity = csrService.getLast(xml.getImoNo());
			CsrForm entity = new CsrForm();
			entity.setImoNo(xml.getImoNo());
			entity.setFormSeq(latestEntity.getFormSeq()+1);
			entity.setApplNo(entityRegMaster.getApplNo());
			entity.setRegistrationDate(entityRegMaster.getRegDate());
			entity.setShipName(xml.getShipName());
			entity.setFormApplyDate(xml.getFormApplyDate());
			entity.setOwners(new ArrayList<CsrFormOwner>());
			long docId = vd.uploadCsr(xml.getPdfName(), xml.getImoNo(), xml.getShipName(), pdf);
			if (docId <= 0) {
				throw new IOException("upload failure");
			}
			csrService.save(entity);
		}
	}

}
