package org.mardep.ssrs.dms.ocr.dbService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.mardep.ssrs.dao.ocr.IOcrMortgageDao;
import org.mardep.ssrs.dao.ocr.IOcrOwnerDao;
import org.mardep.ssrs.dao.ocr.IOcrRegMasterDao;
import org.mardep.ssrs.dms.ocr.xml.OcrXmlRegisterMortgage;
import org.mardep.ssrs.domain.sr.Mortgage;
import org.mardep.ssrs.domain.sr.Mortgagee;
import org.mardep.ssrs.domain.sr.Owner;
import org.mardep.ssrs.domain.sr.RegMaster;
import org.mardep.ssrs.domain.user.User;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.mardep.ssrs.service.IMortgageService;
import org.mardep.ssrs.vitaldoc.IVitalDocClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class OcrDbServiceRegisterMortgage implements IOcrDbServiceRegisterMortgage {

	@Autowired
	protected IMortgageService mortgageService;

	@Autowired
	protected IOcrRegMasterDao regMasterDao;

	@Autowired
	protected IOcrMortgageDao mortgageDao;

	@Autowired
	protected IOcrOwnerDao ownerDao;

	@Autowired IVitalDocClient vd;

	@Override
	public void save(OcrXmlRegisterMortgage xml, byte[] pdf) throws IOException {
		// TODO Auto-generated method stub
		User user = new User();
		user.setId("OCR");
		UserContextThreadLocalHolder.setCurrentUser(user);

		RegMaster entityRegMaster = regMasterDao.getByShipNameAndOfficialNo(xml.getShipName(),  xml.getOfficialNumber());
		if (entityRegMaster!=null) {
			Mortgage latestMortgage = mortgageDao.getLatestMortgageByApplNo(entityRegMaster.getApplNo());

			Mortgage entity = new Mortgage();
			entity.setApplNo(entityRegMaster.getApplNo());
			entity.setRegMaster(entityRegMaster);
			if (latestMortgage==null) {
				entity.setPriorityCode("A");
			} else {
				int value = latestMortgage.getPriorityCode().charAt(0) + 1;
				entity.setPriorityCode(Character.toString((char)value));
			}

			List<String> mortgagors = new ArrayList<String>();
			List<Mortgagee> mortgagees = new ArrayList<Mortgagee>();

			List<Owner> ownerList = ownerDao.getByApplNo(entityRegMaster.getApplNo());
			if (ownerList!=null && ownerList.size()>0) {
				mortgagors.add(ownerList.get(0).getName());
			} else {
				mortgagors.add(" ");
			}
			//mortgagors.add("Test 1557112247643 on1");

			Mortgagee mortgagee = new Mortgagee();
			mortgagee.setName(xml.getNameOfMortgagee());
			mortgagee.setAddress1(xml.getAddressOfMortgagee());
			mortgagees.add(mortgagee);
			long docId = vd.uploadMortgage(xml.getPdfName(), xml.getShipName(), xml.getOfficialNumber(), xml.getNameOfMortgagee(), pdf);
			if (docId <= 0) {
				throw new IOException("upload failure");
			}

			mortgageService.add(entity, mortgagors, mortgagees);

		}
	};


}
