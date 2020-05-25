package org.mardep.ssrs.certService;

import java.util.List;

import javax.transaction.Transactional;

import org.mardep.ssrs.dao.cert.ICertDemandNoteItemDao;
import org.mardep.ssrs.domain.entity.cert.EntityCertDemandNoteItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class CertDemandNoteItemService implements ICertDemandNoteItemService {

	@Autowired
	ICertDemandNoteItemDao dao;
	
	@Override
	public List<EntityCertDemandNoteItem> get(int certApplicationId) {
		List<EntityCertDemandNoteItem> resultList = dao.get(certApplicationId);
		return resultList;
	}
	
	@Override
	public List<EntityCertDemandNoteItem> getByApplicationIdCertType(String certType, int certApplicationId) {
		// TODO Auto-generated method stub
		List<EntityCertDemandNoteItem> resultList = dao.get(certType, certApplicationId);
		
		return resultList;
	}

	@Override
	public EntityCertDemandNoteItem save(EntityCertDemandNoteItem entity) {
		dao.save(entity);
		return entity;
	}
	
	@Override
	public int delete(EntityCertDemandNoteItem entity) {
		int result = dao.delete(entity);
		return result;
	}
}
