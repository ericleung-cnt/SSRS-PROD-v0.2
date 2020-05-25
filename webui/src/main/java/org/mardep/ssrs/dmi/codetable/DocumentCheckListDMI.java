package org.mardep.ssrs.dmi.codetable;

import org.mardep.ssrs.domain.codetable.DocumentCheckList;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class DocumentCheckListDMI extends AbstractCodeTableDMI<DocumentCheckList> {

	@Override
	public DSResponse fetch(DocumentCheckList entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(DocumentCheckList entity, DSRequest dsRequest) throws Exception {
		return super.update(entity, dsRequest);
	}
	
	@Override
	public DSResponse add(DocumentCheckList entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
	
}
