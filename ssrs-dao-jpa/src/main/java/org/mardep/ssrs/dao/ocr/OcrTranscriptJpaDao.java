package org.mardep.ssrs.dao.ocr;

import java.util.List;

import javax.persistence.Query;
import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.domain.ocr.OcrEntityTranscript;
import org.springframework.stereotype.Repository;

@Repository
public class OcrTranscriptJpaDao extends AbstractJpaDao<OcrEntityTranscript, Integer>implements IOcrTranscriptDao {

	@Override
	public List<OcrEntityTranscript> getAll() {
		try {
			Query query = em.createQuery("select t from OcrEntityTranscript t where t.processed is null", OcrEntityTranscript.class);
			List<OcrEntityTranscript> lst = query.getResultList();
			return lst;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	public void remove(int id) {
		// TODO Auto-generated method stub
		try {
//			Query query = em.createQuery("select t from OcrEntityTranscript t where t.ocrTranscriptId=:id")
//					.setParameter("id", id);
//			List<OcrEntityTranscript> lst = (List<OcrEntityTranscript>) query.getResultList();
//			if (lst!=null && lst.size()>0) {
//				OcrEntityTranscript entity = lst.get(0);
//				em.remove(entity);
//			}
			OcrEntityTranscript entity = em.find(OcrEntityTranscript.class, id);
			//em.getTransaction().begin();
			//entity.setProcessed("Y");
			//super.save(entity);
			em.remove(entity);
			//super.delete(entity);
			//em.getTransaction().commit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	public OcrEntityTranscript save(OcrEntityTranscript entity) {
		try {
			em.merge(entity);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
		return null;
	}

}
