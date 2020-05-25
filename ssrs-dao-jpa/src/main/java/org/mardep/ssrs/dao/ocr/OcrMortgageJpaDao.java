package org.mardep.ssrs.dao.ocr;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.mardep.ssrs.domain.sr.Mortgage;
import org.springframework.stereotype.Repository;

@Repository
public class OcrMortgageJpaDao implements IOcrMortgageDao {

	@PersistenceContext
	protected EntityManager em;
	
	@Override
	public Mortgage getLatestMortgageByApplNo(String applNo) {
		// TODO Auto-generated method stub
		try {
			Query query = em.createQuery("select m from Mortgage m where m.applNo = :applNo order by m.priorityCode desc")
					.setParameter("applNo", applNo);
			List<Mortgage> mortgageList = (List<Mortgage>) query.getResultList();
			if (mortgageList!=null && mortgageList.size()>0) {
				return mortgageList.get(0);
			} else {
				return null;
			}
		} catch (Exception ex) {
			return null;
		}
	}

}
