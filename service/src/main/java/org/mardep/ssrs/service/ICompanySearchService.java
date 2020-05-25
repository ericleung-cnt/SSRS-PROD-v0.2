package org.mardep.ssrs.service;

import java.util.List;

import org.mardep.ssrs.domain.ocr.OcrEntityCompanySearch;

public interface ICompanySearchService extends IBaseService{
	List<OcrEntityCompanySearch> getAll();
	//void update(OcrEntityCompanySearch entity);
}
