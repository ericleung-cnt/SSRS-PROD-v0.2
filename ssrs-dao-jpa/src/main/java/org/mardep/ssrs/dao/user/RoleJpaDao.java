package org.mardep.ssrs.dao.user;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.domain.user.Role;
import org.springframework.stereotype.Repository;

@Repository
public class RoleJpaDao extends AbstractJpaDao<Role, Long> implements IRoleDao {

}
