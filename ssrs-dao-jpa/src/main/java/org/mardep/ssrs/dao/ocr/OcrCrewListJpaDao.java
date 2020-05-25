package org.mardep.ssrs.dao.ocr;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.mardep.ssrs.domain.codetable.Crew;
import org.mardep.ssrs.domain.codetable.CrewListCover;
import org.mardep.ssrs.domain.user.User;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.springframework.stereotype.Repository;

@Repository
public class OcrCrewListJpaDao implements IOcrCrewListDao {

	@PersistenceContext
	protected EntityManager em;
	
//	@Autowired
//	protected IOcrRegMasterDao regMasterDao;
	
	@Override
	public void saveUOW(CrewListCover entityCrewListCover, List<Crew> entityListCrew) {
		// TODO Auto-generated method stub
		try {
//			User user = new User();
//			user.setId("OCR");
//			UserContextThreadLocalHolder.setCurrentUser(user);
			//em.merge(entityCrewListCover);
			int recExist = existCrewListCover(entityCrewListCover.getVesselId(), entityCrewListCover.getCoverYymm());
			if (recExist<1) {
				em.merge(entityCrewListCover);
			}
			for (Crew crew : entityListCrew) {
				em.merge(crew);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	public int existCrewListCover(String vesselID, String coverYYMM) {
		try {
			Query query = em.createQuery("select clc from CrewListCover clc where clc.vesselId = :officialNumber and clc.coverYymm = :coverYYMM")
					.setParameter("officialNumber", vesselID)
					.setParameter("coverYYMM", coverYYMM);
			int result = query.getResultList().size();
			return result;
		} catch (Exception ex) {
			ex.printStackTrace();
			return -1;
		}
	}

	@Override
	public CrewListCover getCrewListCoverByVesselId(String vesselID, String coverYYMM) {
		// TODO Auto-generated method stub
		try {
			Query query = em.createQuery("select clc from CrewListCover clc where clc.vesselId = :vesselID and clc.coverYymm = :coverYYMM")
					.setParameter("vesselID", vesselID)
					.setParameter("coverYYMM", coverYYMM);
			List<CrewListCover> entity = (List<CrewListCover>) query.getResultList();
			if (entity.size()>0) {
				return entity.get(0);
			} else {
				return null;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
		//return null;
	}

	@Override
	public Crew getCrew(String officialNumber, String coverYYMM, String serb) {
		// TODO Auto-generated method stub
		try {
			Query query = em.createQuery("select c from CREW c where c.VESSEL_ID = :officialNumber and COVER_YYMM = :coverYYMM and SERB_NO = :serb")
					.setParameter("officialNumber", officialNumber)
					.setParameter("coverYYMM", coverYYMM)
					.setParameter("serb", serb);
			Crew entity = (Crew) query.getSingleResult();
			return entity;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	public Crew saveCrew(Crew entity) {
		// TODO Auto-generated method stub
		try {
			em.merge(entity);
			return entity;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}		
	}

	@Override
	public CrewListCover saveCrewListCover(CrewListCover entity) {
		// TODO Auto-generated method stub
		try {
			User user = new User();
			user.setId("OCR");
			UserContextThreadLocalHolder.setCurrentUser(user);
			em.merge(entity);
			return entity;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}		
	}

	@Override
	public void saveUOW(List<Crew> entityListCrew) {
		// TODO Auto-generated method stub
		try {
			for (Crew crew : entityListCrew) {
				em.merge(crew);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
