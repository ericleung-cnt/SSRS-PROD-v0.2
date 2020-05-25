package org.mardep.ssrs.dao.cert;

import java.util.List;

import org.mardep.ssrs.dao.IBaseDao;
import org.mardep.ssrs.domain.entity.cert.EntityCertDemandNoteItem;

public interface ICertDemandNoteItemDao extends IBaseDao<EntityCertDemandNoteItem, Long>  {
	public List<EntityCertDemandNoteItem> get(Integer certApplicationId);
	public List<EntityCertDemandNoteItem> get(String certType, Integer certApplicationId);
	public List<EntityCertDemandNoteItem> get(String certType, String certApplicationNo);
	
	public EntityCertDemandNoteItem save(EntityCertDemandNoteItem entity);
}
