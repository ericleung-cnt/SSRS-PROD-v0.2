package org.mardep.ssrs.dao.sr;

import java.util.List;

import javax.persistence.Query;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.domain.sr.CsrFormOwner;
import org.mardep.ssrs.domain.sr.CsrFormPK;
import org.springframework.stereotype.Repository;

@Repository
public class CsrFormOwnerJpaDao extends AbstractJpaDao<CsrFormOwner, Long> implements ICsrFormOwnerDao {

	@Override
	public List<CsrFormOwner> get(String imoNo, Integer formSeq) {
		// TODO Auto-generated method stub
		try {
			Query query = em.createQuery("select o from CsrFormOwner o where o.imoNo=:imoNo and o.formSeq=:formSeq")
					.setParameter("imoNo", imoNo)
					.setParameter("formSeq", formSeq);
			List<CsrFormOwner> lst = (List<CsrFormOwner>) query.getResultList();
			return lst;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	public int delete(CsrFormPK id) {
		Query query = em.createQuery("delete CsrFormOwner where imoNo = :imoNo and formSeq = :formSeq");
		query.setParameter("imoNo", id.getImoNo());
		query.setParameter("formSeq", id.getFormSeq());
		return query.executeUpdate();
	}
}
