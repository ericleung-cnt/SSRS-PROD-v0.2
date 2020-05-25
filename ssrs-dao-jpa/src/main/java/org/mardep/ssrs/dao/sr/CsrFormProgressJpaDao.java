package org.mardep.ssrs.dao.sr;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.domain.sr.CsrFormPK;
import org.mardep.ssrs.domain.sr.CsrFormProgress;
import org.springframework.stereotype.Repository;

@Repository
public class CsrFormProgressJpaDao extends AbstractJpaDao<CsrFormProgress, CsrFormPK> implements ICsrFormProgressDao {

}
