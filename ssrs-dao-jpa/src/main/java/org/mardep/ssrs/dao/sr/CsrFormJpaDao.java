package org.mardep.ssrs.dao.sr;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.PredicateCriteria;
import org.mardep.ssrs.dao.PredicateCriteria.PredicateType;
import org.mardep.ssrs.domain.sr.CsrForm;
import org.mardep.ssrs.domain.sr.CsrFormPK;
import org.springframework.stereotype.Repository;

@Repository
public class CsrFormJpaDao extends AbstractJpaDao<CsrForm, CsrFormPK> implements ICsrFormDao {
	public CsrFormJpaDao() {
		criteriaList.add(new PredicateCriteria("imoNo", PredicateType.EQUAL));
		criteriaList.add(new PredicateCriteria("formSeq", PredicateType.EQUAL));
	}

}
