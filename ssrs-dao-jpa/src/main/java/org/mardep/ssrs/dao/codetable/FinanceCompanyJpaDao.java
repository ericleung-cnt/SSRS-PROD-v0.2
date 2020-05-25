package org.mardep.ssrs.dao.codetable;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.PredicateCriteria;
import org.mardep.ssrs.dao.PredicateCriteria.PredicateType;
import org.mardep.ssrs.domain.codetable.FinanceCompany;
import org.springframework.stereotype.Repository;

@Repository
public class FinanceCompanyJpaDao extends AbstractJpaDao<FinanceCompany, Long> implements IFinanceCompanyDao {
	public FinanceCompanyJpaDao() {
		for (int i = 0; i < criteriaList.size(); i++) {
			PredicateCriteria c = criteriaList.get(i);
			switch (c.getKey()) {
			case "id":
			case "clinicNo":
				PredicateCriteria element = new PredicateCriteria(c.getKey(), PredicateType.EQUAL);
				criteriaList.set(i, element);
				break;
			}
		}
	}
}
