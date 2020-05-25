package org.mardep.ssrs.dao.user;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.domain.user.FuncEntitle;
import org.mardep.ssrs.domain.user.FuncEntitlePK;
import org.mardep.ssrs.domain.user.SystemFunc;
import org.springframework.stereotype.Repository;

@Repository
public class FuncEntitleJpaDao extends AbstractJpaDao<FuncEntitle, FuncEntitlePK> implements IFuncEntitleDao {
	
	@SuppressWarnings("unchecked")
	@Override
	public List<SystemFunc> findSystemFuncByUserId(String userId){
		logger.debug("#findSystemFuncByUserId:{}", userId);
		StringBuffer sb = new StringBuffer();
		sb.append("select DISTINCT fe.systemFunc from FuncEntitle fe, UserRole ur ");
		sb.append("where fe.roleId = ur.roleId and ur.userId=:userId");
		Query query = em.createQuery(sb.toString());
		query.setParameter("userId", userId);
		List<SystemFunc> resultList = query.getResultList();
		return resultList;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<SystemFunc> findSystemFuncByRoleList(List<String> roleList){
		logger.debug("#findSystemFuncByRoleList:{}", roleList);
		List<SystemFunc> resultList = new ArrayList<SystemFunc>();
		if (roleList.size() <= 0){
			return resultList;
		}
		StringBuffer sb = new StringBuffer();
		sb.append("select DISTINCT fe.systemFunc from FuncEntitle fe, Role r ");
		sb.append("where fe.roleId = r.roleId and r.engDesc in :roleList");
		Query query = em.createQuery(sb.toString());
		query.setParameter("roleList", roleList);
		resultList = query.getResultList();

		return resultList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FuncEntitle> findByRoleId(Long roleId) {
		Query query = em.createQuery("select fe from FuncEntitle fe left join fetch fe.role where fe.roleId =:roleId ");
		query.setParameter("roleId", roleId);
		return query.getResultList();
	}
}
