package org.mardep.ssrs.certService;

import javax.transaction.Transactional;

import org.mardep.ssrs.dao.cert.ICertIssueLogDao;
import org.mardep.ssrs.domain.entity.cert.EntityCertIssueLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class CertIssueLogService implements ICertIssueLogService {

	@Autowired
	ICertIssueLogDao dao;
	
	@Override
	public EntityCertIssueLog save(EntityCertIssueLog entity) {
		// TODO Auto-generated method stub
		dao.save(entity);
		return entity;
	}

}
