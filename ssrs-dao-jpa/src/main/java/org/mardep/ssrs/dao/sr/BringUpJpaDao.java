package org.mardep.ssrs.dao.sr;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.domain.sr.BringUp;
import org.mardep.ssrs.domain.sr.BringUpPK;
import org.springframework.stereotype.Repository;

@Repository
public class BringUpJpaDao extends AbstractJpaDao<BringUp, BringUpPK> implements IBringUpDao {

}
