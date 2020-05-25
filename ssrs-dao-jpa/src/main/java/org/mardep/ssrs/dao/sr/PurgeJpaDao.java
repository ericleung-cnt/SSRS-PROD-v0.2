package org.mardep.ssrs.dao.sr;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.domain.sr.Purge;
import org.springframework.stereotype.Repository;

@Repository
public class PurgeJpaDao extends AbstractJpaDao<Purge, String> implements IPurgeDao {

}
