package org.mardep.ssrs.dms.ocr.dbService;

import java.io.IOException;
import java.util.List;

import javax.transaction.Transactional;

import org.mardep.ssrs.dao.ocr.IOcrRegMasterDao;
import org.mardep.ssrs.dao.sr.IBuilderMakerDao;
import org.mardep.ssrs.dms.ocr.service.IOcrBaseService;
import org.mardep.ssrs.dms.ocr.xml.OcrXmlCos;
import org.mardep.ssrs.domain.sr.BuilderMaker;
import org.mardep.ssrs.domain.sr.RegMaster;
import org.mardep.ssrs.domain.user.User;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.mardep.ssrs.service.IBuilderMakerService;
import org.mardep.ssrs.vitaldoc.IVitalDocClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class OcrDbServiceCos implements IOcrDbServiceCos {

	@Autowired
	IOcrBaseService baseService;
	
	@Autowired
	protected IOcrRegMasterDao regMasterDao;

//	@Autowired
//	protected IBuilderMakerDao bmDao;

	@Autowired
	IBuilderMakerService bmSvc;
	
	@Autowired
	IVitalDocClient vd;

	@Override
	public void save(OcrXmlCos xml, byte[] pdf) throws IOException {
		// TODO Auto-generated method stub
		User user = new User();
		user.setId("OCR");
		UserContextThreadLocalHolder.setCurrentUser(user);

		RegMaster entityRegMaster = regMasterDao.getByShipNameAndIMO(xml.getShipName(), xml.getImoNumber());
		if (entityRegMaster!=null) {
			entityRegMaster.setHowProp(xml.getHowPropelled());
			entityRegMaster.setMaterial(xml.getMaterialOfHull());
			entityRegMaster.setGrossTon(xml.getShipGrossTonnage());
			entityRegMaster.setRegNetTon(xml.getShipNetTonnage());
			entityRegMaster.setBuildDate(xml.getKeelLaidDate());
			entityRegMaster.setLength(xml.getShipLength());
			entityRegMaster.setBreadth(xml.getShipBreadth());
			entityRegMaster.setDepth(xml.getShipDepth());
			entityRegMaster.setEngSetNum(xml.getEngSetNum());
			entityRegMaster.setNoOfShafts(xml.getNoOfShafts());
			entityRegMaster.setEngPower(xml.getTotalEnginePower());
			//entityRegMaster.setEngMaker(xml.getEngineMake());
//			1. Engine Desc 1
			entityRegMaster.setEngDesc1(xml.getMainEngineType());
//			2. Estimate Speed
			entityRegMaster.setEstSpeed(xml.getSpeed());
//			3. Type of Ship
			entityRegMaster.setSurveyShipType(xml.getTypeOfShip());
//			4. Engine Model 1
			entityRegMaster.setEngModel1(xml.getEngineMake());
//			5. Engine Model 2
			entityRegMaster.setEngModel2(xml.getModel());
			
			if (baseService.getDmsEnabled()) {
				long docId = vd.uploadCos(xml.getPdfName(), entityRegMaster.getRegName(), entityRegMaster.getImoNo(), pdf);
				if (docId <= 0) {
					throw new IOException("upload failure");
				}
			}
			
			regMasterDao.save(entityRegMaster);
			
			if (xml.getBuilderNameAddress()!=null) {
				String applNo = entityRegMaster.getApplNo();
				List<BuilderMaker> bms = bmSvc.findByApplId(applNo);
				if (bms.size()==0) {
					BuilderMaker bm = new BuilderMaker();
					bm.setApplNo(applNo);
					bm.setBuilderCode("S");
					bm.setName(xml.getBuilderNameAddress());
					bmSvc.insert(bm);
				}
			}
		}
	}
}
