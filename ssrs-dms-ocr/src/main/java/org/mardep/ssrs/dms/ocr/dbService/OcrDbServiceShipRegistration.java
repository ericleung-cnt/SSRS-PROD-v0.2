package org.mardep.ssrs.dms.ocr.dbService;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.mardep.ssrs.dms.ocr.xml.OcrXmlShipRegistration;
import org.mardep.ssrs.domain.sr.ApplDetail;
import org.mardep.ssrs.domain.sr.Owner;
import org.mardep.ssrs.domain.sr.RegMaster;
import org.mardep.ssrs.domain.sr.Representative;
import org.mardep.ssrs.domain.user.User;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.mardep.ssrs.service.IShipRegService;
import org.mardep.ssrs.vitaldoc.IVitalDocClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class OcrDbServiceShipRegistration implements IOcrDbServiceShipRegistration {

//	@Autowired
//	protected IOcrRegMasterDao regMasterDao;

	@Autowired
	protected IShipRegService shipRegService;
	@Autowired IVitalDocClient vd;

	@Override
	public void save(OcrXmlShipRegistration xml, byte[] pdf) throws IOException, ParseException {
		// TODO Auto-generated method stub
		User user = new User();
		user.setId("OCR");
		UserContextThreadLocalHolder.setCurrentUser(user);

		List<Owner> owners = new ArrayList<Owner>();

		RegMaster regMaster = new RegMaster();
		regMaster.setRegName(xml.getShipName());
		regMaster.setRegChiName(xml.getChiShipName());
		regMaster.setImoNo(xml.getImoNo());
		regMaster.setGrossTon(xml.getGrossTon());
		regMaster.setRegNetTon(xml.getNetTon());
		
		ApplDetail applDetail = new ApplDetail();
		applDetail.setPrevName(xml.getPresentName());
		applDetail.setPrevPort(xml.getPresentPort());
		applDetail.setPreOffNo(xml.getPrevOfficialNo());
		applDetail.setProposeRegDate(xml.getProposedRegDate());
		//applDetail.setClassSociety(xml.getClassSociety());

		if (xml.getOwnerName()!=null && !xml.getOwnerName().isEmpty()) {
			Owner owner = new Owner();
			owner.setName(xml.getOwnerName());
			owner.setAddress1(xml.getOwnerAddress());
			owner.setType("C");
			owner.setIntMixed(xml.getShare());
			owners.add(owner);
		}
		if (xml.getDemiseName()!=null && !xml.getDemiseName().isEmpty()) {
			Owner demise = new Owner();
			demise.setName(xml.getDemiseName());
			demise.setType(Owner.TYPE_DEMISE);
			owners.add(demise);
		}

		Representative rp = new Representative();
		rp.setName(xml.getRpNameAndAddress());
		rp.setTelNo(xml.getRpTel());
		rp.setFaxNo(xml.getRpFax());
		rp.setEmail(xml.getRpEmail());
		rp.setStatus(Representative.STATUS_CORPORATION);

		shipRegService.create(regMaster, applDetail, owners, rp);
		long docId = vd.uploadShipRegApp(xml.getPdfName(), xml.getShipName(), xml.getImoNo(), xml.getOwnerName(), xml.getDemiseName(), xml.getRpNameAndAddress(), pdf);
		if (docId <= 0) {
			throw new IOException("upload failure");
		}
	}
}
