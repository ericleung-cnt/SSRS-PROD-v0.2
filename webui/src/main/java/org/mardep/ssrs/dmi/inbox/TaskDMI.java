package org.mardep.ssrs.dmi.inbox;

import java.util.List;

import org.mardep.ssrs.dmi.AbstractDMI;
import org.mardep.ssrs.domain.inbox.Task;
import org.mardep.ssrs.service.IBaseService;
import org.mardep.ssrs.service.IInboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class TaskDMI extends AbstractDMI<Task>{

	@Autowired
	IInboxService inboxService;

	public TaskDMI() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected IBaseService getBaseService() {
		return inboxService;
	}

	@Override
	public DSResponse fetch(Task entity, DSRequest dsRequest) {
		if ("taskDS_fetchActions".equals(dsRequest.getOperationId())) {
			List<String> actions = inboxService.getActions(entity.getId());
			return new DSResponse(actions, DSResponse.STATUS_SUCCESS);
		} else {
			return super.fetch(entity, dsRequest);
		}
	}

	@Override
	public DSResponse update(Task entity, DSRequest dsRequest) throws Exception {
		// TODO Auto-generated method stub
		return super.update(entity, dsRequest);
	}

	@Override
	public DSResponse remove(Task entity, DSRequest dsRequest) throws Exception{
		// TODO Auto-generated method stub
		return super.remove(entity, dsRequest);
	}

	@Override
	public DSResponse add(Task entity, DSRequest dsRequest) throws Exception {
		// TODO Auto-generated method stub
		return super.add(entity, dsRequest);
	}


}
