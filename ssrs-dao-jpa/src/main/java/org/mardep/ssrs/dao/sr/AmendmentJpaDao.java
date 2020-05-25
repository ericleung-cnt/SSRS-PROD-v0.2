package org.mardep.ssrs.dao.sr;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.domain.sr.Amendment;
import org.springframework.stereotype.Repository;

@Repository
public class AmendmentJpaDao extends AbstractJpaDao<Amendment, Long> implements IAmendmentDao {

}
