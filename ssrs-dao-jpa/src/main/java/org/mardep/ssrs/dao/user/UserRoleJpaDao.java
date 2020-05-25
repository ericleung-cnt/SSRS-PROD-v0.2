package org.mardep.ssrs.dao.user;

import java.util.List;

import javax.persistence.Query;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.domain.user.UserRole;
import org.mardep.ssrs.domain.user.UserRolePK;
import org.springframework.stereotype.Repository;

@Repository
public class UserRoleJpaDao extends AbstractJpaDao<UserRole, UserRolePK> implements IUserRoleDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<UserRole> findUserRoleByUserId(String userId) {
		logger.debug("#findUserRoleByUserId:{}", userId);
		StringBuffer sb = new StringBuffer();
		sb.append("select ur from UserRole ur left join fetch ur.role where ur.userId =:userId ");
		Query query = em.createQuery(sb.toString());
		query.setParameter("userId", userId);
		return query.getResultList();
	}
}
