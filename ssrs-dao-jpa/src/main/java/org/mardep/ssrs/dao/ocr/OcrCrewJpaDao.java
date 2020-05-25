package org.mardep.ssrs.dao.ocr;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.mardep.ssrs.domain.codetable.Crew;
import org.springframework.stereotype.Repository;

@Repository
public class OcrCrewJpaDao implements IOcrCrewDao {

	@PersistenceContext
	protected EntityManager em;
	
	@Override
	public int maxRefNo(String vesselId, String yyMM) {
		// TODO Auto-generated method stub
		try {
			Query query = em.createQuery("select max(c.referenceNo) from Crew c where c.vesselId = :vesselId and c.coverYymm = :yymm")
					.setParameter("vesselId", vesselId)
					.setParameter("yymm", yyMM);
			List<Integer> refNo = (List<Integer>)query.getResultList();
			if (refNo.size()>0) {
				System.out.println("refno size:" + refNo.size() + " value: " + refNo.get(0));
				if (refNo.get(0)!=null) {
					return (int)refNo.get(0);
				} else {
					return 0;
				}
				//return (int) refNo.get(0);
			} else {
				return 0;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	@Override
	public List<Crew> getCrewByVesselId(String vesselId, String yyMM, String serb) {
		// TODO Auto-generated method stub
		try {
			Query query = em.createQuery("select c from Crew c where c.vesselId = :vesselId and c.coverYymm = :yymm and c.serbNo = :serb order by c.engageDate desc")
					.setParameter("vesselId", vesselId)
					.setParameter("yymm", yyMM)
					.setParameter("serb", serb);
			List<Crew> crewList = (List<Crew>)query.getResultList();
			//em.detach(crewList);
//			if (crewList.size()>0) {
//				Crew crew = crewList.get(0);
//				return crew;
//			} else {
//				return null;
//			}
			return crewList;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	public Crew getCrewByVesselId(String vesselId, String yyMM, String serb, Date engagementDate) {
		// TODO Auto-generated method stub
		try {
			Query query = em.createQuery("select c from Crew c where c.vesselId = :vesselId and c.coverYymm = :yymm and c.serbNo = :serb and c.engageDate = :engagementDate")
					.setParameter("vesselId", vesselId)
					.setParameter("yymm", yyMM)
					.setParameter("serb", serb)
					.setParameter("engagementDate",  engagementDate);
			List<Crew> crewList = (List<Crew>)query.getResultList();
			//em.detach(crewList);
			if (crewList.size()>0) {
				Crew crew = crewList.get(0);
				return crew;
			} else {
				return null;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}	
	
}
