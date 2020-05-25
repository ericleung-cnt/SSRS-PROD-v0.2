package org.mardep.ssrs.dao.cert;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.Criteria;
import org.mardep.ssrs.dao.PagingCriteria;
import org.mardep.ssrs.dao.SortByCriteria;
import org.mardep.ssrs.domain.entity.cert.EntityCertDemandNoteLog;
import org.mardep.ssrs.domain.sr.Transaction;
import org.springframework.stereotype.Repository;

@Repository
public class CertDemandNoteLogJpaDao extends AbstractJpaDao<EntityCertDemandNoteLog, Integer> implements ICertDemandNoteLogDao {

	@Override
	public List<EntityCertDemandNoteLog> get(Integer certApplicationId) {
		// TODO Auto-generated method stub
		try {
			String sql = "select l from EntityCertDemandNoteLog l where l.certApplicationId=:certApplicationId";
			Query query = em.createQuery(sql, EntityCertDemandNoteLog.class)
					//.setParameter("certType", certType)
					.setParameter("certApplicationId", certApplicationId);
			List<EntityCertDemandNoteLog> resultList = query.getResultList();
			return resultList;
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	public List<EntityCertDemandNoteLog> get(String certType, Integer certApplicationId) {
		// TODO Auto-generated method stub
		try {
			String sql = "select l from EntityCertDemandNoteLog l where l.certType=:certType and l.certApplicationId=:certApplicationId";
			Query query = em.createQuery(sql, EntityCertDemandNoteLog.class)
					.setParameter("certType", certType)
					.setParameter("certApplicationId", certApplicationId);
			List<EntityCertDemandNoteLog> resultList = query.getResultList();
			return resultList;
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	public List<EntityCertDemandNoteLog> get(String certType, String certApplicationNo) {
		// TODO Auto-generated method stub
		try {
			String sql = "select l from EntityCertDemandNoteLog l where l.certType=:certType and l.certApplicationNo=:certApplicationNo";
			Query query = em.createQuery(sql, EntityCertDemandNoteLog.class)
					.setParameter("certType", certType)
					.setParameter("certApplicationNo", certApplicationNo);
			List<EntityCertDemandNoteLog> resultList = query.getResultList();
			return resultList;
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			ex.printStackTrace();
			return null;
		}
	}


}
