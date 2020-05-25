package org.mardep.ssrs.service;

import java.util.List;

import javax.transaction.Transactional;

import org.mardep.ssrs.dao.ocr.IOcrCompanySearchDao;
import org.mardep.ssrs.domain.ocr.OcrEntityCompanySearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class CompanySearchService extends AbstractService implements ICompanySearchService {

	@Autowired
	IOcrCompanySearchDao companySearchDao;

	@Override
	public List<OcrEntityCompanySearch> getAll() {
		return companySearchDao.getAll();
	}

//	@Override
//	public void update(OcrEntityCompanySearch entity) {
//		// TODO Auto-generated method stub
//		return companySearchDao.save(entity);
//	}

}
