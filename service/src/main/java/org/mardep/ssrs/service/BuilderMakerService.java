package org.mardep.ssrs.service;

import java.util.List;

import javax.transaction.Transactional;

import org.mardep.ssrs.dao.sr.IBuilderMakerDao;
import org.mardep.ssrs.domain.sr.BuilderMaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class BuilderMakerService implements IBuilderMakerService {

	@Autowired
	IBuilderMakerDao bmDao;
	
	@Override
	public BuilderMaker insert(BuilderMaker entity) {
		// TODO Auto-generated method stub
		BuilderMaker savedEntity = bmDao.save(entity);
		return savedEntity;
	}
	
	@Override
	public List<BuilderMaker> findByApplId(String applNo) {
		return bmDao.findByApplId(applNo);
	}

}
