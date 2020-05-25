package org.mardep.ssrs.dao.ocr;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.domain.ocr.OcrEntityCompanySearch;
import org.mardep.ssrs.domain.ocr.OcrEntityTranscript;
import org.springframework.stereotype.Repository;

@Repository
public class OcrCompanySearchJpaDao extends AbstractJpaDao<OcrEntityCompanySearch, Integer>implements IOcrCompanySearchDao {

	@PersistenceContext
	protected EntityManager em;

	@Override
	public List<OcrEntityCompanySearch> getAll() {
		// TODO Auto-generated method stub
		try {
			Query query = em.createQuery("select c from OcrEntityCompanySearch c order by c.companyName", 
					OcrEntityCompanySearch.class);
			List<OcrEntityCompanySearch> lst = (List<OcrEntityCompanySearch>) query.getResultList();
			return lst;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	public OcrEntityCompanySearch getByCrNumber(String crNumber) {
		// TODO Auto-generated method stub
		try {
			Query query = em.createQuery("select c from OcrEntityCompanySearch c where c.crNumber=:crNumber)", 
					OcrEntityCompanySearch.class)
					.setParameter("crNumber", crNumber);
			List<OcrEntityCompanySearch> lst = (List<OcrEntityCompanySearch>) query.getResultList();
			if (lst.size()>0) {
				if (lst.get(0)!=null) {
					return lst.get(0);
				} else {
					return null;
				}
			} else {
				return null;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;			
		}
	}
}
