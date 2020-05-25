package org.mardep.ssrs.dao.seafarer;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.seafarer.IEyeTestDao;
import org.mardep.ssrs.domain.seafarer.EyeTest;
import org.springframework.stereotype.Repository;

@Repository
public class EyeTestJpaDao extends AbstractJpaDao<EyeTest, Long> implements IEyeTestDao {

}
