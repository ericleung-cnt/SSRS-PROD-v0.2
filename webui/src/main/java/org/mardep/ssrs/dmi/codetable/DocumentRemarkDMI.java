package org.mardep.ssrs.dmi.codetable;

import java.util.List;
import java.util.Map;

import org.mardep.ssrs.domain.codetable.DocumentRemark;
import org.mardep.ssrs.service.IDocRemarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class DocumentRemarkDMI extends AbstractCodeTableDMI<DocumentRemark> {

	@Autowired
	IDocRemarkService remarkSvc;
	
	@Override
	public DSResponse fetch(DocumentRemark entity, DSRequest dsRequest){
		Map suppliedValues = dsRequest.getClientSuppliedValues();
		DSResponse dsResponse = new DSResponse();
		if (suppliedValues==null || suppliedValues.isEmpty()) {
			return super.fetch(entity, dsRequest);
		} else if(entity!=null && "ACD".equalsIgnoreCase(entity.getRemarkGroup())){
			List<DocumentRemark> remarkList = remarkSvc.getByGroup("ACD");
			dsResponse.setData(remarkList);
			dsResponse.setStatus(DSResponse.STATUS_SUCCESS);
			return dsResponse;			
		} else {
			String group = (String)suppliedValues.get("remarkGroup");
//			DSResponse dsResponse = new DSResponse();
			
			//List<DocumentRemark> remarkList = remarkSvc.getByGroup("CSR");
			List<DocumentRemark> remarkList = remarkSvc.getByGroup(group);
			dsResponse.setData(remarkList);
			dsResponse.setStatus(DSResponse.STATUS_SUCCESS);
			return dsResponse;			
		}
	}

	@Override
	public DSResponse update(DocumentRemark entity, DSRequest dsRequest) throws Exception {
		return super.update(entity, dsRequest);
	}
	
	@Override
	public DSResponse add(DocumentRemark entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
}
