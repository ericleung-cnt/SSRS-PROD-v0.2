package org.mardep.ssrs.dao.codetable;

import java.util.List;

import javax.persistence.Query;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.domain.codetable.UserGroup2;
import org.springframework.stereotype.Repository;

@Repository
public class UserGroup2JpaDao extends AbstractJpaDao<UserGroup2, Long> implements IUserGroup2Dao {

	@Override
	public List<UserGroup2> getAll() {
		// TODO Auto-generated method stub
		try {
			String sql = "select u from UserGroup2 u";
			Query query = em.createQuery(sql, UserGroup2.class);
			List<UserGroup2> resultList = query.getResultList();
			return resultList;
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			ex.printStackTrace();
			return null;		
		}
	}


}
