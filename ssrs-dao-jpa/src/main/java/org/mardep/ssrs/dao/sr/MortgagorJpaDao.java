package org.mardep.ssrs.dao.sr;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.PredicateCriteria;
import org.mardep.ssrs.dao.PredicateCriteria.PredicateType;
import org.mardep.ssrs.domain.sr.Mortgage;
import org.mardep.ssrs.domain.sr.Mortgagor;
import org.mardep.ssrs.domain.sr.MortgagorPK;
import org.springframework.stereotype.Repository;

@Repository
public class MortgagorJpaDao extends AbstractJpaDao<Mortgagor, MortgagorPK> implements IMortgagorDao {
	private List<PredicateCriteria> criteriaList = new ArrayList<PredicateCriteria>();

	public MortgagorJpaDao() {
		criteriaList.add(new PredicateCriteria("applNo", PredicateType.EQUAL));
		criteriaList.add(new PredicateCriteria("priorityCode", PredicateType.EQUAL));
	}

	@Override
	protected List<PredicateCriteria> resolvePredicateCriteriaList(CriteriaBuilder cb, Root<Mortgagor> listRoot) {
		return criteriaList;
	}

	@Override
	public List<String> findMortgagorNames(String applNo, String code) {
		Query query = em.createQuery("select owner.name "
				+ "from Owner owner, Mortgagor m "
				+ "where owner.applNo = m.applNo "
				+ "and m.seq = owner.ownerSeqNo "
				+ "and m.applNo = :applNo "
				+ "and m.priorityCode = :priorityCode");
		query.setParameter("applNo", applNo);
		query.setParameter("priorityCode", code);
		return query.getResultList();
	}

	@Override
	public int deleteByName(String applNo, String priorityCode, String name) {
		String qlString = "delete from Mortgagor m "
				+ "where m.applNo = :applNo "
				+ "and m.priorityCode = :priorityCode "
				+ "and m.seq = (select owner.ownerSeqNo "
				+ "from Owner owner "
				+ "where owner.applNo = :applNo "
				+ "and owner.name = :name)";
		Query query = em.createQuery(qlString);
		query.setParameter("applNo", applNo);
		query.setParameter("priorityCode", priorityCode);
		query.setParameter("name", name);
		return query.executeUpdate();
	}

	@Override
	public List<Mortgagor> findByCriteria(Mortgage mortgage) {
		Mortgagor mgorCriteria = new Mortgagor();
		mgorCriteria.setApplNo(mortgage.getApplNo());
		mgorCriteria.setPriorityCode(mortgage.getPriorityCode());
		return findByCriteria(mgorCriteria);
	}

	@Override
	public List<Mortgagor> findByTime(Mortgage mortgage, Date reportDate) {
		List<Mortgagor> result = new ArrayList<>();
		List<Mortgagor> list = findHistory(mortgage.getApplNo(), reportDate);
		for (Mortgagor m : list) {
			if (mortgage.getPriorityCode().equals(m.getPriorityCode())) {
				result.add(m);
			}
		}
		return result;
	}

	@Override
	public List<Mortgagor> findByMortgage(Mortgage mortgage) {
		// TODO Auto-generated method stub
		List<Mortgagor> mortgagorList = findByCriteria(mortgage);
		return mortgagorList;			
	}

}
