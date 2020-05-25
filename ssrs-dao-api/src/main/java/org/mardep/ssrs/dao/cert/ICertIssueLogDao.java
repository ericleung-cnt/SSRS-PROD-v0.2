package org.mardep.ssrs.dao.cert;

import java.util.Date;
import java.util.List;

import org.mardep.ssrs.dao.IBaseDao;
import org.mardep.ssrs.domain.entity.cert.EntityCertIssueLog;

public interface ICertIssueLogDao extends IBaseDao<EntityCertIssueLog, Integer> {
	//public List<EntityCertIssueLog> get(String certType, Integer certApplicationId);
	public List<EntityCertIssueLog> get(String certType, String certApplicationNo);
	
	public EntityCertIssueLog save(EntityCertIssueLog entity);
	List<EntityCertIssueLog> getAllForReport(Date fromDate, Date toDate);
	List<EntityCertIssueLog> get(Integer certApplicationId);

}
