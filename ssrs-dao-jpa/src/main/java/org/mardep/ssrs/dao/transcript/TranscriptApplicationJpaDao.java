package org.mardep.ssrs.dao.transcript;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.Criteria;
import org.mardep.ssrs.dao.PagingCriteria;
import org.mardep.ssrs.dao.SortByCriteria;
import org.mardep.ssrs.domain.entity.transcript.EntityTranscriptApplication;
import org.mardep.ssrs.domain.sr.Transaction;
import org.springframework.stereotype.Repository;

@Repository
public class TranscriptApplicationJpaDao extends AbstractJpaDao<EntityTranscriptApplication, Integer> implements ITranscriptApplicationDao {

	@Override
	public List<EntityTranscriptApplication> getAll() {
		// TODO Auto-generated method stub
		try {
			String sql = "select t from EntityTranscriptApplication t";
			Query query = em.createQuery(sql, EntityTranscriptApplication.class);
			List<EntityTranscriptApplication> resultList = query.getResultList();
			return resultList;
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	public EntityTranscriptApplication get(Integer transcriptApplicationId) {
		// TODO Auto-generated method stub
		try {
			String sql = "select t from EntityTranscriptApplication t where t.id=:id";
			Query query = em.createQuery(sql, EntityTranscriptApplication.class)
					.setParameter("id", transcriptApplicationId);
			List<EntityTranscriptApplication> resultList = query.getResultList();
			if (resultList.size()>0) {
				return resultList.get(0);
			} else {
				return null;
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			ex.printStackTrace();
			return null;
		}
	}

}
