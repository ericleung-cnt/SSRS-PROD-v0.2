package org.mardep.ssrs.dao.user;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.domain.user.LdapUser;
import org.springframework.stereotype.Repository;

@Repository
public class LdapUserJpaDao extends AbstractJpaDao<LdapUser, String> implements ILdapUserDao {

}
