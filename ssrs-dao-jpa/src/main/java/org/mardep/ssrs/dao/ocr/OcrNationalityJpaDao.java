package org.mardep.ssrs.dao.ocr;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.mardep.ssrs.domain.codetable.Nationality;
import org.springframework.stereotype.Repository;

@Repository
public class OcrNationalityJpaDao implements IOcrNationalityDao {

	@PersistenceContext
	protected EntityManager em;
	
	@Override
	public Nationality getByName(String name) {
		// TODO Auto-generated method stub
		try {
			Query query = em.createQuery("select n from Nationality n where n.engDesc = :name")
					.setParameter("name", name);
			List<Nationality> nationalityList = (List<Nationality>)query.getResultList();
			if (nationalityList.size()>0) {
				return nationalityList.get(0);
			} else {
				return null;
			}
		} catch (Exception ex) {
			return null;
		}
	}

}
