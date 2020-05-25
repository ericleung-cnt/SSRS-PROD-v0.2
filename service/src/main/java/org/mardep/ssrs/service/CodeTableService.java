package org.mardep.ssrs.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.mardep.ssrs.dao.codetable.IShipManagerDao;
import org.mardep.ssrs.dao.user.IFuncEntitleDao;
import org.mardep.ssrs.dao.user.IUserRoleDao;
import org.mardep.ssrs.domain.AbstractPersistentEntity;
import org.mardep.ssrs.domain.codetable.ShipManager;
import org.mardep.ssrs.domain.user.FuncEntitle;
import org.mardep.ssrs.domain.user.FuncEntitlePK;
import org.mardep.ssrs.domain.user.Role;
import org.mardep.ssrs.domain.user.User;
import org.mardep.ssrs.domain.user.UserRole;
import org.mardep.ssrs.domain.user.UserRolePK;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * <li>PRG-GEN-***
 * 
 * 
 * @author Leo.LIANG
 *
 */
@Primary
@Service("codeTableService")
public class CodeTableService extends AbstractService implements ICodeTableService{

	@Autowired
	IUserRoleDao userRoleDao;
	
	@Autowired
	IFuncEntitleDao funcEntitleDao;

	@Autowired
	IShipManagerDao shipManagerDao;
	
	@Override
	@Transactional(value=TxType.REQUIRED)
	public <T> T save(AbstractPersistentEntity entity) {

		if(entity instanceof User){
			saveUser((User)entity);
		}else if(entity instanceof Role){
			saveRole((Role)entity);
		}
		return super.save(entity);
	}
	
	@Override
	public ShipManager findShipManagerByShipName(String name){
		return shipManagerDao.findByName(name);
	}

	private void saveUser(User user){
		final String userId = user.getId();
		final List<UserRole> userRoleList = userRoleDao.findUserRoleByUserId(userId);
		
		Set<Long> existRoleIds = new HashSet<Long>();
		Set<Long> newRoleIds = user.getRoleIds();
		userRoleList.forEach(ur->{existRoleIds.add(ur.getRoleId());});
		
		existRoleIds.forEach(id->{
			if(!newRoleIds.contains(id)){
				userRoleDao.delete(UserRole.class, new UserRolePK(userId, id));
			}
		});
		newRoleIds.forEach(id->{
			if(!existRoleIds.contains(id)){
				UserRole newUr = new UserRole();
				newUr.setUserId(userId);
				newUr.setRoleId(id);
				userRoleDao.save(newUr);
			}
		});
		
	}
	
	private void saveRole(Role role){
		final Long roleId = role.getId();
		final List<FuncEntitle> funcEntitleList = funcEntitleDao.findByRoleId(roleId);
		
		Set<Long> newFuncIds = role.getFuncIds();
		Set<Long> existFuncIds = funcEntitleList.stream().map(fe->fe.getFuncId()).collect(Collectors.toSet());
		
		existFuncIds.forEach(id->{
			if(!newFuncIds.contains(id)){
				funcEntitleDao.delete(FuncEntitle.class, new FuncEntitlePK(roleId, id));
			}
		});
		newFuncIds.forEach(id->{
			if(!existFuncIds.contains(id)){
				FuncEntitle newFe = new FuncEntitle();
				newFe.setFuncId(id);
				newFe.setRoleId(roleId);
				funcEntitleDao.save(newFe);
			}
		});
		
	}
	
}
