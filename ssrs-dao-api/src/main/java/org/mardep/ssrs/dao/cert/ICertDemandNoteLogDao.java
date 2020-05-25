package org.mardep.ssrs.dao.cert;

import java.util.List;

import org.mardep.ssrs.dao.IBaseDao;
import org.mardep.ssrs.domain.entity.cert.EntityCertDemandNoteLog;

public interface ICertDemandNoteLogDao extends IBaseDao<EntityCertDemandNoteLog, Integer>  {
	public List<EntityCertDemandNoteLog> get(Integer certApplicationId);
	public List<EntityCertDemandNoteLog> get(String certType, Integer certApplicationId);
	public List<EntityCertDemandNoteLog> get(String certType, String certApplicationNo);
	
	public EntityCertDemandNoteLog save(EntityCertDemandNoteLog entity);
}
