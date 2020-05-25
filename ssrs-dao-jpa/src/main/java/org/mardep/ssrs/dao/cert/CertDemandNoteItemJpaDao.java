package org.mardep.ssrs.dao.cert;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.Criteria;
import org.mardep.ssrs.dao.PagingCriteria;
import org.mardep.ssrs.dao.SortByCriteria;
import org.mardep.ssrs.domain.entity.cert.EntityCertDemandNoteItem;
import org.mardep.ssrs.domain.sr.Transaction;
import org.springframework.stereotype.Repository;

@Repository
public class CertDemandNoteItemJpaDao extends AbstractJpaDao<EntityCertDemandNoteItem, Long> implements ICertDemandNoteItemDao {

	@Override
	public List<EntityCertDemandNoteItem> get(Integer certApplicationId) {
		// TODO Auto-generated method stub
		try {
			String sql = "select dni from EntityCertDemandNoteItem dni where dni.certApplicationId=:certApplicationId)";
			Query query = em.createQuery(sql, EntityCertDemandNoteItem.class)
					//.setParameter("certType", certType)
					.setParameter("certApplicationId", certApplicationId);
			List<EntityCertDemandNoteItem> resultList = query.getResultList();
			return resultList;
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			ex.printStackTrace();
			return null;
		}
	}

	
	@Override
	public List<EntityCertDemandNoteItem> get(String certType, Integer certApplicationId) {
		// TODO Auto-generated method stub
		try {
			String sql = "select dni from EntityCertDemandNoteItem dni where dni.certApplicationId=:certApplicationId and dni.certType=:certType)";
			Query query = em.createQuery(sql, EntityCertDemandNoteItem.class)
					.setParameter("certType", certType)
					.setParameter("certApplicationId", certApplicationId);
			List<EntityCertDemandNoteItem> resultList = query.getResultList();
			return resultList;
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	public List<EntityCertDemandNoteItem> get(String certType, String certApplicationNo) {
		// TODO Auto-generated method stub
		try {
			String sql = "select dni from EntityCertDemandNoteItem dni where dni.certType=:certType and dni.certApplicationNo=:certApplicationNo";
			Query query = em.createQuery(sql, EntityCertDemandNoteItem.class)
					.setParameter("certType", certType)
					.setParameter("certApplicationNo", certApplicationNo);
			List<EntityCertDemandNoteItem> resultList = query.getResultList();
			return resultList;
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			ex.printStackTrace();
			return null;
		}
	}

}
