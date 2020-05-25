package org.mardep.ssrs.dao.ocr;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.mardep.ssrs.domain.sr.Owner;
import org.springframework.stereotype.Repository;

@Repository
public class OcrOwnerJpaDao implements IOcrOwnerDao {

	@PersistenceContext
	protected EntityManager em;
	
	@Override
	public List<Owner> getByApplNo(String applNo) {
		// TODO Auto-generated method stub
		try {
			Query query = em.createQuery("select o from Owner o where o.applNo = :applNo order by o.ownerSeqNo")
					.setParameter("applNo", applNo);
			List<Owner> ownerList = (List<Owner>)query.getResultList();
			return ownerList;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	public void save(Owner owner) {
		// TODO Auto-generated method stub
		try {
			em.merge(owner);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
