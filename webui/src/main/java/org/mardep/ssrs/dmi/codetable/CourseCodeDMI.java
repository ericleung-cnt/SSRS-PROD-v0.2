package org.mardep.ssrs.dmi.codetable;

import org.mardep.ssrs.domain.codetable.CourseCode;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class CourseCodeDMI extends AbstractCodeTableDMI<CourseCode> {

	@Override
	public DSResponse fetch(CourseCode entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(CourseCode entity, DSRequest dsRequest) throws Exception {
//		entity.setFeeCode(null);
		return super.update(entity, dsRequest);
	}
	
	@Override
	public DSResponse add(CourseCode entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
	
}
