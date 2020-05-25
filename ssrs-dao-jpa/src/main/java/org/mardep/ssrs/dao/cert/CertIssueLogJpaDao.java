package org.mardep.ssrs.dao.cert;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.Criteria;
import org.mardep.ssrs.dao.PagingCriteria;
import org.mardep.ssrs.dao.SortByCriteria;
import org.mardep.ssrs.domain.entity.cert.EntityCertIssueLog;
import org.mardep.ssrs.domain.sr.Transaction;
import org.springframework.stereotype.Repository;

@Repository
public class CertIssueLogJpaDao extends AbstractJpaDao<EntityCertIssueLog, Integer> implements ICertIssueLogDao {

	@Override
	public List<EntityCertIssueLog> get(Integer certApplicationId){
		try {
			String sql = "select l from EntityCertIssueLog l where l.certApplicationId=:certApplicationId";
			Query query = em.createQuery(sql, EntityCertIssueLog.class)
					//.setParameter("certType", certType)
					.setParameter("certApplicationId", certApplicationId);
			List<EntityCertIssueLog> resultList = query.getResultList();
			return resultList;
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			ex.printStackTrace();
			return null;
		}
	}

//	@Override
//	public List<EntityCertIssueLog> get(String certType, Integer certApplicationId) {
//		// TODO Auto-generated method stub
//		try {
//			String sql = "select l from EntityCertIssueLog l where l.certType=:certType and l.certApplicationId=:certApplicationId";
//			Query query = em.createQuery(sql, EntityCertIssueLog.class)
//					.setParameter("certType", certType)
//					.setParameter("certApplicationId", certApplicationId);
//			List<EntityCertIssueLog> resultList = query.getResultList();
//			return resultList;
//		} catch (Exception ex) {
//			logger.error(ex.getMessage());
//			ex.printStackTrace();
//			return null;
//		}
//	}

	@Override
	public List<EntityCertIssueLog> get(String certType, String certApplicationNo) {
		// TODO Auto-generated method stub
		try {
			String sql = "select l from EntityCertIssueLog l where l.certType=:certType and l.certApplicationNo=:certApplicationNo";
			Query query = em.createQuery(sql, EntityCertIssueLog.class)
					.setParameter("certType", certType)
					.setParameter("certApplicationNo", certApplicationNo);
			List<EntityCertIssueLog> resultList = query.getResultList();
			return resultList;
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	public List<EntityCertIssueLog> getAllForReport(Date fromDate, Date toDate) {
		// TODO Auto-generated method stub
		try {
			//String sql = "select l from EntityCertIssueLog l where l.certType=:certType and l.certApplicationNo=:certApplicationNo";
//			Query query = em.createQuery("select e from EntityCertIssueLog e where :fromDate>e.issueDate and :toDate<e.issueDate", EntityCertIssueLog.class)
//					.setParameter("fromDate", fromDate)
//					.setParameter("toDate", toDate);
//			List<EntityCertIssueLog> resultList = query.getResultList();
			
			Query query = em.createQuery("select e from EntityCertIssueLog e where e.issueDate>=:fromDate and e.issueDate<=:toDate", EntityCertIssueLog.class)
					.setParameter("fromDate", fromDate)
					.setParameter("toDate", toDate);
			List<EntityCertIssueLog> resultList = query.getResultList();
			return resultList;
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			ex.printStackTrace();
			return null;
		}
	}
	
}
