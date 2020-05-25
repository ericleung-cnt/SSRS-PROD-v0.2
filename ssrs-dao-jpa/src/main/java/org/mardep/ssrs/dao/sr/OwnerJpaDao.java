package org.mardep.ssrs.dao.sr;

import java.util.List;

import javax.persistence.Query;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.domain.sr.Owner;
import org.mardep.ssrs.domain.sr.OwnerPK;
import org.springframework.stereotype.Repository;

@Repository
public class OwnerJpaDao extends AbstractJpaDao<Owner, OwnerPK> implements IOwnerDao {

	@Override
	public List<Owner> findByImo(String imoNo) {
		Query query = em.createQuery("select owner from Owner owner, RegMaster rm where owner.applNo = rm.applNo and rm.imoNo = :imoNo order by rm.regDate desc");
		query.setParameter("imoNo", imoNo);
		List resultList = query.getResultList();
		String applNo = null;
		for (int i = 0; i < resultList.size(); i++) {
			if (applNo == null) {
				applNo = ((Owner) resultList.get(i)).getApplNo();
			} else {
				if (!applNo.equals(((Owner) resultList.get(i)).getApplNo())) {
					resultList = resultList.subList(0, i);
					break;
				}
			}
		}
		return resultList;
	}

	@Override
	public List<Owner> findByApplId(String applNo) {
		Query query = em.createQuery("select owner from Owner owner where owner.applNo = :applNo");
		query.setParameter("applNo", applNo);
		return query.getResultList();
	}

	@Override
	public int deleteByApplNoAndSeq(String applNo, int seq) {
		// TODO Auto-generated method stub
		String sql = "delete from Owner o where o.applNo=:applNo and o.ownerSeqNo=:seq";
		Query query = em.createQuery(sql)
				.setParameter("applNo", applNo)
				.setParameter("seq", seq);
		return query.executeUpdate();
	}

}
