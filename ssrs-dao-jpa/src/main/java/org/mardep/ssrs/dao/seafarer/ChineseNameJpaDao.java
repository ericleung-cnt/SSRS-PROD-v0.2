package org.mardep.ssrs.dao.seafarer;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.seafarer.IChineseNameDao;
import org.mardep.ssrs.domain.seafarer.ChineseName;
import org.springframework.stereotype.Repository;

@Repository
public class ChineseNameJpaDao extends AbstractJpaDao<ChineseName, String> implements IChineseNameDao {

}
