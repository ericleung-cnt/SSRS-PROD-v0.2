package org.mardep.ssrs.service;

import java.util.List;

import org.mardep.ssrs.domain.inbox.Task;

public interface IInboxService extends IBaseService {

	Task[] startWorkflow(String name, String param1, String param2, String param3, String properties);

	/**
	 * @param id
	 * @param action
	 * @param properties
	 * @return tasks produced by the action
	 */
	Task[] proceed(Long id, String action, String properties);
	Task[] proceedWithResetParam2(Long id, String action, String properties, String param2);
	
	/**
	 * @param id
	 * @return task by id
	 */
	Task getTask(Long id);

	/**
	 * Get allow actions by configuration
	 * @param id task Id
	 * @return
	 */
	List<String> getActions(Long id);

	Task[] proceedWithNewParam2(Long id, String action, String office, String properties);

	Task[] proceedWithNewParam3(Long id, String action, String office, String corState, String properties);

	void updateParam3(Long taskId, String param3);

}
