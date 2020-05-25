package org.mardep.ssrs.dao.sr;

import java.util.List;

import org.mardep.ssrs.dao.IBaseDao;
import org.mardep.ssrs.domain.sr.BuilderMaker;
import org.mardep.ssrs.domain.sr.BuilderMakerPK;

public interface IBuilderMakerDao extends IBaseDao<BuilderMaker, Long> {

	String findNames(String applNo);
	
	List<BuilderMaker> findByApplId(String applNo);

	BuilderMaker update(BuilderMaker entity);

}
