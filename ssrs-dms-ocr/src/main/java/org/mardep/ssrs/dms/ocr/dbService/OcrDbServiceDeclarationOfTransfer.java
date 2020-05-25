package org.mardep.ssrs.dms.ocr.dbService;

import java.io.IOException;
import java.util.List;

import javax.transaction.Transactional;

import org.mardep.ssrs.dao.ocr.IOcrOwnerDao;
import org.mardep.ssrs.dao.ocr.IOcrRegMasterDao;
import org.mardep.ssrs.dms.ocr.xml.OcrXmlDeclarationOfTransfer;
import org.mardep.ssrs.domain.sr.Owner;
import org.mardep.ssrs.domain.sr.RegMaster;
import org.mardep.ssrs.domain.user.User;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.mardep.ssrs.vitaldoc.IVitalDocClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class OcrDbServiceDeclarationOfTransfer implements IOcrDbServiceDeclarationOfTransfer {


	@Autowired
	protected IOcrRegMasterDao regMasterDao;
	
	@Autowired
	protected IOcrOwnerDao ownerDao;
	
	@Autowired
	IVitalDocClient vd;
	
	@Override
	public void save(OcrXmlDeclarationOfTransfer xml, byte[] pdf) throws IOException {
		// TODO Auto-generated method stub
		User user = new User();
		user.setId("OCR");
		UserContextThreadLocalHolder.setCurrentUser(user);
		
		RegMaster entityRegMaster = regMasterDao.getByShipNameAndOfficialNo(xml.getShipName(), xml.getOfficialNumber());
		if (entityRegMaster!=null) {
			List<Owner> owners = ownerDao.getByApplNo(entityRegMaster.getApplNo());
			int nextSeqNo = 1;
			if (owners!=null && owners.size()>0) {
				nextSeqNo = owners.get(owners.size()-1).getOwnerSeqNo() + 1;
			} 
			Owner owner = new Owner();	
			owner.setApplNo(entityRegMaster.getApplNo());
			owner.setOwnerSeqNo(nextSeqNo);
			owner.setName(xml.getTransferee());
			long docId = vd.uploadTransferDecl(xml.getPdfName(), entityRegMaster.getRegName(), entityRegMaster.getOffNo(), xml.getTransferee(), pdf);
			if (docId <= 0) {
				throw new IOException("upload failure");
			}
			
			ownerDao.save(owner);
		}
	}

}
