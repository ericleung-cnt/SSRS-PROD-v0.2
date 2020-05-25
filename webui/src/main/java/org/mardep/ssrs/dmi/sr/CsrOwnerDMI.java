package org.mardep.ssrs.dmi.sr;

import java.util.List;
import java.util.Map;

import org.mardep.ssrs.dao.sr.ICsrFormOwnerDao;
import org.mardep.ssrs.domain.sr.CsrFormOwner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class CsrOwnerDMI extends AbstractSrDMI<CsrFormOwner> {
	
	@Autowired
	ICsrFormOwnerDao dao;
	
	@Override
	public DSResponse fetch(CsrFormOwner entity, DSRequest dsRequest){
		//return super.fetch(entity, dsRequest);
		Map suppliedValues = dsRequest.getClientSuppliedValues();
		String imoNo = (String)suppliedValues.get("imoNo");
		String formSeq = (String)suppliedValues.get("formSeq");
		List<CsrFormOwner> ownerList = dao.get(imoNo, Integer.parseInt(formSeq));
		DSResponse dsResponse = new DSResponse();
		dsResponse.setData(ownerList);
		dsResponse.setStatus(DSResponse.STATUS_SUCCESS);
		return dsResponse;
	}

	@Override
	public DSResponse update(CsrFormOwner entity, DSRequest dsRequest) throws Exception {	
		return super.update(entity, dsRequest);
		//DSResponse dsResponse = new DSResponse();
		//dsResponse.setStatus(DSResponse.STATUS_SUCCESS);
		//return dsResponse;
	}

//	@Override
//	public DSResponse add(CsrFormOwner entity, DSRequest dsRequest) throws Exception {
//		return super.add(entity, dsRequest);
//	}
//	
//	@Override
//	public DSResponse remove(CsrFormOwner entity, DSRequest dsRequest) {
//		return super.remove(entity, dsRequest);
//	}

}
