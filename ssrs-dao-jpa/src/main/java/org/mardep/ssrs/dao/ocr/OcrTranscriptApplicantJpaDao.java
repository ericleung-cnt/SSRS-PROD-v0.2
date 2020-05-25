package org.mardep.ssrs.dao.ocr;

import java.util.List;

import javax.persistence.Query;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.domain.ocr.OcrEntityTranscriptApplicant;
import org.springframework.stereotype.Repository;

@Repository
public class OcrTranscriptApplicantJpaDao extends AbstractJpaDao<OcrEntityTranscriptApplicant, Integer> implements IOcrTranscriptApplicantDao {

	@Override
	public void saveIfNotExist(OcrEntityTranscriptApplicant entity) {
		// TODO Auto-generated method stub
		try {
			//String sql = "select t from OcrEntityTranscriptApplicant t";// where t.applicantCompanyName=:companyName";
			Query query= em.createQuery("select t from OcrEntityTranscriptApplicant t where t.applicantCompanyName=:companyName", OcrEntityTranscriptApplicant.class)
					.setParameter("companyName", entity.getApplicantCompanyName());
			List<OcrEntityTranscriptApplicant> lst = query.getResultList();
			if (lst.size()<=0) {
				em.merge(entity);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
