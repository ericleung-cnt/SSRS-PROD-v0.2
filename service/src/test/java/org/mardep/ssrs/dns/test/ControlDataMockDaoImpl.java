package org.mardep.ssrs.dns.test;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.mardep.ssrs.dao.Criteria;
import org.mardep.ssrs.dao.PagingCriteria;
import org.mardep.ssrs.dao.SortByCriteria;
import org.mardep.ssrs.dao.dns.IControlDataDao;
import org.mardep.ssrs.domain.dns.ControlAction;
import org.mardep.ssrs.domain.dns.ControlData;
import org.mardep.ssrs.domain.dns.ControlEntity;
import org.mardep.ssrs.domain.sr.Transaction;

public class ControlDataMockDaoImpl implements IControlDataDao {

	@Override
	public ControlData findById(Long id) {
		ControlData	c = new ControlData();
		return c;
	}

	@Override
	public ControlData save(ControlData entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int delete(Class<ControlData> clazz, Long id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int delete(ControlData AbstractPersistentEntity) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<ControlData> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ControlData> findByCriteria(ControlData entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long findByPaging(Map<String, Criteria> map, SortByCriteria sortByCriteria, PagingCriteria pagingCriteria,
			List<ControlData> resultList) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long findByPaging(Map<String, Criteria> map, SortByCriteria sortByCriteria, PagingCriteria pagingCriteria,
			List<ControlData> resultList, boolean checkCount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long count(Map<String, Criteria> map) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ControlData findLatestBySeafarerId(String seafarerId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ControlData readOne() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ControlData> readForScheduler() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long findCountBy(ControlEntity ce, ControlAction ca, String entityId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ControlData find(ControlEntity ce, ControlAction ca, String entityId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveHistory(String applNo, Transaction tx) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<ControlData> findHistory(String applNo, Date txDate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteHistory(String applNo, Transaction tx) {
		// TODO Auto-generated method stub

	}



}
