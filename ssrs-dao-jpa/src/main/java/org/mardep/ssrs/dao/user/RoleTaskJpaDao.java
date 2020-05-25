package org.mardep.ssrs.dao.user;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.domain.user.RoleTask;
import org.springframework.stereotype.Repository;

@Repository
public class RoleTaskJpaDao extends AbstractJpaDao<RoleTask, Long> implements IRoleTaskDao {

}
