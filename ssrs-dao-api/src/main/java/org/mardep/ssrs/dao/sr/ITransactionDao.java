package org.mardep.ssrs.dao.sr;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.mardep.ssrs.dao.IBaseDao;
import org.mardep.ssrs.domain.sr.Transaction;

public interface ITransactionDao extends IBaseDao<Transaction, Long> {

	Transaction save(String applNo, String code, Transaction tx);

	/**
	 * 
	 * Find Transaction for Mortgage:
	 *  - Code ==33
	 * 
	 * @param applNo
	 * @return
	 */
	List<Transaction> findForMortgage(String applNo);

	List<Map<String, Object>> mortgageTransactionsReport(Date start, Date end);

}
