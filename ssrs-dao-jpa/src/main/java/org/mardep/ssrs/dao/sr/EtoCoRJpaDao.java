package org.mardep.ssrs.dao.sr;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.Criteria;
import org.mardep.ssrs.dao.PagingCriteria;
import org.mardep.ssrs.dao.SortByCriteria;
import org.mardep.ssrs.domain.codetable.ShipManager;
import org.mardep.ssrs.domain.sr.EtoCoR;
import org.mardep.ssrs.domain.sr.EtoCorActiveState;
import org.mardep.ssrs.domain.sr.Transaction;
import org.springframework.stereotype.Repository;

@Repository
public class EtoCoRJpaDao extends AbstractJpaDao<EtoCoR, Long> implements IEtoCorDao {

	@Override
	public List<EtoCoR> findByApplNo(String applNo){
		try {
			List<EtoCoR> entities;
			String sql = "select ec from EtoCoR ec where ec.applNo=:applNo and ec.active=:active";
			Query query = em.createQuery(sql)
					.setParameter("applNo", applNo)
					.setParameter("active", "Y");
					
			entities = query.getResultList();
			return entities;
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			return null;
		}
	}

	@Override
	public List<EtoCoR> findByApplNo(String applNo, String suffix){
		try {
			List<EtoCoR> entities;
			String sql = "select ec from EtoCoR ec where ec.applNo=:applNo and ec.active=:active and ec.applNoSuf=:suffix";
			Query query = em.createQuery(sql)
					.setParameter("applNo", applNo)
					.setParameter("active", "Y")
					.setParameter("suffix", suffix);
			entities = query.getResultList();
			return entities;
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			return null;
		}
	}

	@Override
	public void insertMultiEtoCoR(List<EtoCoR> entities) {
		try {
			for (EtoCoR entity : entities) {
				em.merge(entity);
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage());
		}
	}
	
	@Override
	public void updateEtoCoR(EtoCoR entity) {
		try {
			em.merge(entity);
		} catch (Exception ex) {
			logger.error(ex.getMessage());
		}
	}
}
