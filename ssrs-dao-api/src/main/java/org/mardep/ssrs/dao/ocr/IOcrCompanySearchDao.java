package org.mardep.ssrs.dao.ocr;

import java.util.List;

import org.mardep.ssrs.dao.IBaseDao;
import org.mardep.ssrs.domain.ocr.OcrEntityCompanySearch;

public interface IOcrCompanySearchDao extends IBaseDao<OcrEntityCompanySearch, Integer>{
	List<OcrEntityCompanySearch> getAll();
	OcrEntityCompanySearch getByCrNumber(String crNumber);
	//void save(OcrEntityCompanySearch entity);
}
