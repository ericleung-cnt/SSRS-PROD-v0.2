package org.mardep.ssrs.dns.test;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.mardep.ssrs.dao.Criteria;
import org.mardep.ssrs.dao.PagingCriteria;
import org.mardep.ssrs.dao.SortByCriteria;
import org.mardep.ssrs.dao.dns.ISoapMessageInDao;
import org.mardep.ssrs.domain.dns.SoapMessageIn;
import org.mardep.ssrs.domain.sr.Transaction;

public class SoapMessageInMockDaoImpl implements ISoapMessageInDao {

	@Override
	public SoapMessageIn findById(Long id) {
		SoapMessageIn in = new SoapMessageIn();
		in.setId(-1L);
		return in;
	}

	@Override
	public SoapMessageIn save(SoapMessageIn entity){
		SoapMessageIn in = new SoapMessageIn();
		in.setId(-1L);
		return in;
	}


	@Override
	public List<SoapMessageIn> findAll() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long findByPaging(Map<String, Criteria> map, SortByCriteria sortByCriteria, PagingCriteria pagingCriteria,
			List<SoapMessageIn> resultList) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long findByPaging(Map<String, Criteria> map, SortByCriteria sortByCriteria, PagingCriteria pagingCriteria,
			List<SoapMessageIn> resultList, boolean checkCount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SoapMessageIn> findByCriteria(SoapMessageIn entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long count(Map<String, Criteria> map) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int delete(Class<SoapMessageIn> clazz, Long id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int delete(SoapMessageIn AbstractPersistentEntity) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public SoapMessageIn findLatestBySeafarerId(String seafarerId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveHistory(String applNo, Transaction tx) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<SoapMessageIn> findHistory(String applNo, Date txDate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteHistory(String applNo, Transaction tx) {
		// TODO Auto-generated method stub

	}

}
