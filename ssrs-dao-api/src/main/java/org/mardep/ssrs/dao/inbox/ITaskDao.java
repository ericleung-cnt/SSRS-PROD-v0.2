package org.mardep.ssrs.dao.inbox;

import java.util.List;

import org.mardep.ssrs.dao.IBaseDao;
import org.mardep.ssrs.domain.inbox.Task;

public interface ITaskDao extends IBaseDao<Task, Long> {

	List<Task> findPending(Task task);

	void updateParam3(Long taskId, String param3);

}
