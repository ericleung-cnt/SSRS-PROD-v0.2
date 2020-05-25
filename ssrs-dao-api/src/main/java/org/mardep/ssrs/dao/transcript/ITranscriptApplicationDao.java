package org.mardep.ssrs.dao.transcript;

import java.util.List;

import org.mardep.ssrs.dao.IBaseDao;
import org.mardep.ssrs.domain.entity.transcript.EntityTranscriptApplication;

public interface ITranscriptApplicationDao extends IBaseDao<EntityTranscriptApplication, Integer> {
	public List<EntityTranscriptApplication> getAll();
	public EntityTranscriptApplication get(Integer transcriptApplicationId);
	
	public EntityTranscriptApplication save(EntityTranscriptApplication entity);
}
