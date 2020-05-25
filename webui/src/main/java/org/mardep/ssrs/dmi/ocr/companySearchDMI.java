package org.mardep.ssrs.dmi.ocr;

import java.util.List;

import org.mardep.ssrs.dmi.AbstractDMI;
import org.mardep.ssrs.domain.dn.DemandNoteHeader;
import org.mardep.ssrs.domain.ocr.OcrEntityCompanySearch;
import org.mardep.ssrs.service.IBaseService;
import org.mardep.ssrs.service.ICompanySearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;
import com.isomorphic.log.Logger;

@Component
public class companySearchDMI extends AbstractDMI<OcrEntityCompanySearch>{
	
	@Autowired
	ICompanySearchService companySearchService;
	
//	public DSResponse fetch() {
//		List<OcrEntityCompanySearch> lst = companySearchService.getAll();
//		DSResponse response = new DSResponse(lst);
//		return response;
//	}
	
	public DSResponse fetch(OcrEntityCompanySearch entity, DSRequest dsRequest) {
		return super.fetch(entity, dsRequest);
	}
	
	public DSResponse update(OcrEntityCompanySearch entity,  DSRequest dsRequest) {
		DSResponse dsResponse = new DSResponse();
		dsResponse.setStatus(DSResponse.STATUS_SUCCESS);
		try {
			return super.update(entity, dsRequest);
		} catch (Exception ex) {
			logger.error("Fail to update-{}, Exception-{}", new Object[]{entity, ex}, ex);
			return handleException(dsResponse, ex);
		}
	}

	@Override
	protected IBaseService getBaseService() {
		// TODO Auto-generated method stub
		return companySearchService;
	}
}
