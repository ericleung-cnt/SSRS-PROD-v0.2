package org.mardep.ssrs.dmi.codetable;

import org.mardep.ssrs.domain.codetable.ClassSociety;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class ClassSocietyDMI extends AbstractCodeTableDMI<ClassSociety> {

	@Override
	public DSResponse fetch(ClassSociety entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(ClassSociety entity, DSRequest dsRequest) throws Exception {
		return super.update(entity, dsRequest);
	}

	@Override
	public DSResponse add(ClassSociety entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}

}
