package org.mardep.ssrs.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.mardep.ssrs.dao.Criteria;
import org.mardep.ssrs.dao.PagingCriteria;
import org.mardep.ssrs.dao.SortByCriteria;
import org.mardep.ssrs.dao.sr.IEtoCorDao;
import org.mardep.ssrs.domain.AbstractPersistentEntity;
import org.mardep.ssrs.domain.sr.EtoCoR;
import org.mardep.ssrs.domain.sr.EtoCorActiveState;
import org.mardep.ssrs.domain.sr.EtoCorValidState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class EtoCorService extends AbstractService implements IEtoCorService {

	@Autowired
	IEtoCorDao etoCorDao;

	@Autowired
	IShipRegService shipRegSvc;
	
	@Override
	public List<EtoCoR> findEtoCorList(String applNo){
		List<EtoCoR> resultList = etoCorDao.findByApplNo(applNo);
		return resultList;
	}
		
	@Override
	public List<EtoCoR> findEtoCorList(String applNo, String suffix){
		List<EtoCoR> resultList = etoCorDao.findByApplNo(applNo, suffix);
		return resultList;
	}
	
	//@Override
	private void insertMultiEtoCoR(List<EtoCoR> etoCoRs, String suffix) {
		//etoCorDao.insertMultiEtoCoR(etoCoRs);
		for (EtoCoR entity : etoCoRs) {
			entity.setApplNoSuf(suffix);
			entity.setActive("Y");
			entity.setCorValid("N");
			etoCorDao.updateEtoCoR(entity);
		}
		
//		for (EtoCoR entity : etoCoRs) {
//			EtoCoR fEntity = new EtoCoR();
//			fEntity.setApplNo(entity.getApplNo());
//			fEntity.setApplNoSuf("F");
//			fEntity.setActive("Y");
//			fEntity.setCorValid("N");
//			fEntity.setCertIssueDate(entity.getCertIssueDate());
//			fEntity.setRegDate(entity.getRegDate());
//			fEntity.setTrackCode(entity.getTrackCode());
//			
//			//etoCorDao.updateEtoCoR(fEntity);
//		}
	}
	
	@Override
	public void replaceMultiEtoCoR_ProReg(List<EtoCoR> etoCoRs, String applNo) {
		List<EtoCoR> storedList = findEtoCorList(applNo, "P");
		for (EtoCoR entity : storedList) {
			entity.setActive("N");
			etoCorDao.updateEtoCoR(entity);
		}
		insertMultiEtoCoR(etoCoRs, "P");
	}
	
	@Override
	public void replaceMultiEtoCoR_FullReg(List<EtoCoR> etoCoRs, String applNo) {
		List<EtoCoR> storedList = findEtoCorList(applNo, "F");
		for (EtoCoR entity : storedList) {
			entity.setActive("N");
			etoCorDao.updateEtoCoR(entity);
		}
		insertMultiEtoCoR(etoCoRs, "F");
	}
	
	@Override
	public void updateValidEtoCoR(EtoCoR etoCoR) {
		List<EtoCoR> entities = etoCorDao.findByApplNo(etoCoR.getApplNo());
		for (EtoCoR entity : entities) {
			if (entity.getId().equals(etoCoR.getId())) {
				entity.setCorValid("Y");
			} else {
				entity.setCorValid("N");
			}
			etoCorDao.updateEtoCoR(entity);
		}
	}
	
}
