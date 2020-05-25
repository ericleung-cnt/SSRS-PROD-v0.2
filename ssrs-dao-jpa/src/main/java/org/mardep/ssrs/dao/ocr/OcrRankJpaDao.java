package org.mardep.ssrs.dao.ocr;



import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.mardep.ssrs.domain.codetable.Rank;
import org.springframework.stereotype.Repository;

@Repository
public class OcrRankJpaDao implements IOcrRankDao {

	@PersistenceContext
	protected EntityManager em;
	
	@Override
	public Rank getByName(String name) {
		// TODO Auto-generated method stub
		try {
			Query query = em.createQuery("select r from Rank r where r.engDesc = :name")
					.setParameter("name", name);
			List<Rank> rankList = (List<Rank>)query.getResultList();
			if (rankList.size()>0) {
				return rankList.get(0);
			} else {
				return null;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

}
