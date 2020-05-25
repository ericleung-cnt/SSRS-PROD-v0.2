package org.mardep.ssrs.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.transaction.Transactional;

import org.mardep.ssrs.dao.Criteria;
import org.mardep.ssrs.dao.PagingCriteria;
import org.mardep.ssrs.dao.SortByCriteria;
import org.mardep.ssrs.dao.inbox.ITaskDao;
import org.mardep.ssrs.domain.inbox.Task;
import org.mardep.ssrs.domain.user.SystemFunc;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class InboxService extends AbstractService implements IInboxService{

	@Autowired
	ITaskDao dao;
	private Properties prop;
	@Autowired
	private IUserService userService;

	public InboxService() throws IOException {
		prop = new Properties();
		try (InputStream resource = getClass().getResourceAsStream("/server/inbox.properties")) {
			prop.load(resource);
		}
	}

	@Override
	public Task[] startWorkflow(String name, String param1, String param2, String param3, String properties) {
		String tasks = (String) prop.get(name +"$");
		ArrayList<Object> created = new ArrayList<>();
		for (String taskName : tasks.split("\\,")) {
			Task task = new Task();
			task.setName(taskName);
			task.setParam1(param1);
			task.setParam2(param2);
			task.setParam3(param3);
			List<Task> result = dao.findPending(task);
			if (!result.isEmpty()) {
				throw new IllegalArgumentException("Duplicate workflow task exists");
			}
			task.setProperties(properties);
			created.add(dao.save(task));
		}
		return created.toArray(new Task[created.size()]);
	}

	@Override
	public Task[] proceedWithResetParam2(Long id, String action, String properties, String param2) {
		Task current = dao.findById(id);
		String nextSteps = (String) prop.get(current.getName() + "." + action);
		if (nextSteps == null) {
			throw new IllegalArgumentException("action is not allowed " + action);
		}
		ArrayList<Object> created = new ArrayList<>();
		if (nextSteps.length() > 0) {
			for (String taskName : nextSteps.split("\\,")) {
				Task task = new Task();
				task.setName(taskName);
				task.setParam1(current.getParam1());
				//task.setParam2(current.getParam2());
				task.setParam2(param2);
				task.setParam3(current.getParam3());
				task.setProperties(properties);
				task.setParentId(id);
				created.add(dao.save(task));
			}
		}
		current.setActionPerformed(action);
		current.setActionBy(UserContextThreadLocalHolder.getCurrentUserId());
		current.setActionDate(new Date());
		dao.save(current);
		return created.toArray(new Task[created.size()]);
	}

	@Override
	public Task[] proceed(Long id, String action, String properties) {
		Task current = dao.findById(id);
		String nextSteps = (String) prop.get(current.getName() + "." + action);
		if (nextSteps == null) {
			throw new IllegalArgumentException("action is not allowed " + action);
		}
		ArrayList<Object> created = new ArrayList<>();
		if (nextSteps.length() > 0) {
			for (String taskName : nextSteps.split("\\,")) {
				Task task = new Task();
				task.setName(taskName);
				task.setParam1(current.getParam1());
				task.setParam2(current.getParam2());
				task.setParam3(current.getParam3());
				
				List<Task> result = dao.findPending(task);
				if (!result.isEmpty()) {
					throw new IllegalArgumentException("Duplicate workflow task exists");
				}
				
				task.setProperties(properties);
				task.setParentId(id);
				created.add(dao.save(task));
			}
		}
		current.setActionPerformed(action);
		current.setActionBy(UserContextThreadLocalHolder.getCurrentUserId());
		current.setActionDate(new Date());
		dao.save(current);
		return created.toArray(new Task[created.size()]);
	}

	@Override
	public Task[] proceedWithNewParam2(Long id, String action,String office, String properties) {
		Task current = dao.findById(id);
		current.setParam2(office);
		return proceed(id,action,properties);
	}

	@Override
	public Task[] proceedWithNewParam3(Long id, String action,String office, String corState, String properties) {
		Task current = dao.findById(id);
		current.setParam2(office);
		current.setParam3(corState);
		return proceed(id,action,properties);
	}
	
	@Override
	public Task getTask(Long id) {
		return dao.findById(id);
	}

	@Override
	public List<String> getActions(Long id) {
		List<String> actions = new ArrayList<>();
		String prefix = getTask(id).getName() +".";
		for (Object key : prop.keySet()) {
			if (((String) key).startsWith(prefix)) {
				actions.add(((String) key).substring(prefix.length()));
			}
		}
		return actions;
	}

	@Override
	public <T> Long findByPaging(Map<String, Criteria> map, SortByCriteria sortByCriteria,
			PagingCriteria pagingCriteria, List<T> resultList, Class<T> requiredType) {
		map.put("actionBy", new Criteria("actionBy", null));
		String userId = UserContextThreadLocalHolder.getCurrentUserId();
		List<SystemFunc> funcs = userService.findSystemFuncByUserId(userId);
		List<String> names = new ArrayList<>();
		for (SystemFunc func : funcs) {
			if (func.getKey().startsWith("INBOX_")) {
				names.add(func.getKey().substring(6));
			}
		}
		if (map.get("name") == null) {
			map.put("name", new Criteria("name", names));
		}
		return super.findByPaging(map, sortByCriteria, pagingCriteria, resultList, requiredType);
	}

	@Override
	public void updateParam3(Long taskId, String param3) {
		dao.updateParam3(taskId, param3);
	}
}