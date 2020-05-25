package org.mardep.ssrs.dns.test;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.mardep.ssrs.dao.Criteria;
import org.mardep.ssrs.dao.PagingCriteria;
import org.mardep.ssrs.dao.SortByCriteria;
import org.mardep.ssrs.dao.dns.ISoapMessageOutDao;
import org.mardep.ssrs.domain.dns.SoapMessageOut;
import org.mardep.ssrs.domain.sr.Transaction;

public class SoapMessageOutMockDaoImpl implements ISoapMessageOutDao {

	@Override
	public SoapMessageOut findById(Long id)  {
		SoapMessageOut out = new SoapMessageOut();
		out.setId(-1L);
		return out;
	}

	@Override
	public SoapMessageOut save(SoapMessageOut entity)  {
		SoapMessageOut out = new SoapMessageOut();
		out.setId(-1L);
		return out;
	}

	@Override
	public List<SoapMessageOut> findAll()  {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long findByPaging(Map<String, Criteria> map, SortByCriteria sortByCriteria, PagingCriteria pagingCriteria,
			List<SoapMessageOut> resultList)  {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long findByPaging(Map<String, Criteria> map, SortByCriteria sortByCriteria, PagingCriteria pagingCriteria,
			List<SoapMessageOut> resultList, boolean checkCount)  {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SoapMessageOut> findByCriteria(SoapMessageOut entity)  {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long count(Map<String, Criteria> map)  {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SoapMessageOut findByControlId(Long controlId)  {
		SoapMessageOut out = new SoapMessageOut();
		out.setId(-1L);
		return out;
	}

	@Override
	public SoapMessageOut readOne()  {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SoapMessageOut> findListByControlId(Long controlId)
			 {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int delete(Class<SoapMessageOut> clazz, Long id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int delete(SoapMessageOut AbstractPersistentEntity) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public SoapMessageOut findLatestBySeafarerId(String seafarerId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SoapMessageOut> findResponseAfter(Date date) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveHistory(String applNo, Transaction tx) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<SoapMessageOut> findHistory(String applNo, Date txDate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteHistory(String applNo, Transaction tx) {
		// TODO Auto-generated method stub

	}

}
