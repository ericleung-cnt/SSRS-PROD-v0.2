package org.mardep.ssrs.dao.ocr;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.mardep.ssrs.domain.sr.RegMaster;
import org.springframework.stereotype.Repository;

@Repository
public class OcrRegMasterJpaDao implements IOcrRegMasterDao {

	@PersistenceContext
	protected EntityManager em;
	
	@Override
	public RegMaster getByShipNameAndIMO(String shipName, String imo) {
		// TODO Auto-generated method stub
		try {
			Query query = em.createQuery("select r from RegMaster r where r.regName = :shipName and r.imoNo = :imo order by r.regDate desc")
					.setParameter("shipName", shipName)
					.setParameter("imo", imo);
			//RegMaster rm = (RegMaster) query.getSingleResult();
			List<RegMaster> rmList = (List<RegMaster>) query.getResultList();
			if (rmList.size()>0) {
				return rmList.get(0);
			} else {
				return null;
			}
			
//			OcrEntityRegMaster entity = new OcrEntityRegMaster();
//			entity.setApplNo(rm.getApplNo());
//			entity.setImoNo(rm.getImoNo());
			//return rm;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	public RegMaster getByOfficialNo(String officialNo) {
		// TODO Auto-generated method stub
		try {
			Query query = em.createQuery("select r from RegMaster r where r.offNo = :officialNo order by r.regDate desc")
					.setParameter("officialNo", officialNo);
			//RegMaster rm = (RegMaster) query.getSingleResult();
			List<RegMaster> rmList = (List<RegMaster>) query.getResultList();
			if (rmList.size()>0) {
				return rmList.get(0);
			} else {
				return null;
			}	
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	public RegMaster save(RegMaster entity) {
		// TODO Auto-generated method stub
		try {
			em.merge(entity);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
		return null;
	}

	@Override
	public RegMaster getByShipNameAndOfficialNo(String shipName, String officialNo) {
		// TODO Auto-generated method stub
		try {
			Query query = em.createQuery("select r from RegMaster r where r.regName = :shipName and r.offNo = :officialNo order by r.regDate desc")
					.setParameter("shipName", shipName)
					.setParameter("officialNo", officialNo);
			//RegMaster rm = (RegMaster) query.getSingleResult();
			List<RegMaster> rmList = (List<RegMaster>) query.getResultList();
			if (rmList.size()>0) {
				return rmList.get(0);
			} else {
				return null;
			}
			
//			OcrEntityRegMaster entity = new OcrEntityRegMaster();
//			entity.setApplNo(rm.getApplNo());
//			entity.setImoNo(rm.getImoNo());
			//return rm;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
}
