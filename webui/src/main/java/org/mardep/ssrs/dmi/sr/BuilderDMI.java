package org.mardep.ssrs.dmi.sr;

import org.mardep.ssrs.domain.sr.BuilderMaker;
import org.mardep.ssrs.service.IShipRegService;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class BuilderDMI extends AbstractSrDMI<BuilderMaker> {

	@Override
	public DSResponse fetch(BuilderMaker entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(BuilderMaker entity, DSRequest dsRequest) throws Exception {
		IShipRegService sr = (IShipRegService) getBaseService();
		Long taskId = (Long) dsRequest.getClientSuppliedValues().get("taskId");
		switch (dsRequest.getOperationId()) {
		case "builderDS_changeReceive":
			sr.receiveBuilderMakerChange(entity.getApplNo(), entity.getBuilderCode());
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		case "bmAccept":
			sr.acceptBuilderMakerChange(taskId);
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		case "bmWithdraw":
			sr.withdraw(entity, taskId);
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		case "bmApprove":
			sr.approveBuilderMakerChange(taskId);
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		case "bmReady":
			sr.updateBuilderMaker(entity);
			sr.crosscheckReadyBuilderMakerChange(taskId);
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		case "bmComplete":
			entity = sr.completeBuilderMakerChange(entity, taskId, getTx(dsRequest.getClientSuppliedValues()));
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		default:
			break;
		}
//		if (entity.getBuilderMakerId()==null) {
//			entity.setBuilderMakerId(-1L);
//		}
//		if (entity.getVersion()!=null) {
//			entity = sr.updateBuilderMaker(entity);
//			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
//		}
		return super.update(entity, dsRequest);
	}

	@Override
	public DSResponse add(BuilderMaker entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}

	@Override
	public DSResponse remove(BuilderMaker entity, DSRequest dsRequest) {
		shipRegService.removeBuilder(entity);
		return new DSResponse(DSResponse.STATUS_SUCCESS);
	}


}
