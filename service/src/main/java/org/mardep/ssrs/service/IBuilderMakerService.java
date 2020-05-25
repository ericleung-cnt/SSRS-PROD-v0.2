package org.mardep.ssrs.service;

import java.util.List;

import org.mardep.ssrs.domain.sr.BuilderMaker;

public interface IBuilderMakerService {
	BuilderMaker insert(BuilderMaker entity);

	List<BuilderMaker> findByApplId(String applNo);
}
